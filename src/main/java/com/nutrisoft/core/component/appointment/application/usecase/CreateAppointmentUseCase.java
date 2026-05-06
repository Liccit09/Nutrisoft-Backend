package com.nutrisoft.core.component.appointment.application.usecase;

import com.nutrisoft.core.component.appointment.application.command.AppointmentCommandDto;
import com.nutrisoft.core.component.appointment.domain.Appointment;
import com.nutrisoft.core.component.appointment.domain.AppointmentMode;
import com.nutrisoft.core.component.patient.application.usecase.GetPatientByIdUseCase;
import com.nutrisoft.core.component.professional.application.usecase.GetProfessionalByIdUseCase;
import com.nutrisoft.core.component.schedule.domain.AvailabilityCalculator;
import com.nutrisoft.core.component.service.application.usecase.GetServiceByIdUseCase;
import com.nutrisoft.core.port.out.persistence.appointment.AppointmentRepositoryPort;
import com.nutrisoft.core.port.out.persistence.schedule.ScheduleRepositoryPort;
import com.nutrisoft.core.shared.component.patient.PatientId;
import com.nutrisoft.core.shared.component.professional.ProfessionalId;
import com.nutrisoft.core.shared.component.service.ServiceId;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case: Create a new appointment.
 *
 * <p>Located in: Core\Components\Appointment\Application
 *
 * <p>Responsibility: Orchestrate the creation of a new appointment aggregate, validating
 * availability and handling race conditions.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CreateAppointmentUseCase {

  private final GetPatientByIdUseCase findByIdPatientUseCase;
  private final GetProfessionalByIdUseCase findByIdProfessionalUseCase;
  private final GetServiceByIdUseCase findByIdServiceUseCase;

  private final AppointmentRepositoryPort appointmentRepository;
  private final ScheduleRepositoryPort scheduleRepository;

  /**
   * Executes the use case to create a new appointment based on the provided command DTO.
   *
   * @param commandDto The appointment creation request
   * @return The created appointment
   * @throws IllegalArgumentException if the appointment cannot be booked due to scheduling
   *     constraints
   */
  public Appointment execute(final AppointmentCommandDto commandDto) {
    log.info("Creating new appointment for patient: {}", commandDto.getPatientId());

    ensureEntitiesExist(commandDto);

    // Validate appointment availability (race condition prevention)
    validateAppointmentAvailability(commandDto);

    final var appointment =
        Appointment.create(
            PatientId.of(commandDto.getPatientId()),
            ProfessionalId.of(commandDto.getProfessionalId()),
            ServiceId.of(commandDto.getServiceId()),
            commandDto.getStartTime(),
            Enum.valueOf(AppointmentMode.class, commandDto.getMode()),
            commandDto.getVirtualMeetingLink());

    appointmentRepository.save(appointment);

    log.info("Appointment created successfully with ID: {}", appointment.getId());

    return appointment;
  }

  private void validateAppointmentAvailability(final AppointmentCommandDto commandDto) {
    final var professionalId = ProfessionalId.of(commandDto.getProfessionalId());
    final var startTime = commandDto.getStartTime();

    // Get the professional's schedule
    final var schedule = scheduleRepository.findByProfessionalId(professionalId);

    if (schedule.isEmpty()) {
      throw new IllegalArgumentException(
          "No schedule found for professional: " + commandDto.getProfessionalId());
    }

    // Get the service to check its duration
    final var service = findByIdServiceUseCase.execute(commandDto.getServiceId());
    final var serviceDurationInMinutes = service.getDurationInMinutes();

    // Verify the time slot can accommodate the service duration within working hours
    if (!AvailabilityCalculator.canBookSlot(schedule.get(), startTime, serviceDurationInMinutes)) {
      throw new IllegalArgumentException(
          "The requested time slot is outside working hours or cannot accommodate the service duration");
    }

    // Check for conflicting appointments (race condition prevention)
    final var existingAppointments =
        appointmentRepository.findByProfessionalId(commandDto.getProfessionalId());

    final var appointmentEndTime = startTime.plusMinutes(serviceDurationInMinutes);

    for (final var existingAppointment : existingAppointments) {
      // Skip cancelled appointments
      if (existingAppointment.getStatus().name().equals("CANCELLED")) {
        continue;
      }

      // Get the service duration for the existing appointment
      final var existingService =
          findByIdServiceUseCase.execute(existingAppointment.getServiceId().value());
      final var existingServiceDurationInMinutes = existingService.getDurationInMinutes();

      final var existingStart = existingAppointment.getStartTime();
      final var existingEnd = existingStart.plusMinutes(existingServiceDurationInMinutes);

      // Check for overlap
      if (startTime.isBefore(existingEnd) && appointmentEndTime.isAfter(existingStart)) {
        throw new IllegalArgumentException(
            "The requested time slot conflicts with an existing appointment");
      }
    }

    log.debug(
        "Availability validation passed for professional: {} at time: {}",
        professionalId,
        startTime);
  }

  private void ensureEntitiesExist(final AppointmentCommandDto commandDto) {
    findByIdPatientUseCase.execute(commandDto.getPatientId());
    findByIdProfessionalUseCase.execute(commandDto.getProfessionalId());
    findByIdServiceUseCase.execute(commandDto.getServiceId());
  }
}