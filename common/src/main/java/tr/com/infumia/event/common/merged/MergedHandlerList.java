package tr.com.infumia.event.common.merged;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tr.com.infumia.event.common.FunctionalHandlerList;
import tr.com.infumia.event.common.Subscription;

/**
 * an interface to determine merged handler list.
 *
 * @param <Event> type of the event class.
 * @param <Plugin> type of the plugin class.
 * @param <Handled> type of the handled class.
 */
public interface MergedHandlerList<Plugin, Event, Handled>
  extends
    FunctionalHandlerList<Plugin, Handled, Subscription, MergedHandlerList<Plugin, Event, Handled>> {
  /**
   * creates a merged handler list.
   *
   * @param getter the getter to create.
   * @param <Event> type of the event class.
   * @param <Plugin> type of the plugin class.
   * @param <Priority> type of the priority class.
   * @param <Handled> type of the handled class.
   *
   * @return merged handler list.
   */
  @NotNull
  static <
    Plugin, Event, Priority, Handled
  > MergedHandlerList<Plugin, Event, Handled> simple(
    @NotNull final MergedSubscriptionBuilder.Get<Event, Priority, Handled> getter
  ) {
    return new Impl<>(getter);
  }

  @Override
  @NotNull
  default MergedHandlerList<Plugin, Event, Handled> self() {
    return this;
  }

  /**
   * a class that represents merged handler list implementation.
   *
   * @param <Plugin> type of the plugin class.
   * @param <Event> type of the event class.
   * @param <Handled> type of the handled class.
   */
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
  final class Impl<Plugin, Event, Priority, Handled>
    extends FunctionalHandlerList.Base<Plugin, Handled, Subscription, MergedHandlerList<Plugin, Event, Handled>>
    implements MergedHandlerList<Plugin, Event, Handled> {

    /**
     * the getter.
     */
    @NotNull
    MergedSubscriptionBuilder.Get<Event, Priority, Handled> getter;

    @NotNull
    @Override
    public Subscription register(@Nullable final Plugin plugin) {
      if (this.handlers.isEmpty()) {
        throw new IllegalStateException("No handlers have been registered");
      }
      return new EventListener<>(plugin, this.getter, this.handlers).register();
    }
  }
}
