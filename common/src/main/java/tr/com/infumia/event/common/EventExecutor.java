package tr.com.infumia.event.common;

import org.jetbrains.annotations.NotNull;

public interface EventExecutor<Event> {
  @NotNull
  Class<? extends Event> eventClass();

  void execute(@NotNull Event event);

  @NotNull
  Object nativeExecutor();

  void nativeExecutor(@NotNull Object nativeExecutor);
}
