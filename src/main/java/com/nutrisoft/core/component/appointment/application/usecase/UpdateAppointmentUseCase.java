package com.nutrisoft.core.component.appointment.application.usecase;

import com.nutrisoft.core.component.appointment.application.command.AppointmentCommandDto;
import com.nutrisoft.core.component.appointment.domain.Appointment;
import com.nutrisoft.core.port.out.persistence.appointment.AppointmentRepositoryPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case: Update an existing appointment.
 *
 * <p>Located in: Core\Components\Appointment\Application
 *
 * <p>Responsibility: Update appointment details such as time and notes
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UpdateAppointmentUseCase {

  private final AppointmentRepositoryPort appointmentRepository;

  public Appointment execute(
      final UUID appointmentId, final AppointmentCommandDto commandDto) {
    log.info("Updating appointment: {}", appointmentId);

    Appointment appointment =
        appointmentRepository
            .findById(appointmentId)
            .orElseThrow(
                () -> new IllegalArgumentException("Appointment not found: " + appointmentId));

    appointment.reschedule(commandDto.getStartTime());
    appointment.registerVirtualMeetingLink(commandDto.getVirtualMeetingLink());

    appointmentRepository.save(appointment);

    log.info("Appointment updated successfully: {}", appointmentId);

    return appointment;
  }
}
