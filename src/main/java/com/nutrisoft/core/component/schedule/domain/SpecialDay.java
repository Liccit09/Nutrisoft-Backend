package com.nutrisoft.core.component.schedule.domain;

import com.nutrisoft.core.shared.ddd.ValueObject;
import java.time.LocalDate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/**
 * SpecialDay Value Object.
 *
 * <p>Represents a special day such as a holiday or vacation day when the professional is not
 * available.
 *
 * <p>Located in: Core\Components\Schedule\Domain
 */
@Getter
@EqualsAndHashCode
public class SpecialDay implements ValueObject {

  private final LocalDate date;
  private final String reason;
  private final boolean isWorkingDay; // false = day off, true = working despite normal schedule

  public SpecialDay(
      @NonNull final LocalDate date,
      @NonNull final String reason,
      final boolean isWorkingDay) {
    this.date = date;
    this.reason = reason;
    this.isWorkingDay = isWorkingDay;
  }

  /**
   * Creates a special day off (holiday, vacation).
   *
   * @param date The date
   * @param reason The reason for the day off
   * @return New SpecialDay instance
   */
  public static SpecialDay createDayOff(
      @NonNull final LocalDate date, @NonNull final String reason) {
    return new SpecialDay(date, reason, false);
  }

  /**
   * Creates a special working day (e.g., working during weekend).
   *
   * @param date The date
   * @param reason The reason for the special work day
   * @return New SpecialDay instance
   */
  public static SpecialDay createWorkingDay(
      @NonNull final LocalDate date, @NonNull final String reason) {
    return new SpecialDay(date, reason, true);
  }
}
