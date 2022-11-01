package tr.com.infumia.event.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.event.common.Subscription;

/**
 * a class that represents event listener.
 */
@Accessors(fluent = true)
@SuppressWarnings("unchecked")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
final class EventListener extends PacketAdapter implements Subscription {

  /**
   * the active.
   */
  AtomicBoolean active = new AtomicBoolean(true);

  /**
   * the call count.
   */
  AtomicLong callCount = new AtomicLong();

  /**
   * the exception consumer.
   */
  @NotNull
  BiConsumer<PacketEvent, Throwable> exceptionConsumer;

  /**
   * the filters.
   */
  @NotNull
  Predicate<PacketEvent>[] filters;

  /**
   * the handlers.
   */
  @NotNull
  BiConsumer<Subscription, PacketEvent>[] handlers;

  /**
   * the mid-expiry test.
   */
  @NotNull
  BiPredicate<Subscription, PacketEvent>[] midExpiryTests;

  /**
   * tee post expiry tests.
   */
  @NotNull
  BiPredicate<Subscription, PacketEvent>[] postExpiryTests;

  /**
   * the pre expiry tests.
   */
  @NotNull
  BiPredicate<Subscription, PacketEvent>[] preExpiryTests;

  /**
   * the packet types.
   */
  @Getter
  @NotNull
  Set<PacketType> types;

  /**
   * ctor.
   *
   * @param plugin the plugin.
   * @param getter the getter.
   * @param handlers the handlers.
   */
  EventListener(
    @NotNull final Plugin plugin,
    @NotNull final ProtocolSubscriptionBuilder.Get getter,
    @NotNull final List<BiConsumer<Subscription, PacketEvent>> handlers
  ) {
    super(plugin, getter.priority(), getter.types());
    this.types = getter.types();
    this.exceptionConsumer = getter.exceptionConsumer();
    this.filters = getter.filters().toArray(Predicate[]::new);
    this.preExpiryTests = getter.preExpiryTests().toArray(BiPredicate[]::new);
    this.midExpiryTests = getter.midExpiryTests().toArray(BiPredicate[]::new);
    this.postExpiryTests = getter.postExpiryTests().toArray(BiPredicate[]::new);
    this.handlers = handlers.toArray(BiConsumer[]::new);
  }

  @Override
  public boolean active() {
    return this.active.get();
  }

  @Override
  public long callCounter() {
    return this.callCount.get();
  }

  @Override
  public void unregister() {
    if (!this.active.getAndSet(false)) {
      return;
    }
    Protocol.manager().removePacketListener(this);
  }

  @Override
  public boolean closed() {
    return !this.active.get();
  }

  @Override
  public void onPacketReceiving(@NotNull final PacketEvent event) {
    this.onPacket(event);
  }

  @Override
  public void onPacketSending(@NotNull final PacketEvent event) {
    this.onPacket(event);
  }

  /**
   * registers the event.
   */
  @NotNull
  Subscription register() {
    Protocol.manager().addPacketListener(this);
    return this;
  }

  private void onPacket(@NotNull final PacketEvent event) {
    if (!this.types.contains(event.getPacketType())) {
      return;
    }
    if (!this.active.get()) {
      return;
    }
    for (final var test : this.preExpiryTests) {
      if (test.test(this, event)) {
        this.unregister();
        return;
      }
    }
    try {
      for (final var filter : this.filters) {
        if (!filter.test(event)) {
          return;
        }
      }
      for (final var test : this.midExpiryTests) {
        if (test.test(this, event)) {
          this.unregister();
          return;
        }
      }
      for (final var handler : this.handlers) {
        handler.accept(this, event);
      }
      this.callCount.incrementAndGet();
    } catch (final Throwable t) {
      this.exceptionConsumer.accept(event, t);
    }
    for (final var test : this.postExpiryTests) {
      if (test.test(this, event)) {
        this.unregister();
        return;
      }
    }
  }
}
