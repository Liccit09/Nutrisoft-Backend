package com.nutrisoft.core.component.appointment.application.usecase;

import com.nutrisoft.core.component.appointment.domain.Appointment;
import com.nutrisoft.core.port.out.persistence.appointment.AppointmentRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use Case: Register a virtual meeting link for an appointment.
 *
 * <p>Located in: Core\Components\Appointment\Application
 *
 * <p>Responsibility: Add or update the virtual meeting link for a VIRTUAL mode appointment with
 * business rule validation
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RegisterVirtualMeetingLinkUseCase {

  private final AppointmentRepositoryPort appointmentRepository;

  /**
   * Execute the register virtual meeting link use case.
   *
   * @param appointmentId The appointment ID
   * @param virtualMeetingLink The virtual meeting link (URL or meeting code)
   * @return The updated appointment
   * @throws IllegalArgumentException if appointment not found
   * @throws IllegalStateException if appointment is not in VIRTUAL mode or has invalid status
   */
  public Appointment execute(final UUID appointmentId, final String virtualMeetingLink) {
    log.info("Registering virtual meeting link for appointment: {}", appointmentId);

    Appointment appointment = appointmentRepository.findById(appointmentId)
        .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));

    appointment.registerVirtualMeetingLink(virtualMeetingLink);
    appointmentRepository.save(appointment);

    log.info("Virtual meeting link registered successfully for appointment: {}", appointmentId);

    return appointment;
  }
}

