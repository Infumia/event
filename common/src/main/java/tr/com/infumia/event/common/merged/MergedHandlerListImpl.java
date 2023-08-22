package tr.com.infumia.event.common.merged;

import org.jetbrains.annotations.NotNull;
import tr.com.infumia.event.common.FunctionalHandlerList;
import tr.com.infumia.event.common.Subscription;

final class MergedHandlerListImpl<Event, Priority, Handled>
  extends FunctionalHandlerList.Base<Handled, Subscription, MergedHandlerList<Event, Handled>>
  implements MergedHandlerList<Event, Handled> {

  @NotNull
  private final MergedSubscriptionBuilder.Get<Event, Priority, Handled> getter;

  MergedHandlerListImpl(
    @NotNull final MergedSubscriptionBuilder.Get<Event, Priority, Handled> getter
  ) {
    this.getter = getter;
  }

  @NotNull
  @Override
  public Subscription register() {
    return new EventListener<>(this.getter, this.handler).register();
  }
}
