package tr.com.infumia.event.common.merged;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.event.common.EventExecutor;
import tr.com.infumia.event.common.Plugins;
import tr.com.infumia.event.common.Subscription;

/**
 * a class that represents event listener.
 */
@Accessors(fluent = true)
@SuppressWarnings("unchecked")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
final class EventListener<Plugin, Event, Priority, Handled>
  implements Subscription {

  /**
   * the active.
   */
  AtomicBoolean active = new AtomicBoolean(true);

  /**
   * the call count.
   */
  AtomicLong callCount = new AtomicLong();

  /**
   * the exception consumer.
   */
  @NotNull
  BiConsumer<Event, Throwable> exceptionConsumer;

  /**
   * the filters.
   */
  @NotNull
  Predicate<Handled>[] filters;

  /**
   * the handlers.
   */
  @NotNull
  BiConsumer<Subscription, Handled>[] handlers;

  /**
   * the mappings.
   */
  @NotNull
  Map<Class<? extends Event>, MergedHandlerMapping<? extends Event, Priority, Handled>> mappings;

  /**
   * the mid-expiry test.
   */
  @NotNull
  BiPredicate<Subscription, Handled>[] midExpiryTests;

  /**
   * the plugin.
   */
  @NotNull
  Plugin plugin;

  /**
   * tee post expiry tests.
   */
  @NotNull
  BiPredicate<Subscription, Handled>[] postExpiryTests;

  /**
   * the pre expiry tests.
   */
  @NotNull
  BiPredicate<Subscription, Handled>[] preExpiryTests;

  /**
   * the registered events.
   */
  @NotNull
  Collection<EventExecutor<?>> registeredEvents = ConcurrentHashMap.newKeySet();

  /**
   * ctor.
   *
   * @param plugin the plugin.
   * @param getter the getter.
   * @param handlers the handlers.
   */
  EventListener(
    @NotNull final Plugin plugin,
    @NotNull final MergedSubscriptionBuilder.Get<Event, Priority, Handled> getter,
    @NotNull final List<BiConsumer<Subscription, Handled>> handlers
  ) {
    this.mappings = Collections.unmodifiableMap(getter.mappings());
    this.exceptionConsumer = getter.exceptionConsumer();
    this.filters = getter.filters().toArray(Predicate[]::new);
    this.preExpiryTests = getter.preExpiryTests().toArray(BiPredicate[]::new);
    this.midExpiryTests = getter.midExpiryTests().toArray(BiPredicate[]::new);
    this.postExpiryTests = getter.postExpiryTests().toArray(BiPredicate[]::new);
    this.handlers = handlers.toArray(BiConsumer[]::new);
    this.plugin = plugin;
  }

  @Override
  public boolean active() {
    return this.active.get();
  }

  @Override
  public long callCounter() {
    return this.callCount.get();
  }

  @Override
  public void unregister() {
    if (!this.active.getAndSet(false)) {
      return;
    }
    final var manager = Plugins.manager();
    for (final var event : this.registeredEvents) {
      manager.unregister(this.plugin, event);
    }
  }

  @Override
  public boolean closed() {
    return !this.active.get();
  }

  /**
   * registers the event.
   */
  @NotNull
  Subscription register() {
    final var registered = new IdentityHashMap<Class<?>, Priority>();
    this.mappings.forEach((eventClass, mapping) -> {
        final var priority = mapping.priority();
        final var existing = registered.put(eventClass, priority);
        if (existing != null) {
          if (existing != priority) {
            throw new RuntimeException(
              "Unable to register the same event with different priorities: %s --> %s".formatted(
                  eventClass,
                  eventClass
                )
            );
          }
          return;
        }
        final var cls = (Class<Event>) eventClass;
        final var executor = new Executor<>(cls, this);
        this.registeredEvents.add(
            Plugins.manager().register(this.plugin, cls, priority, executor)
          );
      });
    return this;
  }

  /**
   * a class that represents event executors.
   */
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
  private static final class Executor<Plugin, Event, Priority, Handled>
    implements EventExecutor<Event> {

    /**
     * the event class.
     */
    @Getter
    @NotNull
    Class<Event> eventClass;

    /**
     * the native executor.
     */
    AtomicReference<Object> nativeExecutor = new AtomicReference<>();

    /**
     * the parent.
     */
    @NotNull
    EventListener<Plugin, Event, Priority, Handled> parent;

    @Override
    public void execute(@NotNull final Event event) {
      final var mapping = this.parent.mappings.get(event.getClass());
      if (mapping == null) {
        return;
      }
      final var function = mapping.mapping();
      if (!this.parent.active.get()) {
        Plugins.manager().unregister(this.parent.plugin, this);
        return;
      }
      final var handledInstance = function.apply(event);
      for (final var test : this.parent.preExpiryTests) {
        if (test.test(this.parent, handledInstance)) {
          Plugins.manager().unregister(this.parent.plugin, this);
          this.parent.active.set(false);
          return;
        }
      }
      try {
        for (final var filter : this.parent.filters) {
          if (!filter.test(handledInstance)) {
            return;
          }
        }
        for (final var test : this.parent.midExpiryTests) {
          if (test.test(this.parent, handledInstance)) {
            Plugins.manager().unregister(this.parent.plugin, this);
            this.parent.active.set(false);
            return;
          }
        }
        for (final var handler : this.parent.handlers) {
          handler.accept(this.parent, handledInstance);
        }
        this.parent.callCount.incrementAndGet();
      } catch (final Throwable t) {
        this.parent.exceptionConsumer.accept(event, t);
      }
      for (final var test : this.parent.postExpiryTests) {
        if (test.test(this.parent, handledInstance)) {
          Plugins.manager().unregister(this.parent.plugin, this);
          this.parent.active.set(false);
          return;
        }
      }
    }

    @NotNull
    @Override
    public Object nativeExecutor() {
      return Objects.requireNonNull(
        this.nativeExecutor.get(),
        "native executor"
      );
    }

    @Override
    public void nativeExecutor(@NotNull final Object nativeExecutor) {
      this.nativeExecutor.set(nativeExecutor);
    }
  }
}
