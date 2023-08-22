package tr.com.infumia.event.protocollib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketEvent;
import java.util.Collection;
import java.util.function.BiConsumer;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.event.common.Subscription;
import tr.com.infumia.event.common.SubscriptionBuilder;

@Getter
final class ProtocolSubscriptionBuilderImpl
  extends SubscriptionBuilder.Base<
    PacketEvent,
    Subscription,
    ProtocolHandlerList,
    ProtocolSubscriptionBuilder
  >
  implements ProtocolSubscriptionBuilder, ProtocolSubscriptionBuilder.Get {

  @NotNull
  private final ListenerPriority priority;

  @NotNull
  private final Collection<PacketType> types;

  @Setter
  @NotNull
  private BiConsumer<PacketEvent, Throwable> exceptionConsumer = (__, throwable) ->
    throwable.printStackTrace();

  ProtocolSubscriptionBuilderImpl(
    @NotNull final ListenerPriority priority,
    @NotNull final Collection<PacketType> types
  ) {
    this.priority = priority;
    this.types = types;
  }

  @NotNull
  @Override
  public ProtocolHandlerList handlers() {
    return ProtocolHandlerList.simple(this);
  }
}
