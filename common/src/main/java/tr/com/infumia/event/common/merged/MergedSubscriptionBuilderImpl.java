package tr.com.infumia.event.common.merged;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.event.common.Subscription;
import tr.com.infumia.event.common.SubscriptionBuilder;

@Getter
final class MergedSubscriptionBuilderImpl<Event, Priority, Handled>
  extends SubscriptionBuilder.Base<
    Handled,
    Subscription,
    MergedHandlerList<Event, Handled>,
    MergedSubscriptionBuilder<Event, Priority, Handled>
  >
  implements
    MergedSubscriptionBuilder<Event, Priority, Handled>,
    MergedSubscriptionBuilder.Get<Event, Priority, Handled> {

  @NotNull
  private final Class<Handled> handledClass;

  private final Map<
    Class<? extends Event>,
    MergedHandlerMapping<? extends Event, Priority, Handled>
  > mappings = new HashMap<>();

  MergedSubscriptionBuilderImpl(@NotNull final Class<Handled> handledClass) {
    this.handledClass = handledClass;
  }

  @NotNull
  @Override
  public <Merged extends Event> MergedSubscriptionBuilder<Event, Priority, Handled> bindEvent(
    @NotNull final Class<Merged> cls,
    @NotNull final Priority priority,
    @NotNull final Function<Merged, Handled> mapping,
    @NotNull final BiConsumer<Merged, Throwable> exceptionConsumer
  ) {
    this.mappings.put(
        cls,
        new MergedHandlerMappingImpl<>(cls, priority, mapping, exceptionConsumer)
      );
    return this;
  }

  @NotNull
  @Override
  public MergedHandlerList<Event, Handled> handlers() {
    if (this.mappings.isEmpty()) {
      throw new IllegalStateException("No mappings were created");
    }
    return MergedHandlerList.simple(this);
  }
}
