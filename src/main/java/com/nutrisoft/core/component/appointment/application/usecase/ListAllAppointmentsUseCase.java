package com.nutrisoft.core.component.appointment.application.usecase;

import com.nutrisoft.core.component.appointment.domain.Appointment;
import com.nutrisoft.core.port.out.persistence.appointment.AppointmentRepositoryPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case: List all appointments.
 *
 * <p>Located in: Core\Components\Appointment\Application
 *
 * <p>Responsibility: Retrieve all appointments from the repository
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListAllAppointmentsUseCase {

  private final AppointmentRepositoryPort appointmentRepository;

  /**
   * Execute the list all appointments use case.
   *
   * @return List of all appointments
   */
  public List<Appointment> execute() {
    log.info("Listing all appointments");

    return appointmentRepository.findAll();
  }
}
