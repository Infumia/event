package tr.com.infumia.event.common.merged;

import org.jetbrains.annotations.NotNull;
import tr.com.infumia.event.common.FunctionalHandlerList;
import tr.com.infumia.event.common.Subscription;

public interface MergedHandlerList<Event, Handled>
  extends FunctionalHandlerList<Handled, Subscription, MergedHandlerList<Event, Handled>> {
  @NotNull
  static <Event, Priority, Handled> MergedHandlerList<Event, Handled> simple(
    @NotNull final MergedSubscriptionBuilder.Get<Event, Priority, Handled> getter
  ) {
    return new MergedHandlerListImpl<>(getter);
  }

  @Override
  @NotNull
  default MergedHandlerList<Event, Handled> self() {
    return this;
  }
}
