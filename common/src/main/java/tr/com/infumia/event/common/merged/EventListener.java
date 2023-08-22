package tr.com.infumia.event.common.merged;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.event.common.EventExecutor;
import tr.com.infumia.event.common.EventManager;
import tr.com.infumia.event.common.Plugins;
import tr.com.infumia.event.common.Subscription;

@SuppressWarnings("unchecked")
final class EventListener<Event, Priority, Handled> implements Subscription {

  private final AtomicBoolean active = new AtomicBoolean(true);

  private final AtomicLong callCount = new AtomicLong();

  @NotNull
  private final Predicate<Handled> filter;

  @NotNull
  private final BiConsumer<Subscription, Handled> handler;

  @NotNull
  private final Map<
    Class<? extends Event>,
    MergedHandlerMapping<? extends Event, Priority, Handled>
  > mappings;

  @NotNull
  private final BiPredicate<Subscription, Handled> midExpiryTest;

  @NotNull
  private final BiPredicate<Subscription, Handled> postExpiryTest;

  @NotNull
  private final BiPredicate<Subscription, Handled> preExpiryTest;

  @NotNull
  private final Collection<EventExecutor<?>> registeredEvents = ConcurrentHashMap.newKeySet();

  EventListener(
    @NotNull final MergedSubscriptionBuilder.Get<Event, Priority, Handled> getter,
    @NotNull final BiConsumer<Subscription, Handled> handler
  ) {
    this.mappings = Collections.unmodifiableMap(getter.mappings());
    this.filter = getter.filter();
    this.preExpiryTest = getter.preExpiryTest();
    this.midExpiryTest = getter.midExpiryTest();
    this.postExpiryTest = getter.postExpiryTest();
    this.handler = handler;
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
    final EventManager<Object, Object> manager = Plugins.manager();
    for (final EventExecutor<?> event : this.registeredEvents) {
      manager.unregister(event);
    }
  }

  @Override
  public boolean closed() {
    return !this.active.get();
  }

  @NotNull
  Subscription register() {
    final IdentityHashMap<Class<?>, Priority> registered = new IdentityHashMap<>();
    this.mappings.forEach((eventClass, mapping) -> {
        final Priority priority = mapping.priority();
        final Priority existing = registered.put(eventClass, priority);
        if (existing != null) {
          if (existing != priority) {
            throw new RuntimeException(
              String.format(
                "Unable to register the same event with different priorities: %s",
                eventClass
              )
            );
          }
          return;
        }
        final Class<Event> cls = (Class<Event>) eventClass;
        final Executor executor = new Executor(cls);
        this.registeredEvents.add(Plugins.manager().register(cls, priority, executor));
      });
    return this;
  }

  private final class Executor implements EventExecutor<Event> {

    @NotNull
    private final Class<Event> eventClass;

    private final AtomicReference<Object> nativeExecutor = new AtomicReference<>();

    Executor(@NotNull final Class<Event> eventClass) {
      this.eventClass = eventClass;
    }

    @NotNull
    @Override
    public Class<? extends Event> eventClass() {
      return this.eventClass;
    }

    @Override
    public void execute(@NotNull final Event event) {
      final MergedHandlerMapping<Event, Priority, Handled> mapping = (MergedHandlerMapping<
          Event,
          Priority,
          Handled
        >) EventListener.this.mappings.get(event.getClass());
      if (mapping == null) {
        return;
      }
      if (!EventListener.this.active.get()) {
        Plugins.manager().unregister(this);
        return;
      }
      final Handled handledInstance = mapping.map(event);
      if (EventListener.this.preExpiryTest.test(EventListener.this, handledInstance)) {
        Plugins.manager().unregister(this);
        EventListener.this.active.set(false);
        return;
      }
      try {
        if (!EventListener.this.filter.test(handledInstance)) {
          return;
        }
        if (EventListener.this.midExpiryTest.test(EventListener.this, handledInstance)) {
          Plugins.manager().unregister(this);
          EventListener.this.active.set(false);
          return;
        }
        EventListener.this.handler.accept(EventListener.this, handledInstance);
        EventListener.this.callCount.incrementAndGet();
      } catch (final Throwable t) {
        mapping.failed(event, t);
      }
      if (EventListener.this.postExpiryTest.test(EventListener.this, handledInstance)) {
        Plugins.manager().unregister(this);
        EventListener.this.active.set(false);
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
  }
}
