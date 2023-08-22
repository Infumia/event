package tr.com.infumia.event.protocollib;

import com.comphenix.protocol.events.PacketEvent;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.event.common.FunctionalHandlerList;
import tr.com.infumia.event.common.Subscription;

public interface ProtocolHandlerList
  extends FunctionalHandlerList<PacketEvent, Subscription, ProtocolHandlerList> {
  @NotNull
  static ProtocolHandlerList simple(@NotNull final ProtocolSubscriptionBuilder.Get getter) {
    return new ProtocolHandlerListImpl(getter);
  }

  @Override
  @NotNull
  default ProtocolHandlerList self() {
    return this;
  }
}
