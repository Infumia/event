package tr.com.infumia.event.common;

import org.jetbrains.annotations.NotNull;

public interface Self<S extends Self<S>> {
  @NotNull
  S self();
}
