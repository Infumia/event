package tr.com.infumia.event.common;

import tr.com.infumia.terminable.Terminable;

public interface Subscription extends Terminable {
  boolean active();

  long callCounter();

  @Override
  default void close() {
    this.unregister();
  }

  void unregister();
}
