package com.nutrisoft.core.component.schedule.domain;

import com.nutrisoft.core.shared.ddd.ValueObject;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/**
 * BreakSlot Value Object.
 *
 * <p>Represents a break period during a professional's working day (e.g., lunch break).
 *
 * <p>Located in: Core\Components\Schedule\Domain
 *
 * <p>Invariants: - Start time must be before end time
 */
@Getter
@EqualsAndHashCode
public class BreakSlot implements ValueObject {

  private final LocalDateTime startTime;
  private final LocalDateTime endTime;
  private final String reason;

  public BreakSlot(
      @NonNull final LocalDateTime startTime,
      @NonNull final LocalDateTime endTime,
      @NonNull final String reason) {
    validateTimeRange(startTime, endTime);
    this.startTime = startTime;
    this.endTime = endTime;
    this.reason = reason;
  }

  private void validateTimeRange(final LocalDateTime startTime, final LocalDateTime endTime) {
    if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
      throw new IllegalArgumentException("Start time must be before end time");
    }
  }

  /**
   * Checks if a specific time falls within this break slot.
   *
   * @param dateTime The date-time to check
   * @return true if the date-time is within the break slot, false otherwise
   */
  public boolean containsDateTime(final LocalDateTime dateTime) {
    return !dateTime.isBefore(startTime) && dateTime.isBefore(endTime);
  }

  /**
   * Checks if a time slot overlaps with this break slot.
   *
   * @param slotStart The start time of the slot
   * @param slotEnd The end time of the slot
   * @return true if there is overlap, false otherwise
   */
  public boolean overlapsWithSlot(final LocalDateTime slotStart, final LocalDateTime slotEnd) {
    return slotStart.isBefore(endTime) && slotEnd.isAfter(startTime);
  }
}
