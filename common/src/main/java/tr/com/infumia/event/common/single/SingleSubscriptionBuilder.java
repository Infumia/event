package tr.com.infumia.event.common.single;

import java.util.function.BiConsumer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.NonFinal;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.event.common.Subscription;
import tr.com.infumia.event.common.SubscriptionBuilder;

/**
 * an interface to determine single subscription builders.
 *
 * @param <Plugin> type of the plugin class.
 * @param <Event> type of the event class.
 */
public interface SingleSubscriptionBuilder<Plugin, Event>
  extends
    SubscriptionBuilder<Plugin, Event, Subscription, SingleHandlerList<Plugin, Event>, SingleSubscriptionBuilder<Plugin, Event>> {
  /**
   * creates a new builder for single subscriptions.
   *
   * @param eventClass the event class to create.
   * @param priority the priority to create.
   * @param <Plugin> type of the plugin class.
   * @param <Event> type of the event class.
   * @param <Priority> type of the priority class.
   *
   * @return a newly created builder.
   */
  @NotNull
  static <
    Plugin, Event, Priority
  > SingleSubscriptionBuilder<Plugin, Event> newBuilder(
    @NotNull final Class<Event> eventClass,
    @NotNull final Priority priority
  ) {
    return new Impl<>(eventClass, priority);
  }

  /**
   * sets the exception consumer.
   *
   * @param consumer the consumer to set.
   *
   * @return {@code this} for tha chain.
   */
  @NotNull
  SingleSubscriptionBuilder<Plugin, Event> exceptionConsumer(
    @NotNull BiConsumer<Event, Throwable> consumer
  );

  /**
   * sets that the handler should accept subclasses of the event type.
   *
   * @return {@code this} for the chain.
   */
  @NotNull
  SingleSubscriptionBuilder<Plugin, Event> handleSubclasses();

  @NotNull
  @Override
  default SingleSubscriptionBuilder<Plugin, Event> self() {
    return this;
  }

  /**
   * an interface to determine single subscriptions builder getter.
   *
   * @param <Event> type of the event class.
   */
  interface Get<Event, Priority>
    extends SubscriptionBuilder.Get<Event, Subscription> {
    /**
     * obtains the event class.
     *
     * @return event class.
     */
    @NotNull
    Class<Event> eventClass();

    /**
     * obtains the exception consumer.
     *
     * @return exception consumer.
     */
    @NotNull
    BiConsumer<Event, Throwable> exceptionConsumer();

    /**
     * obtains the handle subclasses.
     *
     * @return {@code true} if handles subclasses.
     */
    boolean isHandleSubclasses();

    /**
     * obtains the priority.
     *
     * @return priority.
     */
    @NotNull
    Priority priority();
  }

  /**
   * a simple implementation of {@link SingleSubscriptionBuilder}.
   *
   * @param <Plugin> type of the plugin class.
   * @param <Event> type of the event class.
   */
  @Accessors(fluent = true)
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  final class Impl<Plugin, Event, Priority>
    extends SubscriptionBuilder.Base<Plugin, Event, Subscription, SingleHandlerList<Plugin, Event>, SingleSubscriptionBuilder<Plugin, Event>>
    implements SingleSubscriptionBuilder<Plugin, Event>, Get<Event, Priority> {

    /**
     * the event class.
     */
    @NotNull
    @Getter
    Class<Event> eventClass;

    /**
     * the exception consumer.
     */
    @NotNull
    @Getter
    @Setter
    @NonFinal
    BiConsumer<Event, Throwable> exceptionConsumer = (__, throwable) ->
      throwable.printStackTrace();

    /**
     * the handle subclasses.
     */
    @NonFinal
    boolean handleSubclasses = false;

    /**
     * the priority.
     */
    @NotNull
    @Getter
    Priority priority;

    @NotNull
    @Override
    public SingleSubscriptionBuilder<Plugin, Event> handleSubclasses() {
      this.handleSubclasses = true;
      return this;
    }

    @NotNull
    @Override
    public SingleHandlerList<Plugin, Event> handlers() {
      return SingleHandlerList.simple(this);
    }

    @Override
    public boolean isHandleSubclasses() {
      return this.handleSubclasses;
    }
  }
}
