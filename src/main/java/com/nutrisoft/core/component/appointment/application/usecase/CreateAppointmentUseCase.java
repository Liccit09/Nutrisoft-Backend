package com.nutrisoft.core.component.appointment.application.usecase;

import com.nutrisoft.core.component.appointment.application.command.AppointmentCommandDto;
import com.nutrisoft.core.component.appointment.domain.Appointment;
import com.nutrisoft.core.component.appointment.domain.AppointmentMode;
import com.nutrisoft.core.port.out.persistence.appointment.AppointmentRepositoryPort;
import com.nutrisoft.core.shared.component.patient.PatientId;
import com.nutrisoft.core.shared.component.professional.ProfessionalId;
import com.nutrisoft.core.shared.component.service.ServiceId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case: Create a new appointment.
 *
 * <p>Located in: Core\Components\Appointment\Application
 *
 * <p>Responsibility: Orchestrate the creation of a new appointment aggregate
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CreateAppointmentUseCase {

  private final AppointmentRepositoryPort appointmentRepository;

  /**
   * Executes the use case to create a new appointment based on the provided command DTO.
   *
   * @param commandDto The appointment creation request
   * @return The created appointment
   */
  public Appointment execute(final AppointmentCommandDto commandDto) {
    log.info("Creating new appointment for patient: {}", commandDto.getPatientId());

    // TODO: Ensure that related entities exist (Patient, Professional, Service)

    final var appointment =
        Appointment.create(
            PatientId.of(commandDto.getPatientId()),
            ProfessionalId.of(commandDto.getProfessionalId()),
            ServiceId.of(commandDto.getServiceId()),
            commandDto.getStartTime(),
            AppointmentMode.fromString(commandDto.getMode()),
            commandDto.getVirtualMeetingLink());

    appointmentRepository.save(appointment);

    log.info("Appointment created successfully with ID: {}", appointment.getId());

    return appointment;
  }
}
