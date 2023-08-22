package tr.com.infumia.event.shiruka;

import io.github.shiruka.api.Shiruka;
import io.github.shiruka.api.event.Event;
import io.github.shiruka.api.event.EventListener;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.event.common.EventExecutor;
import tr.com.infumia.event.common.EventManager;

public final class ShirukaEventManager implements EventManager<Event, Integer> {

  @NotNull
  @Override
  public <Registered extends Event> EventExecutor<Registered> register(
    @NotNull final Class<Registered> eventClass,
    @NotNull final Integer priority,
    @NotNull final EventExecutor<Registered> executor
  ) {
    executor.nativeExecutor(
      Shiruka.eventManager().register(eventClass, priority, true, executor::execute)
    );
    return executor;
  }

  @Override
  @SneakyThrows
  public <Registered extends Event> void unregister(
    @NotNull final EventExecutor<Registered> executor
  ) {
    Shiruka.eventManager().unregister((EventListener<? extends Event>) executor.nativeExecutor());
  }
}
