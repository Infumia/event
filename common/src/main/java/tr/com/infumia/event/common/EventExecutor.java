package tr.com.infumia.event.common;

import org.jetbrains.annotations.NotNull;

/**
 * an interface to determine event executors.
 *
 * @param <Event> type of the event.
 */
public interface EventExecutor<Event> {
  /**
   * obtains the event class.
   *
   * @return event class.
   */
  @NotNull
  Class<? extends Event> eventClass();

  /**
   * executes when the event fires.
   *
   * @param event the event to execute.
   */
  void execute(@NotNull Event event);

  /**
   * obtains the native executor.
   *
   * @return native executor.
   */
  @NotNull
  Object nativeExecutor();

  /**
   * sets the native executor.
   *
   * @param nativeExecutor the native executor to set.
   */
  void nativeExecutor(@NotNull Object nativeExecutor);
}
