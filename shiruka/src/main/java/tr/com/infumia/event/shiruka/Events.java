package tr.com.infumia.event.shiruka;

import io.github.shiruka.api.event.Event;
import net.kyori.event.PostOrders;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.event.common.merged.MergedSubscriptionBuilder;
import tr.com.infumia.event.common.single.SingleSubscriptionBuilder;

public interface Events {
  @NotNull
  static <Handled> MergedSubscriptionBuilder<Event, Integer, Handled> merge(
    @NotNull final Class<Handled> handledClass
  ) {
    return MergedSubscriptionBuilder.newBuilder(handledClass);
  }

  @NotNull
  @SafeVarargs
  static <Handled extends Event> MergedSubscriptionBuilder<Event, Integer, Handled> merge(
    @NotNull final Class<Handled> cls,
    @NotNull final Integer priority,
    @NotNull final Class<? extends Handled>... classes
  ) {
    return MergedSubscriptionBuilder.newBuilder(cls, priority, classes);
  }

  @NotNull
  @SafeVarargs
  static <Handled extends Event> MergedSubscriptionBuilder<Event, Integer, Handled> merge(
    @NotNull final Class<Handled> cls,
    @NotNull final Class<? extends Handled>... classes
  ) {
    return Events.merge(cls, PostOrders.NORMAL, classes);
  }

  @NotNull
  static <Handled extends Event> SingleSubscriptionBuilder<Handled> subscribe(
    @NotNull final Class<Handled> cls,
    @NotNull final Integer priority
  ) {
    return SingleSubscriptionBuilder.newBuilder(cls, priority);
  }

  @NotNull
  static <Handled extends Event> SingleSubscriptionBuilder<Handled> subscribe(
    @NotNull final Class<Handled> cls
  ) {
    return Events.subscribe(cls, PostOrders.NORMAL);
  }
}
