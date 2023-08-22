package tr.com.infumia.event.protocollib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface Protocol {
  static void broadcastPacket(@NotNull final PacketContainer packet) {
    Protocol.manager().broadcastServerPacket(packet);
  }

  static void broadcastPacket(
    @NotNull final Iterable<Player> players,
    @NotNull final PacketContainer packet
  ) {
    for (final Player player : players) {
      Protocol.sendPacket(player, packet);
    }
  }

  @NotNull
  static ProtocolManager manager() {
    return ProtocolLibrary.getProtocolManager();
  }

  static void sendPacket(@NotNull final Player player, @NotNull final PacketContainer packet) {
    try {
      Protocol.manager().sendServerPacket(player, packet);
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  @NotNull
  static ProtocolSubscriptionBuilder subscribe(
    @NotNull final ListenerPriority priority,
    @NotNull final PacketType... packets
  ) {
    return ProtocolSubscriptionBuilder.newBuilder(priority, packets);
  }

  @NotNull
  static ProtocolSubscriptionBuilder subscribe(@NotNull final PacketType... packets) {
    return Protocol.subscribe(ListenerPriority.NORMAL, packets);
  }
}
