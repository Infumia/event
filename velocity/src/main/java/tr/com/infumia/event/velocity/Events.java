package tr.com.infumia.event.velocity;

import com.velocitypowered.api.event.PostOrder;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.event.common.merged.MergedSubscriptionBuilder;
import tr.com.infumia.event.common.single.SingleSubscriptionBuilder;

/**
 * an interface that contains utility methods for events.
 */
public interface Events {
  /**
   * creates a new builder for single subscriptions.
   *
   * @param handledClass the handled class to create.
   * @param <Handled> type of the handled class.
   *
   * @return a newly created builder.
   */
  @NotNull
  static <
    Handled
  > MergedSubscriptionBuilder<Object, Object, PostOrder, Handled> merge(
    @NotNull final Class<Handled> handledClass
  ) {
    return MergedSubscriptionBuilder.newBuilder(handledClass);
  }

  /**
   * creates a new builder for single subscriptions.
   *
   * @param cls the class to create.
   * @param priority the priority to create.
   * @param classes the classes to create.
   * @param <Handled> type of the handled class.
   *
   * @return a newly created builder.
   */
  @NotNull
  @SafeVarargs
  static <
    Handled
  > MergedSubscriptionBuilder<Object, Object, PostOrder, Handled> merge(
    @NotNull final Class<Handled> cls,
    @NotNull final PostOrder priority,
    @NotNull final Class<? extends Handled>... classes
  ) {
    return MergedSubscriptionBuilder.newBuilder(cls, priority, classes);
  }

  /**
   * creates a new builder for single subscriptions.
   *
   * @param superClass the super class to create.
   * @param classes the event classes to create.
   * @param <Handled> type of the handled class.
   *
   * @return a newly created builder.
   */
  @NotNull
  @SafeVarargs
  static <
    Handled
  > MergedSubscriptionBuilder<Object, Object, PostOrder, Handled> merge(
    @NotNull final Class<Handled> superClass,
    @NotNull final Class<? extends Handled>... classes
  ) {
    return Events.merge(superClass, PostOrder.NORMAL, classes);
  }

  /**
   * subscribe to the event class.
   *
   * @param cls the cls to subscribe.
   * @param priority the priority to subscribe
   * @param <Event> type of the event class.
   *
   * @return single subscription builder.
   */
  @NotNull
  static <Event> SingleSubscriptionBuilder<Object, Event> subscribe(
    @NotNull final Class<Event> cls,
    @NotNull final PostOrder priority
  ) {
    return SingleSubscriptionBuilder.newBuilder(cls, priority);
  }

  /**
   * subscribe to the event class.
   *
   * @param cls the cls to subscribe.
   * @param <Event> type of the event class.
   *
   * @return single subscription builder.
   */
  @NotNull
  static <Event> SingleSubscriptionBuilder<Object, Event> subscribe(
    @NotNull final Class<Event> cls
  ) {
    return Events.subscribe(cls, PostOrder.NORMAL);
  }
}
