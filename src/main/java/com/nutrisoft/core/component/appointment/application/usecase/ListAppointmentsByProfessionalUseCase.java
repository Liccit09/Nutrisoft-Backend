package com.nutrisoft.core.component.appointment.application.usecase;

import com.nutrisoft.core.component.appointment.domain.Appointment;
import com.nutrisoft.core.port.out.persistence.appointment.AppointmentRepositoryPort;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case: List appointments by professional ID.
 *
 * <p>Located in: Core\Components\Appointment\Application
 *
 * <p>Responsibility: Retrieve all appointments for a specific professional
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListAppointmentsByProfessionalUseCase {

  private final AppointmentRepositoryPort appointmentRepository;

  /**
   * Execute the list appointments by professional use case.
   *
   * @param professionalId The professional ID
   * @return List of appointments for the professional
   */
  public List<Appointment> execute(final UUID professionalId) {
    log.info("Listing appointments for professional: {}", professionalId);

    return appointmentRepository.findByProfessionalId(professionalId);
  }
}
