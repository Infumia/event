package tr.com.infumia.event.protocollib;

import com.comphenix.protocol.events.PacketEvent;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.event.common.FunctionalHandlerList;
import tr.com.infumia.event.common.Subscription;

final class ProtocolHandlerListImpl
  extends FunctionalHandlerList.Base<PacketEvent, Subscription, ProtocolHandlerList>
  implements ProtocolHandlerList {

  @NotNull
  private final ProtocolSubscriptionBuilder.Get getter;

  ProtocolHandlerListImpl(@NotNull final ProtocolSubscriptionBuilder.Get getter) {
    this.getter = getter;
  }

  @NotNull
  @Override
  public Subscription register() {
    return new EventListener(this.getter, this.handler).register();
  }
}
