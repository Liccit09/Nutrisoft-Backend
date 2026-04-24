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
 * Use Case: List appointments by patient ID.
 *
 * <p>Located in: Core\Components\Appointment\Application
 *
 * <p>Responsibility: Retrieve all appointments for a specific patient
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListAppointmentsByPatientUseCase {

  private final AppointmentRepositoryPort appointmentRepository;

  /**
   * Execute the list appointments by patient use case.
   *
   * @param patientId The patient ID
   * @return List of appointments for the patient
   */
  public List<Appointment> execute(final UUID patientId) {
    log.info("Listing appointments for patient: {}", patientId);

    return appointmentRepository.findByPatientId(patientId);
  }
}
