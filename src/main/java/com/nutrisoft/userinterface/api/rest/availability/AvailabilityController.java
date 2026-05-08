package com.nutrisoft.userinterface.api.rest.availability;

import com.nutrisoft.core.component.schedule.application.dto.AvailableDatesResponseDto;
import com.nutrisoft.core.component.schedule.application.dto.AvailableTimeSlotsResponseDto;
import com.nutrisoft.core.component.schedule.application.usecase.GetAvailableDatesUseCase;
import com.nutrisoft.core.component.schedule.application.usecase.GetAvailableTimeSlotsUseCase;
import com.nutrisoft.userinterface.api.rest.appointment.generated.AvailabilityApi;
import com.nutrisoft.userinterface.api.rest.appointment.generated.model.AvailableDatesResponse;
import com.nutrisoft.userinterface.api.rest.appointment.generated.model.AvailableTimeSlotsResponse;
import com.nutrisoft.userinterface.api.rest.availability.mapper.AvailabilityResponseMapper;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

/**
 * Implementation of AvailabilityApi interface.
 *
 * <p>Located in: UserInterface\API\REST\Availability
 *
 * <p>This is a PRIMARY/DRIVING ADAPTER that implements the auto-generated AvailabilityApi
 * interface. It directly delegates to individual use case classes which orchestrate the domain
 * logic.
 *
 * <p>The AvailabilityApi interface is auto-generated from the OpenAPI specification
 * (appointments-api-v1.yaml) using the openapi-generator-maven-plugin.
 *
 * <p>Strategy: API-First - The contract (YAML) drives the implementation.
 *
 * <p>All conversions between layers are delegated to specialized mapper classes.
 *
 * <p>Security: This API is public (no authentication required) as defined in the OpenAPI spec
 * with security: []
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class AvailabilityController implements AvailabilityApi {

  private final GetAvailableDatesUseCase getAvailableDatesUseCase;
  private final GetAvailableTimeSlotsUseCase getAvailableTimeSlotsUseCase;
  private final AvailabilityResponseMapper availabilityResponseMapper;

  /**
   * Get available dates for appointment booking.
   *
   * @param startDate The start date (inclusive) - format: yyyy-MM-dd
   * @param endDate The end date (inclusive) - format: yyyy-MM-dd
   * @param serviceId The service ID
   * @param professionalId Optional professional ID. If not provided, returns general availability
   * @return Response with list of available dates
   */
  @Override
  public ResponseEntity<AvailableDatesResponse> getAvailableDates(
      final LocalDate startDate,
      final LocalDate endDate,
      final UUID serviceId,
      final UUID professionalId) {

    log.info(
        "REST: Getting available dates from {} to {} for service: {} (professional: {})",
        startDate,
        endDate,
        serviceId,
        professionalId);

    final var availableDatesDtos = getAvailableDatesUseCase.execute(
        startDate,
        endDate,
        serviceId,
        Optional.ofNullable(professionalId));

    final var response = availabilityResponseMapper.toAvailableDatesResponse(availableDatesDtos);

    return ResponseEntity.ok(response);
  }

  /**
   * Get available time slots for a specific date.
   *
   * @param date The date to check availability for - format: yyyy-MM-dd
   * @param serviceId The service ID
   * @param professionalId Optional professional ID. If not provided, returns general availability
   * @return Response with list of available time slots
   */
  @Override
  public ResponseEntity<AvailableTimeSlotsResponse> getAvailableTimeSlots(
      final LocalDate date,
      final UUID serviceId,
      final UUID professionalId) {

    log.info(
        "REST: Getting available time slots for date: {} and service: {} (professional: {})",
        date,
        serviceId,
        professionalId);

    final var availableSlotsDtos = getAvailableTimeSlotsUseCase.execute(
        date,
        serviceId,
        Optional.ofNullable(professionalId));

    final var response = availabilityResponseMapper.toAvailableTimeSlotsResponse(availableSlotsDtos);

    return ResponseEntity.ok(response);
  }
}
