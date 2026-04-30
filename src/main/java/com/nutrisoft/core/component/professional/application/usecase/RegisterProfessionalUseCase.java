package com.nutrisoft.core.component.professional.application.usecase;

import com.nutrisoft.core.component.professional.domain.Professional;
import com.nutrisoft.core.port.out.persistence.professional.ProfessionalRepositoryPort;
import com.nutrisoft.core.shared.component.common.ContactInfo;
import com.nutrisoft.core.shared.component.common.Email;
import com.nutrisoft.core.shared.component.professional.ProfessionalId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case: Register a new Professional.
 *
 * <p>Located in: Core\Components\Professional\Application\UseCase
 *
 * <p>Responsibility: Handle ONLY the business logic for professional registration. This use case
 * creates and persists the Professional aggregate.
 *
 * <p>NOTE: Credential creation is NOT the responsibility of this use case. Authentication
 * credentials are managed in the UI layer (AuthenticationService). This keeps the core layer
 * completely independent of authentication infrastructure.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RegisterProfessionalUseCase {

  private final ProfessionalRepositoryPort professionalRepository;

  /**
   * Execute the register professional use case.
   *
   * <p>This method creates a new Professional aggregate and persists it. Email uniqueness
   * validation should be done BEFORE calling this (in AuthenticationService).
   *
   * @param firstName The professional's first name
   * @param lastName The professional's last name
   * @param email The professional's email
   * @param phoneNumber The professional's phone number
   * @param licenseNumber The professional's license number (for auditing/regulatory)
   * @return The registered professional's ID (aggregateId)
   */
  public ProfessionalId execute(
      String firstName, String lastName, String email, String phoneNumber, String licenseNumber) {

    log.info(
        "Registering new professional: {} {} (License: {})", firstName, lastName, licenseNumber);

    // Create the Professional aggregate
    Email emailVO = Email.of(email);
    Professional professional =
        Professional.create(
            firstName.trim(),
            lastName.trim(),
            "General Nutrition", // Default specialization - can be updated later
            ContactInfo.of(phoneNumber, emailVO));

    // Save the professional aggregate
    professionalRepository.save(professional);
    log.info("Professional successfully registered with ID: {}", professional.getId().value());

    return professional.getId();
  }
}
