package tr.com.infumia.event.common;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
@SuppressWarnings("unchecked")
public class Plugins {

  private final AtomicReference<EventManager<?, ?>> EVENT_MANAGER = new AtomicReference<>();

  private final AtomicReference<Object> PLUGIN = new AtomicReference<>();

  public <Event, Priority> void init(@NotNull final EventManager<Event, Priority> manager) {
    Plugins.EVENT_MANAGER.set(manager);
  }

  public <Event, Priority> void init(
    @Nullable final Object plugin,
    @NotNull final EventManager<Event, Priority> manager
  ) {
    Plugins.PLUGIN.set(plugin);
    Plugins.EVENT_MANAGER.set(manager);
  }

  @NotNull
  public <Event, Priority> EventManager<Event, Priority> manager() {
    return (EventManager<Event, Priority>) Objects.requireNonNull(
      Plugins.EVENT_MANAGER.get(),
      "EventManager not found, use #init(Plugin, EventManager) to initialize!"
    );
  }

  @NotNull
  public <Plugin> Plugin plugin() {
    return Objects.requireNonNull(
      (Plugin) Plugins.PLUGIN.get(),
      "Plugin not found, use #init(Plugin, EventManager) to initialize!"
    );
  }
}
