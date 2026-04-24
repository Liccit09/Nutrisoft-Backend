package com.nutrisoft.infrastructure.persistence.jpa.professional;

import com.nutrisoft.core.component.professional.domain.Professional;
import com.nutrisoft.core.port.out.persistence.professional.ProfessionalRepositoryPort;
import com.nutrisoft.infrastructure.persistence.jpa.professional.mapper.ProfessionalMapper;
import com.nutrisoft.infrastructure.persistence.jpa.professional.repository.ProfessionalJpaRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Secondary Adapter: Professional Persistence Adapter.
 *
 * <p>Located in: Infrastructure\Persistence\JPA\Professional
 *
 * <p>This adapter implements the ProfessionalRepositoryPort (defined in Core\Ports). Conversion
 * Layer: Domain ↔ Infrastructure (Database)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProfessionalPersistenceAdapter implements ProfessionalRepositoryPort {

  private final ProfessionalJpaRepository professionalJpaRepository;
  private final ProfessionalMapper professionalMapper;

  @Override
  public void save(final Professional professional) {
    log.debug("Saving professional with ID: {}", professional.getId());

    var entity = professionalMapper.toEntity(professional);
    professionalJpaRepository.save(entity);

    log.debug("Professional saved successfully: {}", professional.getId());
  }

  @Override
  public Optional<Professional> findById(final UUID id) {
    log.debug("Retrieving professional from database with ID: {}", id);

    return professionalJpaRepository.findById(id).map(professionalMapper::toDomain);
  }

  @Override
  public boolean exists(final UUID id) {
    return professionalJpaRepository.existsById(id);
  }
}
