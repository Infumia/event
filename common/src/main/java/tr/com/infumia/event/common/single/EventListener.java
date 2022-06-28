package tr.com.infumia.event.common.single;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.event.common.EventExecutor;
import tr.com.infumia.event.common.Plugins;
import tr.com.infumia.event.common.Subscription;

/**
 * a class that represents event listener.
 *
 * @param <Event> type of the event class.
 */
@Accessors(fluent = true)
@SuppressWarnings("unchecked")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
final class EventListener<Plugin, Event, Priority>
  implements EventExecutor<Event>, Subscription {

  /**
   * the active.
   */
  AtomicBoolean active = new AtomicBoolean(true);

  /**
   * the call count.
   */
  AtomicLong callCount = new AtomicLong();

  /**
   * the event class.
   */
  @Getter
  @NotNull
  Class<Event> eventClass;

  /**
   * the exception consumer.
   */
  @NotNull
  BiConsumer<Event, Throwable> exceptionConsumer;

  /**
   * the filters.
   */
  @NotNull
  Predicate<Event>[] filters;

  /**
   * the handle subclasses.
   */
  boolean handleSubclasses;

  /**
   * the handlers.
   */
  @NotNull
  BiConsumer<Subscription, Event>[] handlers;

  /**
   * the mid-expiry test.
   */
  @NotNull
  BiPredicate<Subscription, Event>[] midExpiryTests;

  /**
   * the native executor.
   */
  AtomicReference<Object> nativeExecutor = new AtomicReference<>();

  /**
   * the plugin.
   */
  @NotNull
  Plugin plugin;

  /**
   * tee post expiry tests.
   */
  @NotNull
  BiPredicate<Subscription, Event>[] postExpiryTests;

  /**
   * the pre expiry tests.
   */
  @NotNull
  BiPredicate<Subscription, Event>[] preExpiryTests;

  /**
   * the priority.
   */
  @NotNull
  Priority priority;

  /**
   * ctor.
   *
   * @param plugin the plugin.
   * @param getter the getter.
   * @param handlers the handlers.
   */
  EventListener(
    @NotNull final Plugin plugin,
    @NotNull final SingleSubscriptionBuilder.Get<Event, Priority> getter,
    @NotNull final List<BiConsumer<Subscription, Event>> handlers
  ) {
    this.plugin = plugin;
    this.eventClass = getter.eventClass();
    this.priority = getter.priority();
    this.exceptionConsumer = getter.exceptionConsumer();
    this.handleSubclasses = getter.isHandleSubclasses();
    this.filters = getter.filters().toArray(Predicate[]::new);
    this.preExpiryTests = getter.preExpiryTests().toArray(BiPredicate[]::new);
    this.midExpiryTests = getter.midExpiryTests().toArray(BiPredicate[]::new);
    this.postExpiryTests = getter.postExpiryTests().toArray(BiPredicate[]::new);
    this.handlers = handlers.toArray(BiConsumer[]::new);
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
    Plugins.manager().unregister(this.plugin, this);
  }

  @Override
  public boolean closed() {
    return !this.active.get();
  }

  @Override
  public void execute(@NotNull final Event event) {
    if (this.handleSubclasses) {
      if (!this.eventClass.isInstance(event)) {
        return;
      }
    } else if (event.getClass() != this.eventClass) {
      return;
    }
    if (this.closed()) {
      Plugins.manager().unregister(this.plugin, this);
      return;
    }
    final var eventInstance = this.eventClass.cast(event);
    for (final var test : this.preExpiryTests) {
      if (test.test(this, eventInstance)) {
        Plugins.manager().unregister(this.plugin, this);
        this.active.set(false);
        return;
      }
    }
    try {
      for (final var filter : this.filters) {
        if (!filter.test(eventInstance)) {
          return;
        }
      }
      for (final var test : this.midExpiryTests) {
        if (test.test(this, eventInstance)) {
          Plugins.manager().unregister(this.plugin, this);
          this.active.set(false);
          return;
        }
      }
      for (final var handler : this.handlers) {
        handler.accept(this, eventInstance);
      }
      this.callCount.incrementAndGet();
    } catch (final Throwable t) {
      this.exceptionConsumer.accept(eventInstance, t);
    }
    for (final var test : this.postExpiryTests) {
      if (test.test(this, eventInstance)) {
        Plugins.manager().unregister(this.plugin, this);
        this.active.set(false);
        return;
      }
    }
  }

  @NotNull
  @Override
  public Object nativeExecutor() {
    return Objects.requireNonNull(this.nativeExecutor.get(), "native executor");
  }

  @Override
  public void nativeExecutor(@NotNull final Object nativeExecutor) {
    this.nativeExecutor.set(nativeExecutor);
  }

  /**
   * registers the event.
   */
  @NotNull
  Subscription register() {
    Plugins
      .manager()
      .register(this.plugin, this.eventClass, this.priority, this);
    return this;
  }
}
