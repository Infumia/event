package tr.com.infumia.event.common.single;

import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.event.common.Subscription;
import tr.com.infumia.event.common.SubscriptionBuilder;

public interface SingleSubscriptionBuilder<Event>
  extends
    SubscriptionBuilder<
      Event,
      Subscription,
      SingleHandlerList<Event>,
      SingleSubscriptionBuilder<Event>
    > {
  @NotNull
  static <Event, Priority> SingleSubscriptionBuilder<Event> newBuilder(
    @NotNull final Class<Event> eventClass,
    @NotNull final Priority priority
  ) {
    return new SingleSubscriptionBuilderImpl<>(eventClass, priority);
  }

  @NotNull
  SingleSubscriptionBuilder<Event> exceptionConsumer(
    @NotNull BiConsumer<Event, Throwable> consumer
  );

  @NotNull
  SingleSubscriptionBuilder<Event> handleSubclasses();

  @NotNull
  @Override
  default SingleSubscriptionBuilder<Event> self() {
    return this;
  }

  interface Get<Event, Priority> extends SubscriptionBuilder.Get<Event, Subscription> {
    @NotNull
    Class<Event> eventClass();

    @NotNull
    BiConsumer<Event, Throwable> exceptionConsumer();

    boolean isHandleSubclasses();

    @NotNull
    Priority priority();
  }
}
