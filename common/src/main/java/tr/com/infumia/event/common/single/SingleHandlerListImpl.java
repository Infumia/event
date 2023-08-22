package tr.com.infumia.event.common.single;

import org.jetbrains.annotations.NotNull;
import tr.com.infumia.event.common.FunctionalHandlerList;
import tr.com.infumia.event.common.Subscription;

final class SingleHandlerListImpl<Event, Priority>
  extends FunctionalHandlerList.Base<Event, Subscription, SingleHandlerList<Event>>
  implements SingleHandlerList<Event> {

  @NotNull
  private final SingleSubscriptionBuilder.Get<Event, Priority> getter;

  SingleHandlerListImpl(@NotNull final SingleSubscriptionBuilder.Get<Event, Priority> getter) {
    this.getter = getter;
  }

  @NotNull
  @Override
  public Subscription register() {
    return new EventListener<>(this.getter, this.handler).register();
  }
}
