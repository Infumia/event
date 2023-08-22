package tr.com.infumia.event.common.single;

import org.jetbrains.annotations.NotNull;
import tr.com.infumia.event.common.FunctionalHandlerList;
import tr.com.infumia.event.common.Subscription;

public interface SingleHandlerList<Event>
  extends FunctionalHandlerList<Event, Subscription, SingleHandlerList<Event>> {
  @NotNull
  static <Event, Priority> SingleHandlerList<Event> simple(
    @NotNull final SingleSubscriptionBuilder.Get<Event, Priority> getter
  ) {
    return new SingleHandlerListImpl<>(getter);
  }

  @Override
  @NotNull
  default SingleHandlerList<Event> self() {
    return this;
  }
}
