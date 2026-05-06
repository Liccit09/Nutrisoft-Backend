package com.nutrisoft.core.component.schedule.domain;

import com.nutrisoft.core.shared.ddd.ValueObject;
import java.time.LocalTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/**
 * WorkingHours Value Object.
 *
 * <p>Represents the working hours (start time and end time) for a specific day or time period.
 *
 * <p>Located in: Core\Components\Schedule\Domain
 *
 * <p>Invariants: - Start time must be before end time
 */
@Getter
@EqualsAndHashCode
public class WorkingHours implements ValueObject {

  private final LocalTime startTime;
  private final LocalTime endTime;

  public WorkingHours(@NonNull final LocalTime startTime, @NonNull final LocalTime endTime) {
    validateTimeRange(startTime, endTime);
    this.startTime = startTime;
    this.endTime = endTime;
  }

  private void validateTimeRange(final LocalTime startTime, final LocalTime endTime) {
    if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
      throw new IllegalArgumentException("Start time must be before end time");
    }
  }

  /**
   * Calculates the duration in minutes between start and end times.
   *
   * @return Duration in minutes
   */
  public int getDurationInMinutes() {
    return (int) java.time.temporal.ChronoUnit.MINUTES.between(startTime, endTime);
  }

  /**
   * Checks if a specific time falls within these working hours.
   *
   * @param time The time to check
   * @return true if the time is within working hours, false otherwise
   */
  public boolean containsTime(final LocalTime time) {
    return !time.isBefore(startTime) && time.isBefore(endTime);
  }

  /**
   * Checks if a specific time slot (start + duration) fits within these working hours.
   *
   * @param slotStart The start time of the slot
   * @param durationInMinutes The duration of the slot in minutes
   * @return true if the slot fits, false otherwise
   */
  public boolean canFitSlot(final LocalTime slotStart, final int durationInMinutes) {
    if (!containsTime(slotStart)) {
      return false;
    }

    final var slotEnd = slotStart.plusMinutes(durationInMinutes);
    return !slotEnd.isAfter(endTime);
  }
}
