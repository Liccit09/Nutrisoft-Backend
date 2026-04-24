package com.nutrisoft.core.component.appointment.application.usecase;

import com.nutrisoft.core.component.appointment.domain.Appointment;
import com.nutrisoft.core.port.out.persistence.appointment.AppointmentRepositoryPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case: Cancel an appointment.
 *
 * <p>Located in: Core\Components\Appointment\Application
 *
 * <p>Responsibility: Transition an appointment to CANCELLED status
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CancelAppointmentUseCase {

  private final AppointmentRepositoryPort appointmentRepository;

  /**
   * Execute the cancel appointment use case.
   *
   * @param appointmentId The appointment ID to cancel
   * @return The cancelled appointment
   */
  public Appointment execute(final UUID appointmentId) {
    log.info("Cancelling appointment: {}", appointmentId);

    Appointment appointment =
        appointmentRepository
            .findById(appointmentId)
            .orElseThrow(
                () -> new IllegalArgumentException("Appointment not found: " + appointmentId));

    appointment.cancel();
    appointmentRepository.save(appointment);

    log.info("Appointment cancelled successfully: {}", appointmentId);

    return appointment;
  }
}
