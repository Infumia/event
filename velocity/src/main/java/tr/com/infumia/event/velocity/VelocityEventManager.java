package tr.com.infumia.event.velocity;

import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.event.common.EventExecutor;
import tr.com.infumia.event.common.EventManager;

/**
 * a record class that represents velocity event managers.
 *
 * @param server the server.
 */
public record VelocityEventManager(@NotNull ProxyServer server)
  implements EventManager<Object, Object, PostOrder> {
  @NotNull
  @Override
  public <Registered> EventExecutor<Registered> register(
    @NotNull final Object plugin,
    @NotNull final Class<Registered> eventClass,
    @NotNull final PostOrder postOrder,
    @NotNull final EventExecutor<Registered> executor
  ) {
    this.server.getEventManager()
      .register(plugin, eventClass, postOrder, new Handler<>(executor));
    return executor;
  }

  @Override
  public <Registered> void unregister(
    @NotNull final Object plugin,
    @NotNull final EventExecutor<Registered> executor
  ) {
    this.server.getEventManager()
      .unregister(plugin, (EventHandler<?>) executor.nativeExecutor());
  }

  /**
   * a record class that represents event handlers.
   *
   * @param <Registered> type of the registered event.
   */
  private record Handler<Registered>(
    @NotNull EventExecutor<Registered> executor
  )
    implements EventHandler<Registered> {
    /**
     * ctor.
     */
    private Handler {
      executor.nativeExecutor(this);
    }

    @Override
    public void execute(final Registered event) {
      this.executor.execute(event);
    }
  }
}
