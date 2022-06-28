package tr.com.infumia.event.common.single;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.event.common.FunctionalHandlerList;
import tr.com.infumia.event.common.Subscription;

/**
 * an interface to determine single handler lists.
 *
 * @param <Plugin> type of the plugin class.
 * @param <Event> type of the event class.
 */
public interface SingleHandlerList<Plugin, Event>
  extends
    FunctionalHandlerList<Plugin, Event, Subscription, SingleHandlerList<Plugin, Event>> {
  /**
   * creates a single handler list.
   *
   * @param getter the getter to create.
   * @param <Plugin> type of the plugin class.
   * @param <Event> type of the event class.
   * @param <Priority> type of the priority class.
   *
   * @return single handler list.
   */
  @NotNull
  static <Plugin, Event, Priority> SingleHandlerList<Plugin, Event> simple(
    @NotNull final SingleSubscriptionBuilder.Get<Event, Priority> getter
  ) {
    return new Impl<>(getter);
  }

  @Override
  @NotNull
  default SingleHandlerList<Plugin, Event> self() {
    return this;
  }

  /**
   * a class that represents single handler list implementation.
   *
   * @param <Plugin> type of the plugin class.
   * @param <Event> type of the event class.
   */
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
  final class Impl<Plugin, Event, Priority>
    extends FunctionalHandlerList.Base<Plugin, Event, Subscription, SingleHandlerList<Plugin, Event>>
    implements SingleHandlerList<Plugin, Event> {

    /**
     * the getter.
     */
    @NotNull
    SingleSubscriptionBuilder.Get<Event, Priority> getter;

    @NotNull
    @Override
    @SneakyThrows
    public Subscription register(@NotNull final Plugin plugin) {
      if (this.handlers.isEmpty()) {
        throw new IllegalStateException("No handlers have been registered");
      }
      return new EventListener<>(plugin, this.getter, this.handlers).register();
    }
  }
}
