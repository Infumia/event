package tr.com.infumia.event.shiruka;

import io.github.shiruka.api.Shiruka;
import io.github.shiruka.api.event.Event;
import io.github.shiruka.api.event.EventListener;
import io.github.shiruka.api.plugin.Plugin;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tr.com.infumia.event.common.EventExecutor;
import tr.com.infumia.event.common.EventManager;

/**
 * a class that represents shiruka event managers.
 */
@SuppressWarnings("unchecked")
public final class ShirukaEventManager
  implements EventManager<Plugin, Event, Integer> {

  @NotNull
  @Override
  public <Registered extends Event> EventExecutor<Registered> register(
    @Nullable final Plugin plugin,
    @NotNull final Class<Registered> eventClass,
    @NotNull final Integer priority,
    @NotNull final EventExecutor<Registered> executor
  ) {
    executor.nativeExecutor(
      Shiruka
        .eventManager()
        .register(eventClass, priority, true, executor::execute)
    );
    return executor;
  }

  @Override
  @SneakyThrows
  public <Registered extends Event> void unregister(
    @Nullable final Plugin plugin,
    @NotNull final EventExecutor<Registered> executor
  ) {
    Shiruka
      .eventManager()
      .unregister((EventListener<? extends Event>) executor.nativeExecutor());
  }
}
