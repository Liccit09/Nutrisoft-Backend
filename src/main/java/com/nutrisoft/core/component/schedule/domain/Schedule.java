package com.nutrisoft.core.component.schedule.domain;

import com.nutrisoft.core.shared.component.professional.ProfessionalId;
import com.nutrisoft.core.shared.component.schedule.ScheduleId;
import com.nutrisoft.core.shared.ddd.AggregateRoot;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;

/**
 * Schedule Aggregate Root.
 *
 * <p>Manages the working schedule of a professional including working hours, breaks, and special
 * days off.
 *
 * <p>Located in: Core\Components\Schedule\Domain Part of the Schedule Component bounded context
 *
 * <p>Invariants: - A professional can only have one schedule - Schedule must have valid working
 * hours for all days - Breaks cannot overlap with each other
 */
@Getter
public class Schedule extends AggregateRoot<ScheduleId> {

  private final ProfessionalId professionalId;
  private final List<DayOfWeek> weeklySchedule; // 7 elements, one for each day of week
  private final List<BreakSlot> breaks; // Recurring and non-recurring breaks
  private final List<SpecialDay> specialDays; // Holidays, vacation days
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public Schedule(
      @NonNull final ScheduleId id,
      @NonNull final ProfessionalId professionalId,
      @NonNull final List<DayOfWeek> weeklySchedule,
      @NonNull final List<BreakSlot> breaks,
      @NonNull final List<SpecialDay> specialDays,
      @NonNull final LocalDateTime createdAt,
      final LocalDateTime updatedAt) {
    super(id);
    validateWeeklySchedule(weeklySchedule);
    this.professionalId = professionalId;
    this.weeklySchedule = new ArrayList<>(weeklySchedule);
    this.breaks = new ArrayList<>(breaks);
    this.specialDays = new ArrayList<>(specialDays);
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  private void validateWeeklySchedule(final List<DayOfWeek> schedule) {
    if (schedule == null || schedule.size() != 7) {
      throw new IllegalArgumentException("Weekly schedule must have exactly 7 days");
    }
  }

  /**
   * Creates a new Schedule with default working hours.
   *
   * @param professionalId The professional ID
   * @param weeklySchedule The weekly schedule
   * @return New Schedule instance
   */
  public static Schedule create(
      @NonNull final ProfessionalId professionalId,
      @NonNull final List<DayOfWeek> weeklySchedule) {
    final var now = LocalDateTime.now();
    return new Schedule(
        ScheduleId.create(),
        professionalId,
        weeklySchedule,
        new ArrayList<>(),
        new ArrayList<>(),
        now,
        now);
  }

  /**
   * Adds a break slot to the schedule.
   *
   * @param breakSlot The break slot to add
   */
  public void addBreak(@NonNull final BreakSlot breakSlot) {
    validateBreakDoesNotOverlap(breakSlot);
    this.breaks.add(breakSlot);
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Removes a break slot from the schedule.
   *
   * @param breakSlot The break slot to remove
   */
  public void removeBreak(@NonNull final BreakSlot breakSlot) {
    this.breaks.remove(breakSlot);
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Adds a special day off (e.g., holiday, vacation).
   *
   * @param specialDay The special day to add
   */
  public void addSpecialDay(@NonNull final SpecialDay specialDay) {
    this.specialDays.add(specialDay);
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Removes a special day off.
   *
   * @param specialDay The special day to remove
   */
  public void removeSpecialDay(@NonNull final SpecialDay specialDay) {
    this.specialDays.remove(specialDay);
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Gets the working hours for a specific day of the week.
   *
   * @param dayOfWeek The day of the week (0=Sunday, 1=Monday, etc.)
   * @return The working hours, or null if not a working day
   */
  public WorkingHours getWorkingHoursForDay(final java.time.DayOfWeek dayOfWeek) {
    for (final var day : weeklySchedule) {
      if (day.getDay().equals(dayOfWeek) && day.isWorkingDay()) {
        return day.getWorkingHours();
      }
    }
    return null;
  }

  /**
   * Checks if a specific date is a working day.
   *
   * @param date The date to check
   * @return true if the date is a working day, false otherwise
   */
  public boolean isWorkingDay(final java.time.LocalDate date) {
    // Check if it's a special day off
    for (final var specialDay : specialDays) {
      if (specialDay.getDate().equals(date) && !specialDay.isWorkingDay()) {
        return false;
      }
    }

    // Check the regular weekly schedule
    final var dayOfWeek = date.getDayOfWeek();
    for (final var day : weeklySchedule) {
      if (day.getDay().equals(dayOfWeek)) {
        return day.isWorkingDay();
      }
    }

    return false;
  }

  /**
   * Gets all breaks for a specific date.
   *
   * @param date The date to check
   * @return List of breaks for that date
   */
  public List<BreakSlot> getBreaksForDate(final java.time.LocalDate date) {
    return breaks;
  }

  private void validateBreakDoesNotOverlap(final BreakSlot newBreak) {
    for (final var existingBreak : breaks) {
      if (existingBreak.overlapsWithSlot(newBreak.getStartTime(), newBreak.getEndTime())) {
        throw new IllegalArgumentException("Break slot overlaps with an existing break");
      }
    }
  }

  /**
   * Gets an unmodifiable view of the weekly schedule.
   *
   * @return Unmodifiable list of DayOfWeek
   */
  public List<DayOfWeek> getWeeklyScheduleView() {
    return Collections.unmodifiableList(weeklySchedule);
  }

  /**
   * Gets an unmodifiable view of the breaks.
   *
   * @return Unmodifiable list of breaks
   */
  public List<BreakSlot> getBreaksView() {
    return Collections.unmodifiableList(breaks);
  }

  /**
   * Gets an unmodifiable view of the special days.
   *
   * @return Unmodifiable list of special days
   */
  public List<SpecialDay> getSpecialDaysView() {
    return Collections.unmodifiableList(specialDays);
  }
}
