package com.nutrisoft.core.component.appointment.application.usecase;

import com.nutrisoft.core.component.appointment.domain.Appointment;
import com.nutrisoft.core.port.out.persistence.appointment.AppointmentRepositoryPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case: Mark appointment as no-show.
 *
 * <p>Located in: Core\Components\Appointment\Application
 *
 * <p>Responsibility: Transition an appointment to NO_SHOW status
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MarkAppointmentAsNoShowUseCase {

  private final AppointmentRepositoryPort appointmentRepository;

  /**
   * Execute the mark appointment as no-show use case.
   *
   * @param appointmentId The appointment ID
   * @return The no-show appointment
   */
  public Appointment execute(final UUID appointmentId) {
    log.info("Marking appointment as no-show: {}", appointmentId);

    Appointment appointment =
        appointmentRepository
            .findById(appointmentId)
            .orElseThrow(
                () -> new IllegalArgumentException("Appointment not found: " + appointmentId));

    appointment.markAsNoShow();
    appointmentRepository.save(appointment);

    log.info("Appointment marked as no-show: {}", appointmentId);

    return appointment;
  }
}
