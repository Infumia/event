package tr.com.infumia.event.paper;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
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
  > MergedSubscriptionBuilder<Plugin, Event, EventPriority, Handled> merge(
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
  > MergedSubscriptionBuilder<Plugin, Event, EventPriority, Handled> merge(
    @NotNull final Class<Handled> cls,
    @NotNull final EventPriority priority,
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
    Handled extends Event
  > MergedSubscriptionBuilder<Plugin, Event, EventPriority, Handled> merge(
    @NotNull final Class<Handled> superClass,
    @NotNull final Class<? extends Handled>... classes
  ) {
    return Events.merge(superClass, EventPriority.NORMAL, classes);
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
    @NotNull final EventPriority priority
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
    return Events.subscribe(cls, EventPriority.NORMAL);
  }
}
