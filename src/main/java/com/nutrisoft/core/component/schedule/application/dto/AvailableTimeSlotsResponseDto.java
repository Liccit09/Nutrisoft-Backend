package com.nutrisoft.core.component.schedule.application.dto;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Response DTO for available time slots query.
 *
 * <p>Located in: Core\Components\Schedule\Application\DTO
 *
 * <p>Contains a list of available time slots (each with full date-time) and the service ID.
 */
@Getter
@AllArgsConstructor
public class AvailableTimeSlotsResponseDto {

  private final UUID serviceId;
  private final List<TimeSlotDto> availableSlots;
}
