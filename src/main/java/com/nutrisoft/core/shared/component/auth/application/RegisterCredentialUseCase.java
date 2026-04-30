package com.nutrisoft.core.shared.component.auth.application;

import com.nutrisoft.core.port.out.auth.IdentityManager;
import com.nutrisoft.core.shared.component.auth.domain.Credential;
import com.nutrisoft.core.shared.component.auth.domain.UserRole;
import com.nutrisoft.core.shared.component.common.Email;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case: Register a new Credential.
 *
 * <p>Located in: Core\Shared\Component\Auth\Application\UseCase
 *
 * <p>Responsibility: Handle the business logic for credential registration. This is a
 * shared/transversal use case that orchestrates: 1. Password encoding 2. Credential creation 3.
 * Credential persistence
 *
 * <p>NOTE: This use case is called AFTER the domain aggregate has been created and persisted. The
 * aggregateId must refer to an existing aggregate (Patient, Professional, Admin).
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RegisterCredentialUseCase {

  private final IdentityManager identityManager;
  private final PasswordEncoder passwordEncoder;

  /**
   * Execute the register credential use case.
   *
   * @param aggregateId The ID of the aggregate (Patient, Professional, Admin) that owns this
   *     credential
   * @param email The email (must be unique)
   * @param plainPassword The plain password (will be encoded with BCrypt)
   * @param role The role of the user (PATIENT, PROFESSIONAL, ADMIN)
   * @return The created Credential
   */
  public Credential execute(UUID aggregateId, String email, String plainPassword, UserRole role) {

    log.info("Registering credential for {} with role: {}", email, role);

    // Validate email uniqueness
    Email emailVO = Email.of(email);
    if (identityManager.existsByEmail(emailVO)) {
      log.warn("Credential registration failed: Email already registered - {}", email);
      throw new IllegalArgumentException("Email is already registered");
    }

    // Encode password
    String encodedPassword = passwordEncoder.encode(plainPassword);

    // Create credential VO
    Credential credential = Credential.create(aggregateId, emailVO, encodedPassword, role);

    // Persist credential
    identityManager.save(credential);
    log.info("Credential successfully registered for email: {} with role: {}", email, role);

    return credential;
  }
}
