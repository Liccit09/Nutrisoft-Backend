package com.nutrisoft.core.component.patient.application.usecase;

import com.nutrisoft.core.component.patient.domain.Patient;
import com.nutrisoft.core.port.out.persistence.patient.PatientRepositoryPort;
import com.nutrisoft.core.shared.component.common.ContactInfo;
import com.nutrisoft.core.shared.component.common.Email;
import com.nutrisoft.core.shared.component.patient.PatientId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case: Register a new Patient.
 *
 * <p>Located in: Core\Components\Patient\Application\UseCase
 *
 * <p>Responsibility: Handle ONLY the business logic for patient registration. This use case creates
 * and persists the Patient aggregate.
 *
 * <p>NOTE: Credential creation is NOT the responsibility of this use case. Authentication
 * credentials are managed in the UI layer (AuthenticationService). This keeps the core layer
 * completely independent of authentication infrastructure.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RegisterPatientUseCase {

  private final PatientRepositoryPort patientRepository;

  /**
   * Execute the register patient use case.
   *
   * <p>This method creates a new Patient aggregate and persists it. Email uniqueness validation
   * should be done BEFORE calling this (in AuthenticationService).
   *
   * @param firstName The patient's first name
   * @param lastName The patient's last name
   * @param email The patient's email
   * @param phoneNumber The patient's phone number
   * @return The registered patient's ID (aggregateId)
   */
  public PatientId execute(String firstName, String lastName, String email, String phoneNumber) {

    log.info("Registering new patient: {} {}", firstName, lastName);

    // Create the Patient aggregate
    Email emailVO = Email.of(email);
    Patient patient =
        Patient.create(
            firstName.trim(),
            lastName.trim(),
            null, // dateOfBirth (optional for initial registration)
            ContactInfo.of(phoneNumber, emailVO),
            null // address (optional for initial registration)
            );

    // Save the patient aggregate
    patientRepository.save(patient);
    log.info("Patient successfully registered with ID: {}", patient.getId().value());

    return patient.getId();
  }
}
