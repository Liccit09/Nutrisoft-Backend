package com.nutrisoft.core.component.professional.application.usecase;

import com.nutrisoft.core.component.professional.domain.Professional;
import com.nutrisoft.core.port.out.eventbus.EventBus;
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
 * creates, persists the Professional aggregate, stores the password temporarily, and publishes a
 * ProfessionalRegisteredEvent to trigger credential registration asynchronously.
 *
 * <p>NOTE: Credential creation is NOT done synchronously. Instead, an event is published that will
 * be consumed by an event listener to register credentials asynchronously.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RegisterProfessionalUseCase {

  private final ProfessionalRepositoryPort professionalRepository;
  private final EventBus eventBus;

  /**
   * Execute the register professional use case.
   *
   * <p>This method: 1. Creates a new Professional aggregate 2. Persists it to the database 3.
   * Publishes ProfessionalRegisteredEvent to trigger async credential registration
   *
   * @param firstName The professional's first name
   * @param lastName The professional's last name
   * @param email The professional's email
   * @param phoneNumber The professional's phone number
   * @param specialization The professional's specialization
   * @return The registered professional's ID (aggregateId)
   */
  public ProfessionalId execute(
      String firstName, String lastName, String email, String phoneNumber, String specialization) {

    log.info(
        "Registering new professional: {} {} (Specialization: {})",
        firstName,
        lastName,
        specialization);

    Email emailVO = Email.of(email);
    Professional professional =
        Professional.create(
            firstName.trim(),
            lastName.trim(),
            specialization,
            ContactInfo.of(phoneNumber, emailVO));

    professionalRepository.save(professional);
    log.info("Professional successfully registered with ID: {}", professional.getId().value());

    eventBus.publish(professional.pullDomainEvents());

    return professional.getId();
  }
}
