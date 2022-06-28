package tr.com.infumia.event.common.merged;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.event.common.Subscription;
import tr.com.infumia.event.common.SubscriptionBuilder;

/**
 * an interface to determine merged subscription builders.
 */
public interface MergedSubscriptionBuilder<Plugin, Event, Priority, Handled>
  extends
    SubscriptionBuilder<Plugin, Handled, Subscription, MergedHandlerList<Plugin, Event, Handled>, MergedSubscriptionBuilder<Plugin, Event, Priority, Handled>> {
  /**
   * creates a new builder for single subscriptions.
   *
   * @param handledClass the handled class to create.
   * @param <Plugin> type of the plugin class.
   * @param <Event> type of the event class.
   * @param <Priority> type of the priority class.
   * @param <Handled> type of the handled class.
   *
   * @return a newly created builder.
   */
  @NotNull
  static <
    Plugin, Event, Priority, Handled
  > MergedSubscriptionBuilder<Plugin, Event, Priority, Handled> newBuilder(
    @NotNull final Class<Handled> handledClass
  ) {
    return new Impl<>(handledClass);
  }

  /**
   * creates a new builder for single subscriptions.
   *
   * @param cls the class to create.
   * @param priority the priority to create.
   * @param classes the classes to create.
   * @param <Plugin> type of the plugin class.
   * @param <Event> type of the event class.
   * @param <Priority> type of the priority class.
   * @param <Handled> type of the handled class.
   *
   * @return a newly created builder.
   */
  @NotNull
  @SafeVarargs
  static <
    Plugin, Event, Priority, Handled extends Event
  > MergedSubscriptionBuilder<Plugin, Event, Priority, Handled> newBuilder(
    @NotNull final Class<Handled> cls,
    @NotNull final Priority priority,
    @NotNull final Class<? extends Handled>... classes
  ) {
    if (classes.length < 2) {
      throw new IllegalArgumentException(
        "merge method used for only one subclass"
      );
    }
    final var builder = MergedSubscriptionBuilder.<Plugin, Event, Priority, Handled>newBuilder(
      cls
    );
    for (final var event : classes) {
      builder.bindEvent(event, priority, e -> e);
    }
    return builder;
  }

  /**
   * binds the event.
   *
   * @param cls the event class to bind.
   * @param priority the priority to bind.
   * @param mapping the mapping to bind.
   * @param <Merged> type of the merged class.
   *
   * @return {@code this} for the chain.
   */
  @NotNull
  <
    Merged extends Event
  > MergedSubscriptionBuilder<Plugin, Event, Priority, Handled> bindEvent(
    @NotNull Class<Merged> cls,
    @NotNull Priority priority,
    @NotNull Function<Merged, Handled> mapping
  );

  /**
   * sets the exception consumer.
   *
   * @param consumer the consumer to set.
   *
   * @return {@code this} for tha chain.
   */
  @NotNull
  MergedSubscriptionBuilder<Plugin, Event, Priority, Handled> exceptionConsumer(
    @NotNull BiConsumer<Event, Throwable> consumer
  );

  @NotNull
  @Override
  default MergedSubscriptionBuilder<Plugin, Event, Priority, Handled> self() {
    return this;
  }

  /**
   * an interface to determine merged subscriptions builder getter.
   */
  interface Get<Event, Priority, Handled>
    extends SubscriptionBuilder.Get<Handled, Subscription> {
    /**
     * obtains the exception consumer.
     *
     * @return exception consumer.
     */
    @NotNull
    BiConsumer<Event, Throwable> exceptionConsumer();

    /**
     * obtains the handled class.
     *
     * @return handled class.
     */
    @NotNull
    Class<Handled> handledClass();

    /**
     * obtains the mappings.
     *
     * @return mappings.
     */
    @NotNull
    Map<Class<? extends Event>, MergedHandlerMapping<? extends Event, Priority, Handled>> mappings();
  }

  /**
   * a simple implementation of {@link MergedSubscriptionBuilder}.
   */
  @Getter
  @Accessors(fluent = true)
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
  final class Impl<Plugin, Event, Priority, Handled>
    extends SubscriptionBuilder.Base<Plugin, Handled, Subscription, MergedHandlerList<Plugin, Event, Handled>, MergedSubscriptionBuilder<Plugin, Event, Priority, Handled>>
    implements
      MergedSubscriptionBuilder<Plugin, Event, Priority, Handled>,
      Get<Event, Priority, Handled> {

    /**
     * the handled class.
     */
    @NotNull
    Class<Handled> handledClass;

    /**
     * the mappings.
     */
    Map<Class<? extends Event>, MergedHandlerMapping<? extends Event, Priority, Handled>> mappings = new HashMap<>();

    /**
     * the exception consumer.
     */
    @Setter
    @NotNull
    @NonFinal
    BiConsumer<Event, Throwable> exceptionConsumer = (__, throwable) ->
      throwable.printStackTrace();

    @NotNull
    @Override
    public <
      Merged extends Event
    > MergedSubscriptionBuilder<Plugin, Event, Priority, Handled> bindEvent(
      @NotNull final Class<Merged> cls,
      @NotNull final Priority priority,
      @NotNull final Function<Merged, Handled> mapping
    ) {
      this.mappings.put(cls, new MergedHandlerMapping<>(priority, mapping));
      return this;
    }

    @NotNull
    @Override
    public MergedHandlerList<Plugin, Event, Handled> handlers() {
      if (this.mappings.isEmpty()) {
        throw new IllegalStateException("No mappings were created");
      }
      return MergedHandlerList.simple(this);
    }
  }
}
