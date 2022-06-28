package tr.com.infumia.event.common.merged;

import java.util.function.Function;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

/**
 * a class that represents merged handler mappings.
 *
 * @param <Event> type of the event.
 * @param <Priority> type of the priority.
 * @param <Handled> type of the handled.
 */
@Getter
@Accessors(fluent = true)
@SuppressWarnings("unchecked")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class MergedHandlerMapping<Event, Priority, Handled> {

  /**
   * the mapping.
   */
  @NotNull
  Function<Object, Handled> mapping;

  /**
   * the priority.
   */
  @NotNull
  Priority priority;

  /**
   * ctor.
   *
   * @param priority the priority.
   * @param mapping the mapping.
   */
  MergedHandlerMapping(
    @NotNull final Priority priority,
    @NotNull final Function<Event, Handled> mapping
  ) {
    this.priority = priority;
    this.mapping = o -> mapping.apply((Event) o);
  }
}
