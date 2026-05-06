package com.nutrisoft.core.component.schedule.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DTO representing a time slot with available professionals.
 *
 * <p>Located in: Core\Components\Schedule\Application\DTO
 *
 * <p>Note: startTime and endTime include both date and time information in LocalDateTime format.
 */
@Getter
@AllArgsConstructor
public class TimeSlotDto {

  private final LocalDateTime startTime;
  private final LocalDateTime endTime;
  private final List<UUID> availableProfessionalIds;
}
