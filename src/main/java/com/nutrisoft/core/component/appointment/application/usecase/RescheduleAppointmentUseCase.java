package com.nutrisoft.core.component.appointment.application.usecase;

import com.nutrisoft.core.component.appointment.domain.Appointment;
import com.nutrisoft.core.port.out.persistence.appointment.AppointmentRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Use Case: Reschedule an existing appointment.
 *
 * <p>Located in: Core\Components\Appointment\Application
 *
 * <p>Responsibility: Change the start time of an existing appointment with business rule validation
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RescheduleAppointmentUseCase {

  private final AppointmentRepositoryPort appointmentRepository;

  /**
   * Execute the reschedule appointment use case.
   *
   * @param appointmentId The appointment ID to reschedule
   * @param newStartTime The new start time for the appointment
   * @return The rescheduled appointment
   * @throws IllegalArgumentException if appointment not found
   * @throws IllegalStateException if appointment cannot be rescheduled (invalid status)
   */
  public Appointment execute(final UUID appointmentId, final LocalDateTime newStartTime) {
    log.info("Rescheduling appointment: {} to new time: {}", appointmentId, newStartTime);

    Appointment appointment = appointmentRepository.findById(appointmentId)
        .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));

    appointment.reschedule(newStartTime);
    appointmentRepository.save(appointment);

    log.info("Appointment rescheduled successfully: {}", appointmentId);

    return appointment;
  }
}

