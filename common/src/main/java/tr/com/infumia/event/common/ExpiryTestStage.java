package tr.com.infumia.event.common;

/**
 * an enum class that contains expiry test stages.
 */
public enum ExpiryTestStage {
  /**
   * tests before event filters and handlers.
   */
  PRE,
  /**
   * tests after event filters.
   */
  POST_FILTER,
  /**
   * tests after event handlers.
   */
  POST_HANDLE,
}
