package com.nutrisoft.core.port.out.persistence.patient;

import com.nutrisoft.core.component.patient.domain.Patient;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port for Patient persistence.
 *
 * <p>Located in: Core\Ports\Persistence\Patient
 *
 * <p>Defines the contract for patient persistence operations. Implemented by the persistence
 * adapter in the infrastructure layer.
 *
 * <p>This port is part of the hexagonal architecture's dependency inversion: - Core depends on
 * ports - Infrastructure implements ports
 */
public interface PatientRepositoryPort {

  /**
   * Save a patient.
   *
   * @param patient The patient to save
   */
  void save(Patient patient);

  /**
   * Find a patient by ID.
   *
   * @param id The patient ID
   * @return The patient if found
   */
  Optional<Patient> findById(UUID id);

  /**
   * Check if a patient exists.
   *
   * @param id The patient ID
   * @return True if patient exists
   */
  boolean exists(UUID id);
}
