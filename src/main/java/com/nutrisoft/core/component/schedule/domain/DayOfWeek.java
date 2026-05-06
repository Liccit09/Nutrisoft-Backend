package com.nutrisoft.core.component.schedule.domain;

import com.nutrisoft.core.shared.ddd.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/**
 * DayOfWeek Value Object.
 *
 * <p>Represents a day of the week (Monday through Sunday) with working status.
 *
 * <p>Located in: Core\Components\Schedule\Domain
 */
@Getter
@EqualsAndHashCode
public class DayOfWeek implements ValueObject {

  private final java.time.DayOfWeek day;
  private final WorkingHours workingHours;
  private final boolean isWorkingDay;

  public DayOfWeek(
      @NonNull final java.time.DayOfWeek day,
      @NonNull final WorkingHours workingHours,
      final boolean isWorkingDay) {
    this.day = day;
    this.workingHours = workingHours;
    this.isWorkingDay = isWorkingDay;
  }

  /**
   * Creates a DayOfWeek with specified working hours.
   *
   * @param day The day of the week
   * @param workingHours The working hours for this day
   * @return New DayOfWeek instance
   */
  public static DayOfWeek createWorkingDay(
      @NonNull final java.time.DayOfWeek day, @NonNull final WorkingHours workingHours) {
    return new DayOfWeek(day, workingHours, true);
  }

  /**
   * Creates a DayOfWeek as a non-working day (e.g., weekend).
   *
   * @param day The day of the week
   * @return New DayOfWeek instance
   */
  public static DayOfWeek createNonWorkingDay(@NonNull final java.time.DayOfWeek day) {
    // Create dummy working hours for non-working days
    return new DayOfWeek(day, new WorkingHours(java.time.LocalTime.MIDNIGHT, java.time.LocalTime.MIDNIGHT.plusMinutes(1)), false);
  }
}
