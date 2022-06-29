package tr.com.infumia.event.protocol;

import com.comphenix.protocol.events.PacketEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.event.common.FunctionalHandlerList;
import tr.com.infumia.event.common.Subscription;

/**
 * an interface to determine single handler lists.
 */
public interface ProtocolHandlerList
  extends
    FunctionalHandlerList<Plugin, PacketEvent, Subscription, ProtocolHandlerList> {
  /**
   * creates a single handler list.
   *
   * @param getter the getter to create.
   *
   * @return single handler list.
   */
  @NotNull
  static ProtocolHandlerList simple(
    @NotNull final ProtocolSubscriptionBuilder.Get getter
  ) {
    return new Impl(getter);
  }

  @Override
  @NotNull
  default ProtocolHandlerList self() {
    return this;
  }

  /**
   * a class that represents single handler list implementation.
   */
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
  final class Impl
    extends Base<Plugin, PacketEvent, Subscription, ProtocolHandlerList>
    implements ProtocolHandlerList {

    /**
     * the getter.
     */
    @NotNull
    ProtocolSubscriptionBuilder.Get getter;

    @NotNull
    @Override
    @SneakyThrows
    public Subscription register(@NotNull final Plugin plugin) {
      if (this.handlers.isEmpty()) {
        throw new IllegalStateException("No handlers have been registered");
      }
      return new EventListener(plugin, this.getter, this.handlers).register();
    }
  }
}
