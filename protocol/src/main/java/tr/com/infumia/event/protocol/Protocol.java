package tr.com.infumia.event.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketContainer;
import java.lang.reflect.InvocationTargetException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * an interface that contains utility methods for protocols.
 */
public interface Protocol {
  /**
   * sends the packet to server.
   *
   * @param packet the packet to send.
   */
  static void broadcastPacket(@NotNull final PacketContainer packet) {
    Protocol.manager().broadcastServerPacket(packet);
  }

  /**
   * sends the packet to the players.
   *
   * @param players the players to send.
   * @param packet the packet to send.
   */
  static void broadcastPacket(
    @NotNull final Iterable<Player> players,
    @NotNull final PacketContainer packet
  ) {
    for (final var player : players) {
      Protocol.sendPacket(player, packet);
    }
  }

  /**
   * gets the protocol manager.
   *
   * @return protocol manager.
   */
  @NotNull
  static ProtocolManager manager() {
    return ProtocolLibrary.getProtocolManager();
  }

  /**
   * sends the packet to the player.
   *
   * @param player the player to send.
   * @param packet the packet to send.
   */
  static void sendPacket(
    @NotNull final Player player,
    @NotNull final PacketContainer packet
  ) {
    try {
      Protocol.manager().sendServerPacket(player, packet);
    } catch (final InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * subscribe to the packets.
   *
   * @param priority the priority.
   * @param packets the packets to subscribe.
   *
   * @return packet subscription builder.
   */
  @NotNull
  static ProtocolSubscriptionBuilder subscribe(
    @NotNull final ListenerPriority priority,
    @NotNull final PacketType... packets
  ) {
    return ProtocolSubscriptionBuilder.newBuilder(priority, packets);
  }

  /**
   * subscribe to the packets.
   *
   * @param packets the packets to subscribe.
   *
   * @return packet subscription builder.
   */
  @NotNull
  static ProtocolSubscriptionBuilder subscribe(
    @NotNull final PacketType... packets
  ) {
    return Protocol.subscribe(ListenerPriority.NORMAL, packets);
  }
}
