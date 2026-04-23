package com.nutrisoft.core.component.patient.application.usecase;

import com.nutrisoft.core.component.patient.domain.Patient;
import com.nutrisoft.core.port.out.persistence.patient.PatientRepositoryPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case: Get a Patient by ID.
 *
 * <p>Located in: Core\Components\Patient\Application
 *
 * <p>Responsibility: Retrieve a specific patient by its ID
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetPatientByIdUseCase {

  private final PatientRepositoryPort patientRepository;

  /**
   * Execute the get patient by ID use case.
   *
   * @param patientId The patient ID
   * @return The patient response DTO
   */
  public Patient execute(final UUID patientId) {
    log.info("Retrieving patient: {}", patientId);

    return patientRepository
        .findById(patientId)
        .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + patientId));
  }
}
