package com.nutrisoft.userinterface.api.rest.availability.mapper;

import com.nutrisoft.core.component.schedule.application.dto.AvailableDatesResponseDto;
import com.nutrisoft.core.component.schedule.application.dto.AvailableTimeSlotsResponseDto;
import com.nutrisoft.core.component.schedule.application.dto.TimeSlotDto;
import com.nutrisoft.userinterface.api.rest.appointment.generated.model.AvailableDatesResponse;
import com.nutrisoft.userinterface.api.rest.appointment.generated.model.AvailableTimeSlotsResponse;
import com.nutrisoft.userinterface.api.rest.appointment.generated.model.TimeSlot;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting Availability DTOs to API Response objects.
 *
 * <p>Located in: UserInterface\API\Rest\Availability\Mapper
 *
 * <p>Responsibility: Convert application-level availability DTOs to API-generated response
 * objects for JSON serialization.
 *
 * <p>Conversion Layer: Application → UserInterface
 */
@Component
public class AvailabilityResponseMapper {

  /**
   * Convert AvailableDatesResponseDto to AvailableDatesResponse.
   *
   * @param dto The application-level DTO
   * @return The API response object
   */
  public AvailableDatesResponse toAvailableDatesResponse(
      final AvailableDatesResponseDto dto) {
    final var response = new AvailableDatesResponse();
    response.setAvailableDates(dto.getAvailableDates());
    return response;
  }

  /**
   * Convert AvailableTimeSlotsResponseDto to AvailableTimeSlotsResponse.
   *
   * @param dto The application-level DTO
   * @return The API response object
   */
  public AvailableTimeSlotsResponse toAvailableTimeSlotsResponse(
      final AvailableTimeSlotsResponseDto dto) {
    final var response = new AvailableTimeSlotsResponse();
    response.setServiceId(dto.getServiceId());
    
    // Convert TimeSlotDto objects to TimeSlot API objects
    final var timeSlots = mapAvailableSlotsToTimeSlots(dto.getAvailableSlots());
    response.setAvailableSlots(timeSlots);
    
    return response;
  }

  /**
   * Map available TimeSlotDto slots to TimeSlot API objects.
   *
   * @param availableSlots List of available slots with professional information
   * @return List of TimeSlot objects
   */
  private List<TimeSlot> mapAvailableSlotsToTimeSlots(final List<TimeSlotDto> availableSlots) {
    return availableSlots.stream()
        .map(this::createTimeSlot)
        .toList();
  }

  /**
   * Create a TimeSlot API object from a TimeSlotDto.
   *
   * @param slotDto The slot DTO with start time, end time, and available professionals
   * @return TimeSlot API object
   */
  private TimeSlot createTimeSlot(final TimeSlotDto slotDto) {
    final var timeSlot = new TimeSlot();
    // Convert LocalDateTime to OffsetDateTime in ISO-8601 format (UTC)
    timeSlot.setStartTime(
        OffsetDateTime.of(slotDto.getStartTime(), ZoneId.systemDefault().getRules()
            .getOffset(slotDto.getStartTime())));
    timeSlot.setEndTime(
        OffsetDateTime.of(slotDto.getEndTime(), ZoneId.systemDefault().getRules()
            .getOffset(slotDto.getEndTime())));
    timeSlot.setAvailableProfessionalIds(slotDto.getAvailableProfessionalIds());
    
    return timeSlot;
  }
}

