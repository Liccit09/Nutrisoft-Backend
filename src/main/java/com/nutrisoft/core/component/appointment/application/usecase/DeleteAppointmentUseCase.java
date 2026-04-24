package com.nutrisoft.core.component.appointment.application.usecase;

import com.nutrisoft.core.component.appointment.domain.Appointment;
import com.nutrisoft.core.port.out.persistence.appointment.AppointmentRepositoryPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case: Delete an appointment.
 *
 * <p>Located in: Core\Components\Appointment\Application
 *
 * <p>Responsibility: Delete an appointment with business rule validation
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DeleteAppointmentUseCase {

  private final AppointmentRepositoryPort appointmentRepository;

  /**
   * Execute the delete appointment use case.
   *
   * @param appointmentId The appointment ID to delete
   */
  public void execute(final UUID appointmentId) {
    log.info("Deleting appointment: {}", appointmentId);

    Appointment appointment =
        appointmentRepository
            .findById(appointmentId)
            .orElseThrow(
                () -> new IllegalArgumentException("Appointment not found: " + appointmentId));

    // Business rule: Cannot delete completed or no-show appointments
    if (appointment.getStatus().isCompleted() || appointment.getStatus().isNoShow()) {
      throw new IllegalStateException(
          "Cannot delete appointment with status: " + appointment.getStatus());
    }

    appointmentRepository.delete(appointmentId);

    log.info("Appointment deleted successfully: {}", appointmentId);
  }
}
