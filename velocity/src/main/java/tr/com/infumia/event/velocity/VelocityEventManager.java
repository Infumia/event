package tr.com.infumia.event.velocity;

import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.event.common.EventExecutor;
import tr.com.infumia.event.common.EventManager;
import tr.com.infumia.event.common.Plugins;

public final class VelocityEventManager implements EventManager<Object, PostOrder> {

  @NotNull
  private final ProxyServer server;

  public VelocityEventManager(@NotNull final ProxyServer server) {
    this.server = server;
  }

  @NotNull
  @Override
  public <Registered> EventExecutor<Registered> register(
    @NotNull final Class<Registered> eventClass,
    @NotNull final PostOrder priority,
    @NotNull final EventExecutor<Registered> executor
  ) {
    this.server.getEventManager()
      .register(Plugins.plugin(), eventClass, priority, new Handler<>(executor));
    return executor;
  }

  @Override
  public <Registered> void unregister(@NotNull final EventExecutor<Registered> executor) {
    this.server.getEventManager()
      .unregister(Plugins.plugin(), (EventHandler<?>) executor.nativeExecutor());
  }

  private static final class Handler<Registered> implements EventHandler<Registered> {

    @NotNull
    private final EventExecutor<Registered> executor;

    public Handler(@NotNull EventExecutor<Registered> executor) {
      this.executor = executor;
      executor.nativeExecutor(this);
    }

    @Override
    public void execute(final Registered event) {
      this.executor.execute(event);
    }
  }
}
