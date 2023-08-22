package tr.com.infumia.event.common;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public interface SubscriptionBuilder<
  Event,
  Sb extends Subscription,
  HandlerList extends FunctionalHandlerList<Event, Sb, HandlerList>,
  Slf extends SubscriptionBuilder<Event, Sb, HandlerList, Slf>
>
  extends Self<Slf> {
  @NotNull
  default HandlerList biConsumer(@NotNull final BiConsumer<Sb, Event> handler) {
    return this.handlers().biConsumer(handler);
  }

  @NotNull
  default Sb biHandler(@NotNull final BiConsumer<Sb, Event> handler) {
    return this.biConsumer(handler).register();
  }

  @NotNull
  default HandlerList consumer(@NotNull final Consumer<Event> handler) {
    return this.handlers().consumer(handler);
  }

  @NotNull
  default Slf expireAfter(@NotNull final Duration duration) {
    final long dur = duration.toNanos();
    if (dur < 1) {
      throw new IllegalArgumentException("duration is less than 1ms!");
    }
    final long expiry = Math.addExact(System.nanoTime(), dur);
    return this.expireIf((__, ___) -> System.nanoTime() > expiry, ExpiryTestStage.PRE);
  }

  @NotNull
  default Slf expireAfter(final long duration, @NotNull final TimeUnit unit) {
    return this.expireAfter(Duration.of(duration, Internal.toChronoUnit(unit)));
  }

  @NotNull
  default Slf expireAfter(final long maxCalls) {
    if (maxCalls < 1) {
      throw new IllegalArgumentException("maxCalls is less than 1!");
    }
    return this.expireIf(
        (handler, event) -> handler.callCounter() >= maxCalls,
        ExpiryTestStage.PRE,
        ExpiryTestStage.POST_HANDLE
      );
  }

  @NotNull
  default Slf expireIf(@NotNull final Predicate<Event> predicate) {
    return this.expireIf(
        (__, e) -> predicate.test(e),
        ExpiryTestStage.PRE,
        ExpiryTestStage.POST_HANDLE
      );
  }

  @NotNull
  Slf expireIf(@NotNull BiPredicate<Sb, Event> predicate, @NotNull ExpiryTestStage... testPoints);

  @NotNull
  Slf filter(@NotNull Predicate<Event> predicate);

  @NotNull
  default Slf filterNot(@NotNull final Predicate<Event> predicate) {
    return this.filter(Predicate.not(predicate));
  }

  @NotNull
  default Sb handler(@NotNull final Consumer<Event> handler) {
    return this.biHandler((__, e) -> handler.accept(e));
  }

  @NotNull
  default Sb handler(@NotNull final Runnable handler) {
    return this.handler(__ -> handler.run());
  }

  @NotNull
  HandlerList handlers();

  interface Get<Event, Sb extends Subscription> {
    @NotNull
    Predicate<Event> filter();

    @NotNull
    BiPredicate<Sb, Event> midExpiryTest();

    @NotNull
    BiPredicate<Sb, Event> postExpiryTest();

    @NotNull
    BiPredicate<Sb, Event> preExpiryTest();
  }

  @Getter
  abstract class Base<
    Event,
    Sb extends Subscription,
    HandlerList extends FunctionalHandlerList<Event, Sb, HandlerList>,
    Slf extends SubscriptionBuilder<Event, Sb, HandlerList, Slf>
  >
    implements SubscriptionBuilder<Event, Sb, HandlerList, Slf>, Get<Event, Sb> {

    @NotNull
    private Predicate<Event> filter = __ -> true;

    @NotNull
    private BiPredicate<Sb, Event> midExpiryTest = (__, ___) -> false;

    @NotNull
    private BiPredicate<Sb, Event> postExpiryTest = (__, ___) -> false;

    @NotNull
    private BiPredicate<Sb, Event> preExpiryTest = (__, ___) -> false;

    @NotNull
    @Override
    public final Slf expireIf(
      @NotNull final BiPredicate<Sb, Event> predicate,
      @NotNull final ExpiryTestStage... testPoints
    ) {
      for (final ExpiryTestStage testPoint : testPoints) {
        switch (testPoint) {
          case PRE:
            this.preExpiryTest = this.preExpiryTest.and(predicate);
            break;
          case POST_FILTER:
            this.midExpiryTest = this.midExpiryTest.and(predicate);
            break;
          case POST_HANDLE:
            this.postExpiryTest = this.postExpiryTest.and(predicate);
            break;
          default:
            throw new UnsupportedOperationException(testPoint + " not implemented!");
        }
      }
      return this.self();
    }

    @NotNull
    @Override
    public final Slf filter(@NotNull final Predicate<Event> predicate) {
      this.filter = this.filter.and(predicate);
      return this.self();
    }
  }
}
