package tr.com.infumia.event.common.merged;

import java.util.function.BiConsumer;
import java.util.function.Function;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
@SuppressWarnings("unchecked")
final class MergedHandlerMappingImpl<Merged, Priority, Handled>
  implements MergedHandlerMapping<Merged, Priority, Handled> {

  @NotNull
  private final BiConsumer<Merged, Throwable> exceptionConsumer;

  @NotNull
  private final Function<Object, Handled> mapping;

  @NotNull
  private final Class<Merged> mergedClass;

  @NotNull
  private final Priority priority;

  MergedHandlerMappingImpl(
    @NotNull final Class<Merged> mergedClass,
    @NotNull final Priority priority,
    @NotNull final Function<Merged, Handled> mapping,
    @NotNull final BiConsumer<Merged, Throwable> exceptionConsumer
  ) {
    this.mergedClass = mergedClass;
    this.priority = priority;
    this.mapping = o -> mapping.apply((Merged) o);
    this.exceptionConsumer = exceptionConsumer;
  }

  @Override
  public void failed(@NotNull final Merged event, @NotNull final Throwable error) {
    this.exceptionConsumer.accept(event, error);
  }

  @NotNull
  @Override
  public Handled map(@NotNull final Object object) {
    return this.mapping.apply(object);
  }
}
