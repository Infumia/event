package tr.com.infumia.event.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * an interface to determine event managers.
 *
 * @param <Plugin> type of the plugin.
 * @param <Event> type of the event.
 * @param <Priority> type of the priority.
 */
public interface EventManager<Plugin, Event, Priority> {
  /**
   * registers the event.
   *
   * @param plugin the plugin to register.
   * @param eventClass the event class to register.
   * @param priority the priority to register.
   * @param executor the executor to register.
   * @param <Registered> type of the registered event.
   *
   * @return registered event executor.
   */
  @NotNull
  <Registered extends Event> EventExecutor<Registered> register(
    @Nullable Plugin plugin,
    @NotNull Class<Registered> eventClass,
    @NotNull Priority priority,
    @NotNull EventExecutor<Registered> executor
  );

  /**
   * registers the event.
   *
   * @param plugin the plugin to unregister.
   * @param executor the executor to unregister.
   * @param <Registered> type of the registered event.
   */
  <Registered extends Event> void unregister(
    @Nullable Plugin plugin,
    @NotNull EventExecutor<Registered> executor
  );
}
