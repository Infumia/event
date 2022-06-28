package tr.com.infumia.event.common;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

/**
 * an interface to determine subscription builders.
 *
 * @param <Plugin> type of the plugin class.
 * @param <Event> type of the event class.
 * @param <Sb> type of the subscription class.
 * @param <HandlerList> type of the handler list class.
 * @param <Slf> type of the self implementation class.
 */
public interface SubscriptionBuilder<
  Plugin,
  Event,
  Sb extends Subscription,
  HandlerList extends FunctionalHandlerList<Plugin, Event, Sb, HandlerList>,
  Slf extends SubscriptionBuilder<Plugin, Event, Sb, HandlerList, Slf>
>
  extends Self<Slf> {
  /**
   * builds and registers the event.
   *
   * @param plugin the plugin to register.
   * @param handler the handler to register.
   *
   * @return registered {@link Sb}.
   */
  @NotNull
  default Sb biHandler(
    @NotNull final Plugin plugin,
    @NotNull final BiConsumer<Sb, Event> handler
  ) {
    return this.handlers().biConsumer(handler).register(plugin);
  }

  /**
   * builds and registers the event.
   *
   * @param handler the handler to register.
   *
   * @return registered {@link Sb}.
   */
  @NotNull
  default Sb biHandler(@NotNull final BiConsumer<Sb, Event> handler) {
    return this.biHandler(Plugins.plugin(), handler);
  }

  /**
   * adds expire after.
   *
   * @param duration the duration to set.
   *
   * @return {@code this} for the chain.
   */
  @NotNull
  default Slf expireAfter(@NotNull final Duration duration) {
    final var dur = duration.toMillis();
    if (dur < 1) {
      throw new IllegalArgumentException("duration is less than 1ms!");
    }
    final var expiry = Math.addExact(System.currentTimeMillis(), dur);
    return this.expireIf(
        (__, ___) -> System.currentTimeMillis() > expiry,
        ExpiryTestStage.PRE
      );
  }

  /**
   * adds expire after.
   *
   * @param duration the duration to set.
   * @param unit the unit to set.
   *
   * @return {@code this} for the chain.
   */
  @NotNull
  default Slf expireAfter(final long duration, @NotNull final TimeUnit unit) {
    return this.expireAfter(Duration.of(duration, unit.toChronoUnit()));
  }

  /**
   * adds expire after.
   *
   * @param maxCalls the max calls to set.
   *
   * @return {@code this} for the chain.
   */
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

  /**
   * adds expire if.
   *
   * @param predicate the predicate calls to set.
   *
   * @return {@code this} for the chain.
   */
  @NotNull
  default Slf expireIf(@NotNull final Predicate<Event> predicate) {
    return this.expireIf(
        (__, e) -> predicate.test(e),
        ExpiryTestStage.PRE,
        ExpiryTestStage.POST_HANDLE
      );
  }

  /**
   * adds expire if.
   *
   * @param predicate the predicate to set.
   * @param testPoints the test point to set.
   *
   * @return {@code this} for the chain.
   */
  @NotNull
  Slf expireIf(
    @NotNull BiPredicate<Sb, Event> predicate,
    @NotNull ExpiryTestStage... testPoints
  );

  /**
   * adds the filter.
   *
   * @param predicate the predicate calls to set.
   *
   * @return {@code this} for the chain.
   */
  @NotNull
  Slf filter(@NotNull Predicate<Event> predicate);

  /**
   * builds and registers the handler.
   *
   * @param plugin the plugin to register.
   * @param handler the handler to register.
   *
   * @return registered subscription.
   */
  @NotNull
  default Sb handler(
    @NotNull final Plugin plugin,
    @NotNull final Consumer<Event> handler
  ) {
    return this.biHandler(plugin, (__, e) -> handler.accept(e));
  }

  /**
   * builds and registers the handler.
   *
   * @param handler the handler to register.
   *
   * @return registered subscription.
   */
  @NotNull
  default Sb handler(@NotNull final Consumer<Event> handler) {
    return this.handler(Plugins.plugin(), handler);
  }

  /**
   * obtains the handlers.
   *
   * @return handlers.
   */
  @NotNull
  HandlerList handlers();

  /**
   * an interface to determine subscription builder getter.
   *
   * @param <Event> type of the event class.
   * @param <Sb> type of the subscription class.
   */
  interface Get<Event, Sb extends Subscription> {
    /**
     * obtains the filters.
     *
     * @return filters.
     */
    List<Predicate<Event>> filters();

    /**
     * obtains the mid-expiry test.
     *
     * @return mid-expiry test.
     */
    List<BiPredicate<Sb, Event>> midExpiryTests();

    /**
     * obtains the post expiry test.
     *
     * @return expiry test.
     */
    List<BiPredicate<Sb, Event>> postExpiryTests();

    /**
     * obtains the pre expiry test.
     *
     * @return pre expiry test.
     */
    List<BiPredicate<Sb, Event>> preExpiryTests();
  }

  /**
   * an abstract implementation of {@link SubscriptionBuilder}.
   *
   * @param <Plugin> type of the plugin class.
   * @param <Event> type of the event class.
   * @param <Sb> type of the subscription class.
   * @param <HandlerList> type of the handler list class.
   * @param <Slf> type of the self implementation class.
   */
  @Getter
  @Accessors(fluent = true)
  @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
  @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
  abstract class Base<
    Plugin,
    Event,
    Sb extends Subscription,
    HandlerList extends FunctionalHandlerList<Plugin, Event, Sb, HandlerList>,
    Slf extends SubscriptionBuilder<Plugin, Event, Sb, HandlerList, Slf>
  >
    implements
      SubscriptionBuilder<Plugin, Event, Sb, HandlerList, Slf>, Get<Event, Sb> {

    /**
     * the filters.
     */
    List<Predicate<Event>> filters = new ArrayList<>(3);

    /**
     * the mid-expiry test.
     */
    List<BiPredicate<Sb, Event>> midExpiryTests = new ArrayList<>(0);

    /**
     * the post expiry test.
     */
    List<BiPredicate<Sb, Event>> postExpiryTests = new ArrayList<>(0);

    /**
     * the pre expiry test.
     */
    List<BiPredicate<Sb, Event>> preExpiryTests = new ArrayList<>(0);

    @NotNull
    @Override
    public final Slf expireIf(
      @NotNull final BiPredicate<Sb, Event> predicate,
      @NotNull final ExpiryTestStage... testPoints
    ) {
      for (final var testPoint : testPoints) {
        switch (testPoint) {
          case PRE -> this.preExpiryTests.add(predicate);
          case POST_FILTER -> this.midExpiryTests.add(predicate);
          case POST_HANDLE -> this.postExpiryTests.add(predicate);
        }
      }
      return this.self();
    }

    @NotNull
    @Override
    public final Slf filter(@NotNull final Predicate<Event> predicate) {
      this.filters.add(predicate);
      return this.self();
    }
  }
}
