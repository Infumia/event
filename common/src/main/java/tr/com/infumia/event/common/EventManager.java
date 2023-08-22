package tr.com.infumia.event.common;

import org.jetbrains.annotations.NotNull;

public interface EventManager<Event, Priority> {
  @NotNull
  <Registered extends Event> EventExecutor<Registered> register(
    @NotNull Class<Registered> eventClass,
    @NotNull Priority priority,
    @NotNull EventExecutor<Registered> executor
  );

  <Registered extends Event> void unregister(@NotNull EventExecutor<Registered> executor);
}
