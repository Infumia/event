package tr.com.infumia.event.common;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
class Internal {

  @NotNull
  ChronoUnit toChronoUnit(@NotNull TimeUnit unit) {
    switch (unit) {
      case NANOSECONDS:
        return ChronoUnit.NANOS;
      case MICROSECONDS:
        return ChronoUnit.MICROS;
      case MILLISECONDS:
        return ChronoUnit.MILLIS;
      case SECONDS:
        return ChronoUnit.SECONDS;
      case MINUTES:
        return ChronoUnit.MINUTES;
      case HOURS:
        return ChronoUnit.HOURS;
      case DAYS:
        return ChronoUnit.DAYS;
      default:
        throw new AssertionError();
    }
  }
}
