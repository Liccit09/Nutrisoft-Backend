package com.nutrisoft.core.port.out.auth;

import com.nutrisoft.core.shared.component.auth.domain.Credential;
import com.nutrisoft.core.shared.component.common.Email;
import java.util.Optional;
import java.util.UUID;

/**
 * Authentication Repository Port.
 *
 * <p>Located in: Core\Ports\Persistence\Auth
 *
 * <p>Defines the contract for authentication-related persistence operations. Implemented by the
 * persistence adapter in the infrastructure layer.
 *
 * <p>This port is part of the hexagonal architecture's dependency inversion: - Core depends on
 * ports - Infrastructure implements ports
 */
public interface CredentialRepository {

  /**
   * Save or update a credential.
   *
   * @param credential The credential to save
   */
  void save(Credential credential);

  /**
   * Find a credential by email.
   *
   * @param email The email to search for
   * @return Optional containing the credential if found
   */
  Optional<Credential> findByEmail(Email email);

  /**
   * Find a credential by aggregate ID (Patient, Professional, etc.).
   *
   * @param aggregateId The ID of the aggregate
   * @return Optional containing the credential if found
   */
  Optional<Credential> findByAggregateId(UUID aggregateId);

  /**
   * Check if a credential exists for the given email.
   *
   * @param email The email to check
   * @return True if credential exists
   */
  boolean existsByEmail(Email email);

  /**
   * Check if a credential exists for the given aggregate ID.
   *
   * @param aggregateId The aggregate ID to check
   * @return True if credential exists
   */
  boolean existsByAggregateId(UUID aggregateId);

  /**
   * Delete a credential by email.
   *
   * @param email The email of the credential to delete
   */
  void deleteByEmail(Email email);
}
