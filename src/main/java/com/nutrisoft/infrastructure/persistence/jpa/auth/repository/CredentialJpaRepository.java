package com.nutrisoft.infrastructure.persistence.jpa.auth.repository;

import com.nutrisoft.infrastructure.persistence.jpa.auth.entity.CredentialEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA Repository for Credential Entity.
 *
 * <p>Located in: Infrastructure\Persistence\JPA\Auth\Repository
 */
@Repository
public interface CredentialJpaRepository extends JpaRepository<CredentialEntity, UUID> {

  /** Find a credential by email. */
  Optional<CredentialEntity> findByEmail(String email);

  /** Find a credential by aggregate ID. */
  Optional<CredentialEntity> findByAggregateId(UUID aggregateId);

  /** Check if a credential exists for the given email. */
  boolean existsByEmail(String email);

  /** Check if a credential exists for the given aggregate ID. */
  boolean existsByAggregateId(UUID aggregateId);

  /** Delete a credential by email. */
  void deleteByEmail(String email);
}
