package tr.com.infumia.event.velocity;

import com.velocitypowered.api.event.PostOrder;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.event.common.merged.MergedSubscriptionBuilder;
import tr.com.infumia.event.common.single.SingleSubscriptionBuilder;

public interface Events {
  @NotNull
  static <Handled> MergedSubscriptionBuilder<Object, PostOrder, Handled> merge(
    @NotNull final Class<Handled> handledClass
  ) {
    return MergedSubscriptionBuilder.newBuilder(handledClass);
  }

  @NotNull
  @SafeVarargs
  static <Handled> MergedSubscriptionBuilder<Object, PostOrder, Handled> merge(
    @NotNull final Class<Handled> cls,
    @NotNull final PostOrder priority,
    @NotNull final Class<? extends Handled>... classes
  ) {
    return MergedSubscriptionBuilder.newBuilder(cls, priority, classes);
  }

  @NotNull
  @SafeVarargs
  static <Handled> MergedSubscriptionBuilder<Object, PostOrder, Handled> merge(
    @NotNull final Class<Handled> superClass,
    @NotNull final Class<? extends Handled>... classes
  ) {
    return Events.merge(superClass, PostOrder.NORMAL, classes);
  }

  @NotNull
  static <Event> SingleSubscriptionBuilder<Event> subscribe(
    @NotNull final Class<Event> cls,
    @NotNull final PostOrder priority
  ) {
    return SingleSubscriptionBuilder.newBuilder(cls, priority);
  }

  @NotNull
  static <Event> SingleSubscriptionBuilder<Event> subscribe(@NotNull final Class<Event> cls) {
    return Events.subscribe(cls, PostOrder.NORMAL);
  }
}
