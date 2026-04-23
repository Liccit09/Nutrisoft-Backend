package com.nutrisoft.core.component.appointment.application.usecase;

import com.nutrisoft.core.component.appointment.domain.Appointment;
import com.nutrisoft.core.port.out.persistence.appointment.AppointmentRepositoryPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case: Mark appointment as completed.
 *
 * <p>Located in: Core\Components\Appointment\Application
 *
 * <p>Responsibility: Transition an appointment to COMPLETED status
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MarkAppointmentAsCompletedUseCase {

  private final AppointmentRepositoryPort appointmentRepository;

  /**
   * Execute the mark appointment as completed use case.
   *
   * @param appointmentId The appointment ID
   * @return The completed appointment
   */
  public Appointment execute(final UUID appointmentId) {
    log.info("Marking appointment as completed: {}", appointmentId);

    Appointment appointment =
        appointmentRepository
            .findById(appointmentId)
            .orElseThrow(
                () -> new IllegalArgumentException("Appointment not found: " + appointmentId));

    appointment.markAsCompleted();
    appointmentRepository.save(appointment);

    log.info("Appointment marked as completed: {}", appointmentId);

    return appointment;
  }
}
