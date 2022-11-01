package tr.com.infumia.event.shiruka;

import io.github.shiruka.api.event.Event;
import io.github.shiruka.api.plugin.Plugin;
import net.kyori.event.PostOrders;
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
  > MergedSubscriptionBuilder<Plugin, Event, Integer, Handled> merge(
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
    Handled extends Event
  > MergedSubscriptionBuilder<Plugin, Event, Integer, Handled> merge(
    @NotNull final Class<Handled> cls,
    @NotNull final Integer priority,
    @NotNull final Class<? extends Handled>... classes
  ) {
    return MergedSubscriptionBuilder.newBuilder(cls, priority, classes);
  }

  /**
   * creates a new builder for single subscriptions.
   *
   * @param cls the class to create.
   * @param classes the event classes to create.
   * @param <Handled> type of the handled class.
   *
   * @return a newly created builder.
   */
  @NotNull
  @SafeVarargs
  static <
    Handled extends Event
  > MergedSubscriptionBuilder<Plugin, Event, Integer, Handled> merge(
    @NotNull final Class<Handled> cls,
    @NotNull final Class<? extends Handled>... classes
  ) {
    return Events.merge(cls, PostOrders.NORMAL, classes);
  }

  /**
   * subscribe to the event class.
   *
   * @param cls the cls to subscribe.
   * @param priority the priority to subscribe.
   * @param <Handled> type of the handled class.
   *
   * @return single subscription builder.
   */
  @NotNull
  static <
    Handled extends Event
  > SingleSubscriptionBuilder<Plugin, Handled> subscribe(
    @NotNull final Class<Handled> cls,
    @NotNull final Integer priority
  ) {
    return SingleSubscriptionBuilder.newBuilder(cls, priority);
  }

  /**
   * subscribe to the event class.
   *
   * @param cls the cls to subscribe.
   * @param <Handled> type of the handled class.
   *
   * @return single subscription builder.
   */
  @NotNull
  static <
    Handled extends Event
  > SingleSubscriptionBuilder<Plugin, Handled> subscribe(
    @NotNull final Class<Handled> cls
  ) {
    return Events.subscribe(cls, PostOrders.NORMAL);
  }
}
