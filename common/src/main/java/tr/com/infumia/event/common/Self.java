package tr.com.infumia.event.common;

import org.jetbrains.annotations.NotNull;

/**
 * an interface to determine self.
 *
 * @param <S> type of the class.
 */
public interface Self<S extends Self<S>> {
  /**
   * obtains {@code this}.
   *
   * @return {@code this}.
   */
  @NotNull
  S self();
}
