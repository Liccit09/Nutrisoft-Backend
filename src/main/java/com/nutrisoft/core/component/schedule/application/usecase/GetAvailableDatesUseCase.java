package com.nutrisoft.core.component.schedule.application.usecase;

import com.nutrisoft.core.component.schedule.application.dto.AvailableDatesResponseDto;
import com.nutrisoft.core.component.schedule.domain.AvailabilityCalculator;
import com.nutrisoft.core.port.out.persistence.appointment.AppointmentRepositoryPort;
import com.nutrisoft.core.port.out.persistence.schedule.ScheduleRepositoryPort;
import com.nutrisoft.core.port.out.persistence.service.ServiceRepositoryPort;
import com.nutrisoft.core.shared.component.professional.ProfessionalId;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Use Case: Get available dates for appointment booking.
 *
 * <p>Located in: Core\Components\Schedule\Application\UseCase
 *
 * <p>Responsibility: Query available dates within a date range considering professional
 * schedules, service duration, and existing appointments.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetAvailableDatesUseCase {

  private final ScheduleRepositoryPort scheduleRepository;
  private final AppointmentRepositoryPort appointmentRepository;
  private final ServiceRepositoryPort serviceRepository;

  /**
   * Get available dates within a date range.
   *
   * @param startDate The start date (inclusive)
   * @param endDate The end date (inclusive)
   * @param serviceId The service ID (to get duration)
   * @param professionalId Optional - if null, returns general availability
   * @return DTO with available dates
   */
  public AvailableDatesResponseDto execute(
      final LocalDate startDate,
      final LocalDate endDate,
      final UUID serviceId,
      final Optional<UUID> professionalId) {

    log.info(
        "Getting available dates from {} to {} for service: {}",
        startDate,
        endDate,
        serviceId);

    // Validate dates
    if (startDate.isAfter(endDate)) {
      throw new IllegalArgumentException("Start date must be before or equal to end date");
    }

    // Get service duration
    final var service = serviceRepository.findById(serviceId);
    if (service.isEmpty()) {
      throw new IllegalArgumentException("Service not found: " + serviceId);
    }

    final var serviceDurationInMinutes = service.get().getDurationInMinutes();

    // Get appointments grouped by date
    final var appointmentsByDate = buildAppointmentsByDateMap(professionalId);

    List<LocalDate> availableDates;

    if (professionalId.isPresent()) {
      // Get availability for specific professional
      availableDates = getAvailableDatesForProfessional(
          startDate,
          endDate,
          professionalId.get(),
          serviceDurationInMinutes,
          appointmentsByDate);
    } else {
      // Get combined availability from all professionals
      availableDates = getAvailableDatesForAllProfessionals(
          startDate,
          endDate,
          serviceDurationInMinutes,
          appointmentsByDate);
    }

    return new AvailableDatesResponseDto(startDate, endDate, availableDates);
  }

  private List<LocalDate> getAvailableDatesForProfessional(
      final LocalDate startDate,
      final LocalDate endDate,
      final UUID professionalId,
      final int serviceDurationInMinutes,
      final Map<LocalDate, List<LocalDateTime>> appointmentsByDate) {

    log.debug("Getting available dates for professional: {}", professionalId);

    final var schedule = scheduleRepository.findByProfessionalId(ProfessionalId.of(professionalId));

    if (schedule.isEmpty()) {
      log.warn("No schedule found for professional: {}", professionalId);
      return List.of();
    }

    return AvailabilityCalculator.calculateAvailableDates(
        schedule.get(),
        startDate,
        endDate,
        serviceDurationInMinutes,
        appointmentsByDate);
  }

  private List<LocalDate> getAvailableDatesForAllProfessionals(
      final LocalDate startDate,
      final LocalDate endDate,
      final int serviceDurationInMinutes,
      final Map<LocalDate, List<LocalDateTime>> appointmentsByDate) {

    log.debug("Getting combined available dates for all professionals");

    // Get all schedules
    final var allSchedules = scheduleRepository.findAll();

    if (allSchedules.isEmpty()) {
      log.warn("No schedules found in the system");
      return List.of();
    }

    // Combine dates from all professionals (dates where at least one is available)
    final var availableDates = new java.util.TreeSet<LocalDate>();

    for (final var schedule : allSchedules) {
      final var datesForProfessional = AvailabilityCalculator.calculateAvailableDates(
          schedule,
          startDate,
          endDate,
          serviceDurationInMinutes,
          appointmentsByDate);

      availableDates.addAll(datesForProfessional);
    }

    return new java.util.ArrayList<>(availableDates);
  }

  private Map<LocalDate, List<LocalDateTime>> buildAppointmentsByDateMap(
      final Optional<UUID> professionalId) {

    final Map<LocalDate, List<LocalDateTime>> appointmentsByDate = new HashMap<>();

    final List<?> appointments;

    if (professionalId.isPresent()) {
      appointments = appointmentRepository.findByProfessionalId(professionalId.get());
    } else {
      appointments = appointmentRepository.findAll();
    }

    for (final var appointment : appointments) {
      // We need to cast it back to Appointment type
      final var typedAppointment = (com.nutrisoft.core.component.appointment.domain.Appointment) appointment;
      final var date = typedAppointment.getStartTime().toLocalDate();

      appointmentsByDate
          .computeIfAbsent(date, k -> new java.util.ArrayList<>())
          .add(typedAppointment.getStartTime());
    }

    return appointmentsByDate;
  }
}
