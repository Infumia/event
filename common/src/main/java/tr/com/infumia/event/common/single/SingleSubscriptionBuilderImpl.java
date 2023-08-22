package tr.com.infumia.event.common.single;

import java.util.function.BiConsumer;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.event.common.Subscription;
import tr.com.infumia.event.common.SubscriptionBuilder;

@Getter
final class SingleSubscriptionBuilderImpl<Event, Priority>
  extends SubscriptionBuilder.Base<
    Event,
    Subscription,
    SingleHandlerList<Event>,
    SingleSubscriptionBuilder<Event>
  >
  implements SingleSubscriptionBuilder<Event>, SingleSubscriptionBuilder.Get<Event, Priority> {

  @NotNull
  private final Class<Event> eventClass;

  @Getter
  @NotNull
  private final Priority priority;

  @Getter
  @Setter
  @NotNull
  private BiConsumer<Event, Throwable> exceptionConsumer = (__, throwable) ->
    throwable.printStackTrace();

  private boolean handleSubclasses = false;

  SingleSubscriptionBuilderImpl(
    @NotNull final Class<Event> eventClass,
    @NotNull final Priority priority
  ) {
    this.eventClass = eventClass;
    this.priority = priority;
  }

  @NotNull
  @Override
  public SingleSubscriptionBuilder<Event> handleSubclasses() {
    this.handleSubclasses = true;
    return this;
  }

  @NotNull
  @Override
  public SingleHandlerList<Event> handlers() {
    return SingleHandlerList.simple(this);
  }

  @Override
  public boolean isHandleSubclasses() {
    return this.handleSubclasses;
  }
}
