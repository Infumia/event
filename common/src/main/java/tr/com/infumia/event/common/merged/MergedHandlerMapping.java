package tr.com.infumia.event.common.merged;

import org.jetbrains.annotations.NotNull;

public interface MergedHandlerMapping<Merged, Priority, Handled> {
  void failed(@NotNull Merged event, @NotNull Throwable error);

  @NotNull
  Handled map(@NotNull Object object);

  @NotNull
  Priority priority();
}
