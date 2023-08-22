package tr.com.infumia.event.bukkit;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.event.common.merged.MergedSubscriptionBuilder;
import tr.com.infumia.event.common.single.SingleSubscriptionBuilder;

public interface Events {
  @NotNull
  static <Handled> MergedSubscriptionBuilder<Event, EventPriority, Handled> merge(
    @NotNull final Class<Handled> handledClass
  ) {
    return MergedSubscriptionBuilder.newBuilder(handledClass);
  }

  @NotNull
  @SafeVarargs
  static <Handled extends Event> MergedSubscriptionBuilder<Event, EventPriority, Handled> merge(
    @NotNull final Class<Handled> cls,
    @NotNull final EventPriority priority,
    @NotNull final Class<? extends Handled>... classes
  ) {
    return MergedSubscriptionBuilder.newBuilder(cls, priority, classes);
  }

  @NotNull
  @SafeVarargs
  static <Handled extends Event> MergedSubscriptionBuilder<Event, EventPriority, Handled> merge(
    @NotNull final Class<Handled> cls,
    @NotNull final Class<? extends Handled>... classes
  ) {
    return Events.merge(cls, EventPriority.NORMAL, classes);
  }

  @NotNull
  static <Handled extends Event> SingleSubscriptionBuilder<Handled> subscribe(
    @NotNull final Class<Handled> cls,
    @NotNull final EventPriority priority
  ) {
    return SingleSubscriptionBuilder.newBuilder(cls, priority);
  }

  @NotNull
  static <Handled extends Event> SingleSubscriptionBuilder<Handled> subscribe(
    @NotNull final Class<Handled> cls
  ) {
    return Events.subscribe(cls, EventPriority.NORMAL);
  }
}
