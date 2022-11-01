package tr.com.infumia.event.common;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * an interface to determine functional handler lists.
 *
 * @param <Plugin> type of the plugin class.
 * @param <Event> type of the event class.
 * @param <Sb> type of the subscription class.
 * @param <Slf> type of the self implementation class.
 */
public interface FunctionalHandlerList<
  Plugin,
  Event,
  Sb extends Subscription,
  Slf extends FunctionalHandlerList<Plugin, Event, Sb, Slf>
>
  extends Self<Slf> {
  /**
   * adds the bi consumer to handlers.
   *
   * @param handler the handler to add.
   *
   * @return {@code this} for the chain.
   */
  @NotNull
  Slf biConsumer(@NotNull BiConsumer<Sb, Event> handler);

  /**
   * adds the consumer to handlers.
   *
   * @param handler the handler to add.
   *
   * @return {@code this} for the chain.
   */
  @NotNull
  default Slf consumer(@NotNull final Consumer<Event> handler) {
    return this.biConsumer((__, e) -> handler.accept(e));
  }

  /**
   * registers the handlers.
   *
   * @param plugin the plugin to register.
   *
   * @return registered subscription.
   */
  @NotNull
  Sb register(@Nullable Plugin plugin);

  /**
   * an abstract implementation of {@link FunctionalHandlerList}.
   *
   * @param <Plugin> type of the plugin class.
   * @param <Event> type of the event class.
   * @param <Sb> type of the subscription class.
   * @param <Slf> type of the self implementation class.
   */
  @FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
  abstract class Base<
    Plugin,
    Event,
    Sb extends Subscription,
    Slf extends FunctionalHandlerList<Plugin, Event, Sb, Slf>
  >
    implements FunctionalHandlerList<Plugin, Event, Sb, Slf> {

    /**
     * the handlers.
     */
    List<BiConsumer<Sb, Event>> handlers = new ArrayList<>(1);

    @NotNull
    @Override
    public final Slf biConsumer(@NotNull final BiConsumer<Sb, Event> handler) {
      this.handlers.add(handler);
      return this.self();
    }
  }
}
