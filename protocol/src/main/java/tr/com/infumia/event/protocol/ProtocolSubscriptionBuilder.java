package tr.com.infumia.event.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketEvent;
import java.util.Set;
import java.util.function.BiConsumer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.NonFinal;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.event.common.Subscription;
import tr.com.infumia.event.common.SubscriptionBuilder;

/**
 * an interface to determine protocol subscription builders.
 */
public interface ProtocolSubscriptionBuilder
  extends
    SubscriptionBuilder<Plugin, PacketEvent, Subscription, ProtocolHandlerList, ProtocolSubscriptionBuilder> {
  /**
   * creates a new builder for single subscriptions.
   *
   * @param priority the priority to create.
   * @param packets the packets to create.
   *
   * @return a newly created builder.
   */
  @NotNull
  static ProtocolSubscriptionBuilder newBuilder(
    @NotNull final ListenerPriority priority,
    @NotNull final PacketType... packets
  ) {
    return new Impl(priority, Set.of(packets));
  }

  /**
   * sets the exception consumer.
   *
   * @param consumer the consumer to set.
   *
   * @return {@code this} for tha chain.
   */
  @NotNull
  ProtocolSubscriptionBuilder exceptionConsumer(
    @NotNull BiConsumer<PacketEvent, Throwable> consumer
  );

  @NotNull
  @Override
  default ProtocolSubscriptionBuilder self() {
    return this;
  }

  /**
   * an interface to determine single subscriptions builder getter.
   */
  interface Get extends SubscriptionBuilder.Get<PacketEvent, Subscription> {
    /**
     * obtains the exception consumer.
     *
     * @return exception consumer.
     */
    @NotNull
    BiConsumer<PacketEvent, Throwable> exceptionConsumer();

    /**
     * obtains the priority.
     *
     * @return priority.
     */
    @NotNull
    ListenerPriority priority();

    /**
     * obtains the event class.
     *
     * @return event class.
     */
    @NotNull
    Set<PacketType> types();
  }

  /**
   * a simple implementation of {@link ProtocolSubscriptionBuilder}.
   */
  @Accessors(fluent = true)
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  final class Impl
    extends Base<Plugin, PacketEvent, Subscription, ProtocolHandlerList, ProtocolSubscriptionBuilder>
    implements ProtocolSubscriptionBuilder, Get {

    /**
     * the exception consumer.
     */
    @NotNull
    @Getter
    @Setter
    @NonFinal
    BiConsumer<PacketEvent, Throwable> exceptionConsumer = (__, throwable) ->
      throwable.printStackTrace();

    /**
     * the priority.
     */
    @NotNull
    @Getter
    ListenerPriority priority;

    /**
     * the packet types.
     */
    @NotNull
    @Getter
    Set<PacketType> types;

    @NotNull
    @Override
    public ProtocolHandlerList handlers() {
      return ProtocolHandlerList.simple(this);
    }
  }
}
