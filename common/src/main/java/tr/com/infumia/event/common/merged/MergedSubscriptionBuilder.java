package tr.com.infumia.event.common.merged;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.event.common.Subscription;
import tr.com.infumia.event.common.SubscriptionBuilder;

public interface MergedSubscriptionBuilder<Event, Priority, Handled>
  extends
    SubscriptionBuilder<
      Handled,
      Subscription,
      MergedHandlerList<Event, Handled>,
      MergedSubscriptionBuilder<Event, Priority, Handled>
    > {
  @NotNull
  static <Event, Priority, Handled> MergedSubscriptionBuilder<Event, Priority, Handled> newBuilder(
    @NotNull final Class<Handled> handledClass
  ) {
    return new MergedSubscriptionBuilderImpl<>(handledClass);
  }

  @NotNull
  @SafeVarargs
  static <Event, Priority, Handled extends Event> MergedSubscriptionBuilder<
    Event,
    Priority,
    Handled
  > newBuilder(
    @NotNull final Class<Handled> cls,
    @NotNull final Priority priority,
    @NotNull final Class<? extends Handled>... classes
  ) {
    if (classes.length < 2) {
      throw new IllegalArgumentException("Merge method used for only one subclass");
    }
    final MergedSubscriptionBuilder<Event, Priority, Handled> builder =
      MergedSubscriptionBuilder.newBuilder(cls);
    for (final Class<? extends Handled> event : classes) {
      builder.bindEvent(event, priority, e -> e);
    }
    return builder;
  }

  @NotNull
  default <Merged extends Event> MergedSubscriptionBuilder<Event, Priority, Handled> bindEvent(
    @NotNull final Class<Merged> cls,
    @NotNull final Priority priority,
    @NotNull final Function<Merged, Handled> mapping
  ) {
    return this.bindEvent(
        cls,
        priority,
        mapping,
        (event, throwable) -> throwable.printStackTrace()
      );
  }

  @NotNull
  <Merged extends Event> MergedSubscriptionBuilder<Event, Priority, Handled> bindEvent(
    @NotNull Class<Merged> cls,
    @NotNull Priority priority,
    @NotNull Function<Merged, Handled> mapping,
    @NotNull BiConsumer<Merged, Throwable> exceptionConsumer
  );

  @NotNull
  @Override
  default MergedSubscriptionBuilder<Event, Priority, Handled> self() {
    return this;
  }

  interface Get<Event, Priority, Handled> extends SubscriptionBuilder.Get<Handled, Subscription> {
    @NotNull
    Map<
      Class<? extends Event>,
      MergedHandlerMapping<? extends Event, Priority, Handled>
    > mappings();
  }
}
