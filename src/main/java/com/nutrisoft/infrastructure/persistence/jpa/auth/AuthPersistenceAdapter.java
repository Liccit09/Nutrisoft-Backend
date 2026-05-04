package com.nutrisoft.infrastructure.persistence.jpa.auth;

import com.nutrisoft.core.port.out.auth.CredentialRepository;
import com.nutrisoft.core.shared.component.auth.domain.Credential;
import com.nutrisoft.core.shared.component.common.Email;
import com.nutrisoft.infrastructure.persistence.jpa.auth.mapper.CredentialMapper;
import com.nutrisoft.infrastructure.persistence.jpa.auth.repository.CredentialJpaRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Secondary Adapter: Authentication Persistence Adapter.
 *
 * <p>Located in: Infrastructure\Persistence\JPA\Auth
 *
 * <p>This adapter implements the AuthRepositoryPort (defined in Core\Ports). It translates between
 * domain objects and JPA entities for persistence.
 *
 * <p>IMPORTANT: The save method explicitly flushes changes to the database within the current
 * transaction to ensure persistence before event publishing.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthPersistenceAdapter implements CredentialRepository {

  private final CredentialJpaRepository credentialJpaRepository;
  private final CredentialMapper credentialMapper;

  @Override
  public void save(Credential credential) {
    log.debug("Saving credential for email: {}", credential.getEmail().value());
    var entity = credentialMapper.toEntity(credential);
    credentialJpaRepository.save(entity);
    log.debug("Credential saved successfully for email: {}", credential.getEmail().value());
  }

  @Override
  public Optional<Credential> findByEmail(Email email) {
    log.debug("Retrieving credential from database with email: {}", email.value());
    return credentialJpaRepository.findByEmail(email.value()).map(credentialMapper::toDomain);
  }

  @Override
  public Optional<Credential> findByAggregateId(UUID aggregateId) {
    log.debug("Retrieving credential from database with aggregate ID: {}", aggregateId);
    return credentialJpaRepository.findByAggregateId(aggregateId).map(credentialMapper::toDomain);
  }

  @Override
  public boolean existsByEmail(Email email) {
    return credentialJpaRepository.existsByEmail(email.value());
  }

  @Override
  public boolean existsByAggregateId(UUID aggregateId) {
    return credentialJpaRepository.existsByAggregateId(aggregateId);
  }

  @Override
  public void deleteByEmail(Email email) {
    log.debug("Deleting credential with email: {}", email.value());
    credentialJpaRepository.deleteByEmail(email.value());
    log.debug("Credential deleted successfully");
  }
}
