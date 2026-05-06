package com.nutrisoft.core.component.schedule.application.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Response DTO for available dates query.
 *
 * <p>Located in: Core\Components\Schedule\Application\DTO
 */
@Getter
@AllArgsConstructor
public class AvailableDatesResponseDto {

  private final LocalDate startDate;
  private final LocalDate endDate;
  private final List<LocalDate> availableDates;
}
