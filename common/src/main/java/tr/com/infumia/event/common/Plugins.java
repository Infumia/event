package tr.com.infumia.event.common;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

/**
 * a utility class that contains utility methods for plugins.
 */
@UtilityClass
@SuppressWarnings("unchecked")
public class Plugins {

  /**
   * the instance.
   */
  private final AtomicReference<EventManager<?, ?, ?>> EVENT_MANAGER = new AtomicReference<>();

  /**
   * the instance.
   */
  private final AtomicReference<Object> PLUGIN = new AtomicReference<>();

  /**
   * initiates the events.
   *
   * @param plugin the plugin to init.
   * @param manager the manager to init.
   * @param <Plugin> type of the plugin.
   * @param <Event> type of the event class.
   * @param <Priority> type of the priority class.
   */
  public <Plugin, Event, Priority> void init(
    @NotNull final Plugin plugin,
    @NotNull final EventManager<Plugin, Event, Priority> manager
  ) {
    Plugins.PLUGIN.set(plugin);
    Plugins.EVENT_MANAGER.set(manager);
  }

  /**
   * gets the event manager.
   *
   * @param <Plugin> type of the plugin class.
   * @param <Event> type of the event class.
   * @param <Priority> type of the priority class.
   *
   * @return event manager.
   */
  @NotNull
  public <
    Plugin, Event, Priority
  > EventManager<Plugin, Event, Priority> manager() {
    return (EventManager<Plugin, Event, Priority>) Objects.requireNonNull(
      Plugins.EVENT_MANAGER.get(),
      "EventManager not found, use #init(Plugin, EventManager) to initialize!"
    );
  }

  /**
   * gets the plugin.
   *
   * @param <Plugin> type of the plugin.
   *
   * @return plugin.
   */
  @NotNull
  public <Plugin> Plugin plugin() {
    return (Plugin) Objects.requireNonNull(
      Plugins.PLUGIN.get(),
      "Plugin not found, use #init(Plugin, EventManager) to initialize!"
    );
  }
}
