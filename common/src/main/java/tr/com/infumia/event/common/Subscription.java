package tr.com.infumia.event.common;

import tr.com.infumia.terminable.Terminable;

/**
 * an interface to determine event subscriptions.
 */
public interface Subscription extends Terminable {
  /**
   * whether the handler is active.
   *
   * @return {@code true} if the handler is active.
   */
  boolean active();

  /**
   * gets how many times the handler called.
   *
   * @return times the handler called.
   */
  long callCounter();

  @Override
  default void close() {
    this.unregister();
  }

  /**
   * unregisters the handler.
   */
  void unregister();
}
