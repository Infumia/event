package tr.com.infumia.event.common;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
@SuppressWarnings("unchecked")
public class Plugins {

  private final AtomicReference<EventManager<?, ?>> EVENT_MANAGER = new AtomicReference<>();

  public <Event, Priority> void init(@NotNull final EventManager<Event, Priority> manager) {
    Plugins.EVENT_MANAGER.set(manager);
  }

  @NotNull
  public <Event, Priority> EventManager<Event, Priority> manager() {
    return (EventManager<Event, Priority>) Objects.requireNonNull(
      Plugins.EVENT_MANAGER.get(),
      "EventManager not found, use #init(Plugin, EventManager) to initialize!"
    );
  }
}
