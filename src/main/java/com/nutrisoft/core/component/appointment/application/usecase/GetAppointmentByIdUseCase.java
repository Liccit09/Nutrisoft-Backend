package com.nutrisoft.core.component.appointment.application.usecase;

import com.nutrisoft.core.component.appointment.domain.Appointment;
import com.nutrisoft.core.port.out.persistence.appointment.AppointmentRepositoryPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case: Get an appointment by ID.
 *
 * <p>Located in: Core\Components\Appointment\Application
 *
 * <p>Responsibility: Retrieve a specific appointment by its ID
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAppointmentByIdUseCase {

  private final AppointmentRepositoryPort appointmentRepository;

  /**
   * Execute the get appointment use case.
   *
   * @param appointmentId The appointment ID
   * @return The appointment
   */
  public Appointment execute(final UUID appointmentId) {
    log.info("Retrieving appointment: {}", appointmentId);

    return appointmentRepository
        .findById(appointmentId)
        .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));
  }
}
