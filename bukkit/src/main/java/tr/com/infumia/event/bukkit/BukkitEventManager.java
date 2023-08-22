package tr.com.infumia.event.bukkit;

import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.event.common.EventExecutor;
import tr.com.infumia.event.common.EventManager;
import tr.com.infumia.event.common.Plugins;

@SuppressWarnings("unchecked")
public final class BukkitEventManager implements EventManager<Event, EventPriority> {

  @NotNull
  @Override
  public <Registered extends Event> EventExecutor<Registered> register(
    @NotNull final Class<Registered> eventClass,
    @NotNull final EventPriority priority,
    @NotNull final EventExecutor<Registered> executor
  ) {
    final Handler<Registered> handler = new Handler<>(executor);
    Bukkit
      .getPluginManager()
      .registerEvent(eventClass, handler, priority, handler, Plugins.plugin(), false);
    return executor;
  }

  @Override
  @SneakyThrows
  public <Registered extends Event> void unregister(
    @NotNull final EventExecutor<Registered> executor
  ) {
    ((HandlerList) executor.eventClass().getMethod("getHandlerList").invoke(null)).unregister(
        (Listener) executor.nativeExecutor()
      );
  }

  private static final class Handler<Registered>
    implements org.bukkit.plugin.EventExecutor, Listener {

    @NotNull
    private final EventExecutor<Registered> executor;

    public Handler(@NotNull EventExecutor<Registered> executor) {
      this.executor = executor;
      executor.nativeExecutor(this);
    }

    @Override
    public void execute(@NotNull final Listener listener, @NotNull final Event event) {
      this.executor.execute((Registered) event);
    }
  }
}
