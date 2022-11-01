package tr.com.infumia.event.paper;

import java.util.Objects;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tr.com.infumia.event.common.EventExecutor;
import tr.com.infumia.event.common.EventManager;

/**
 * a class that represents paper event managers.
 */
@SuppressWarnings("unchecked")
public final class PaperEventManager
  implements EventManager<Plugin, Event, EventPriority> {

  @NotNull
  @Override
  public <Registered extends Event> EventExecutor<Registered> register(
    @Nullable final Plugin plugin,
    @NotNull final Class<Registered> eventClass,
    @NotNull final EventPriority priority,
    @NotNull final EventExecutor<Registered> executor
  ) {
    Objects.requireNonNull(
      plugin,
      "Plugin cannot be null, initiate the plugin via Plugins#init method!"
    );
    final var handler = new Handler<>(executor);
    Bukkit
      .getPluginManager()
      .registerEvent(eventClass, handler, priority, handler, plugin, false);
    return executor;
  }

  @Override
  @SneakyThrows
  public <Registered extends Event> void unregister(
    @Nullable final Plugin plugin,
    @NotNull final EventExecutor<Registered> executor
  ) {
    (
      (HandlerList) executor
        .eventClass()
        .getMethod("getHandlerList")
        .invoke(null)
    ).unregister((Listener) executor.nativeExecutor());
  }

  /**
   * a record class that represents event handlers.
   *
   * @param <Registered> type of the registered event.
   */
  private record Handler<Registered>(
    @NotNull EventExecutor<Registered> executor
  )
    implements org.bukkit.plugin.EventExecutor, Listener {
    /**
     * ctor.
     */
    private Handler {
      executor.nativeExecutor(this);
    }

    @Override
    public void execute(
      @NotNull final Listener listener,
      @NotNull final Event event
    ) {
      this.executor.execute((Registered) event);
    }
  }
}
