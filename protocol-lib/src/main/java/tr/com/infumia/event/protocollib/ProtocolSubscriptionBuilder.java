package tr.com.infumia.event.protocollib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.event.common.Subscription;
import tr.com.infumia.event.common.SubscriptionBuilder;

public interface ProtocolSubscriptionBuilder
  extends
    SubscriptionBuilder<
      PacketEvent,
      Subscription,
      ProtocolHandlerList,
      ProtocolSubscriptionBuilder
    > {
  @NotNull
  static ProtocolSubscriptionBuilder newBuilder(
    @NotNull final ListenerPriority priority,
    @NotNull final PacketType... packets
  ) {
    return new ProtocolSubscriptionBuilderImpl(priority, new HashSet<>(Arrays.asList(packets)));
  }

  @NotNull
  ProtocolSubscriptionBuilder exceptionConsumer(
    @NotNull BiConsumer<PacketEvent, Throwable> consumer
  );

  @NotNull
  @Override
  default ProtocolSubscriptionBuilder self() {
    return this;
  }

  interface Get extends SubscriptionBuilder.Get<PacketEvent, Subscription> {
    @NotNull
    BiConsumer<PacketEvent, Throwable> exceptionConsumer();

    @NotNull
    ListenerPriority priority();

    @NotNull
    Collection<PacketType> types();
  }
}
