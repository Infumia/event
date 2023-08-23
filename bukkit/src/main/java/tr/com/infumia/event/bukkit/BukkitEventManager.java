package tr.com.infumia.event.bukkit;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.event.common.EventExecutor;
import tr.com.infumia.event.common.EventManager;

@SuppressWarnings("unchecked")
public final class BukkitEventManager implements EventManager<Event, EventPriority> {

  private final Map<Class<?>, MethodHandle> getHandlerListMethods = new HashMap<>();

  @Getter
  @NotNull
  private final Plugin plugin;

  public BukkitEventManager(@NotNull Plugin plugin) {
    this.plugin = plugin;
  }

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
      .registerEvent(eventClass, handler, priority, handler, this.plugin, false);
    return executor;
  }

  @Override
  @SneakyThrows
  public <Registered extends Event> void unregister(
    @NotNull final EventExecutor<Registered> executor
  ) {
    final MethodHandle handle =
      this.getHandlerListMethods.computeIfAbsent(
          executor.eventClass(),
          cls -> {
            try {
              return MethodHandles
                .lookup()
                .findStatic(cls, "getHandlerList", MethodType.methodType(HandlerList.class));
            } catch (final Exception e) {
              throw new RuntimeException(e);
            }
          }
        );
    ((HandlerList) handle.invokeExact()).unregister((Listener) executor.nativeExecutor());
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
