package tr.com.infumia.event.common.single;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.event.common.EventExecutor;
import tr.com.infumia.event.common.Plugins;
import tr.com.infumia.event.common.Subscription;

final class EventListener<Event, Priority> implements EventExecutor<Event>, Subscription {

  private final AtomicBoolean active = new AtomicBoolean(true);

  private final AtomicLong callCount = new AtomicLong();

  @Getter
  @NotNull
  private final Class<Event> eventClass;

  @NotNull
  private final BiConsumer<Event, Throwable> exceptionConsumer;

  @NotNull
  private final Predicate<Event> filter;

  private final boolean handleSubclasses;

  @NotNull
  private final BiConsumer<Subscription, Event> handler;

  @NotNull
  private final BiPredicate<Subscription, Event> midExpiryTest;

  private final AtomicReference<Object> nativeExecutor = new AtomicReference<>();

  @NotNull
  private final BiPredicate<Subscription, Event> postExpiryTest;

  @NotNull
  private final BiPredicate<Subscription, Event> preExpiryTest;

  @NotNull
  private final Priority priority;

  EventListener(
    @NotNull final SingleSubscriptionBuilder.Get<Event, Priority> getter,
    @NotNull final BiConsumer<Subscription, Event> handler
  ) {
    this.eventClass = getter.eventClass();
    this.priority = getter.priority();
    this.exceptionConsumer = getter.exceptionConsumer();
    this.handleSubclasses = getter.isHandleSubclasses();
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
    Plugins.manager().unregister(this);
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
      Plugins.manager().unregister(this);
      return;
    }
    final Event eventInstance = this.eventClass.cast(event);
    if (this.preExpiryTest.test(this, eventInstance)) {
      Plugins.manager().unregister(this);
      this.active.set(false);
      return;
    }
    try {
      if (!this.filter.test(eventInstance)) {
        return;
      }
      if (this.midExpiryTest.test(this, eventInstance)) {
        Plugins.manager().unregister(this);
        this.active.set(false);
        return;
      }
      this.handler.accept(this, eventInstance);
      this.callCount.incrementAndGet();
    } catch (final Throwable t) {
      this.exceptionConsumer.accept(eventInstance, t);
    }
    if (this.postExpiryTest.test(this, eventInstance)) {
      Plugins.manager().unregister(this);
      this.active.set(false);
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

  @NotNull
  Subscription register() {
    Plugins.manager().register(this.eventClass, this.priority, this);
    return this;
  }
}
