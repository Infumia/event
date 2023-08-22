package tr.com.infumia.event.common;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public interface FunctionalHandlerList<
  Event, Sb extends Subscription, Slf extends FunctionalHandlerList<Event, Sb, Slf>
>
  extends Self<Slf> {
  @NotNull
  Slf biConsumer(@NotNull BiConsumer<Sb, Event> handler);

  @NotNull
  default Slf consumer(@NotNull final Consumer<Event> handler) {
    return this.biConsumer((__, e) -> handler.accept(e));
  }

  @NotNull
  Sb register();

  abstract class Base<
    Event, Sb extends Subscription, Slf extends FunctionalHandlerList<Event, Sb, Slf>
  >
    implements FunctionalHandlerList<Event, Sb, Slf> {

    protected BiConsumer<Sb, Event> handler = (sb, event) -> {};

    @NotNull
    @Override
    public final Slf biConsumer(@NotNull final BiConsumer<Sb, Event> handler) {
      this.handler = this.handler.andThen(handler);
      return this.self();
    }
  }
}
