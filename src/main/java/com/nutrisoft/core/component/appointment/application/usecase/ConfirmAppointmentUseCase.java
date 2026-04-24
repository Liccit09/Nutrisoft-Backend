package com.nutrisoft.core.component.appointment.application.usecase;

import com.nutrisoft.core.component.appointment.domain.Appointment;
import com.nutrisoft.core.port.out.persistence.appointment.AppointmentRepositoryPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case: Confirm an appointment.
 *
 * <p>Located in: Core\Components\Appointment\Application
 *
 * <p>Responsibility: Transition an appointment from SCHEDULED to CONFIRMED
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ConfirmAppointmentUseCase {

  private final AppointmentRepositoryPort appointmentRepository;

  /**
   * Executes the use case to confirm an appointment by its ID. Validates the appointment exists.
   *
   * @param appointmentId The appointment ID to confirm
   * @return The confirmed appointment
   */
  public Appointment execute(final UUID appointmentId) {
    log.info("Confirming appointment: {}", appointmentId);

    Appointment appointment =
        appointmentRepository
            .findById(appointmentId)
            .orElseThrow(
                () -> new IllegalArgumentException("Appointment not found: " + appointmentId));

    appointment.confirm();
    appointmentRepository.save(appointment);

    log.info("Appointment confirmed successfully: {}", appointmentId);

    return appointment;
  }
}
