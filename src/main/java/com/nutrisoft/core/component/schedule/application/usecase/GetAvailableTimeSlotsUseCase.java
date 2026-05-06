package com.nutrisoft.core.component.schedule.application.usecase;

import com.nutrisoft.core.component.appointment.domain.Appointment;
import com.nutrisoft.core.component.schedule.application.dto.AvailableTimeSlotsResponseDto;
import com.nutrisoft.core.component.schedule.application.dto.TimeSlotDto;
import com.nutrisoft.core.component.schedule.domain.AvailabilityCalculator;
import com.nutrisoft.core.port.out.persistence.appointment.AppointmentRepositoryPort;
import com.nutrisoft.core.port.out.persistence.schedule.ScheduleRepositoryPort;
import com.nutrisoft.core.port.out.persistence.service.ServiceRepositoryPort;
import com.nutrisoft.core.shared.component.professional.ProfessionalId;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Use Case: Get available time slots for a specific date.
 *
 * <p>Located in: Core\Components\Schedule\Application\UseCase
 *
 * <p>Responsibility: Query available appointments slots considering professional schedule,
 * service duration, and existing appointments. Returns time slots with information about which
 * professionals are available.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetAvailableTimeSlotsUseCase {

  private final ScheduleRepositoryPort scheduleRepository;
  private final AppointmentRepositoryPort appointmentRepository;
  private final ServiceRepositoryPort serviceRepository;

  /**
   * Get available time slots for a specific date and service.
   *
   * @param date The date to check availability for
   * @param serviceId The service ID (to get duration)
   * @param professionalId Optional - if null, returns general availability from all professionals
   * @return DTO with available time slots including available professionals for each slot
   */
  public AvailableTimeSlotsResponseDto execute(
      final LocalDate date,
      final UUID serviceId,
      final Optional<UUID> professionalId) {

    log.info("Getting available time slots for date: {} and service: {}", date, serviceId);

    // Get service duration
    final var service = serviceRepository.findById(serviceId);
    if (service.isEmpty()) {
      throw new IllegalArgumentException("Service not found: " + serviceId);
    }

    final var serviceDurationInMinutes = service.get().getDurationInMinutes();

    List<TimeSlotDto> availableSlots;

    if (professionalId.isPresent()) {
      // Get availability for specific professional
      availableSlots = getAvailableSlotsForProfessional(
          date,
          professionalId.get(),
          serviceDurationInMinutes);
    } else {
      // Get combined availability from all professionals
      availableSlots = getAvailableSlotsForAllProfessionals(
          date,
          serviceDurationInMinutes);
    }

    return new AvailableTimeSlotsResponseDto(serviceId, availableSlots);
  }

  private List<TimeSlotDto> getAvailableSlotsForProfessional(
      final LocalDate date,
      final UUID professionalId,
      final int serviceDurationInMinutes) {

    log.debug("Getting available slots for professional: {}", professionalId);

    final var schedule = scheduleRepository.findByProfessionalId(ProfessionalId.of(professionalId));

    if (schedule.isEmpty()) {
      log.warn("No schedule found for professional: {}", professionalId);
      return List.of();
    }

    // Get existing appointments for this professional on this date
    final var appointmentsOnDate = getAppointmentsOnDate(professionalId, date);

    final var slots = AvailabilityCalculator.calculateAvailableSlots(
        schedule.get(),
        date,
        serviceDurationInMinutes,
        appointmentsOnDate);

    // Convert LocalTime slots to TimeSlotDto with LocalDateTime (combining date + time)
    return slots.stream()
        .map(startTime -> {
          final var startDateTime = LocalDateTime.of(date, startTime);
          final var endDateTime = startDateTime.plusMinutes(serviceDurationInMinutes);
          return new TimeSlotDto(startDateTime, endDateTime, List.of(professionalId));
        })
        .toList();
  }

  private List<TimeSlotDto> getAvailableSlotsForAllProfessionals(
      final LocalDate date,
      final int serviceDurationInMinutes) {

    log.debug("Getting combined available slots for all professionals");

    // Get all schedules
    final var allSchedules = scheduleRepository.findAll();

    // Map to store which professionals are available for each time slot
    final var slotToProfessionalsMap = new HashMap<LocalTime, List<UUID>>();

    // For each professional, calculate their available slots
    for (final var schedule : allSchedules) {
      final var professionalId = schedule.getProfessionalId().value();
      final var appointmentsOnDate = getAppointmentsOnDate(professionalId, date);

      final var slotsForProfessional = AvailabilityCalculator.calculateAvailableSlots(
          schedule,
          date,
          serviceDurationInMinutes,
          appointmentsOnDate);

      // Add this professional to each slot
      for (final var slot : slotsForProfessional) {
        slotToProfessionalsMap
            .computeIfAbsent(slot, k -> new ArrayList<>())
            .add(professionalId);
      }
    }

    // Convert map to TimeSlotDto list with LocalDateTime (combining date + time)
    return slotToProfessionalsMap.entrySet().stream()
        .map(entry -> {
          final var startDateTime = LocalDateTime.of(date, entry.getKey());
          final var endDateTime = startDateTime.plusMinutes(serviceDurationInMinutes);
          return new TimeSlotDto(startDateTime, endDateTime, entry.getValue());
        })
        .sorted(Comparator.comparing(TimeSlotDto::getStartTime))
        .toList();
  }

  private List<LocalDateTime> getAppointmentsOnDate(final UUID professionalId, final LocalDate date) {
    final var allAppointmentsForProfessional =
        appointmentRepository.findByProfessionalId(professionalId);

    return allAppointmentsForProfessional.stream()
        .filter(appointment -> appointment.getStartTime().toLocalDate().equals(date))
        .map(Appointment::getStartTime)
        .toList();
  }
}
