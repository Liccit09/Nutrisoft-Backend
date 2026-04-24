package com.nutrisoft.core.port.out.persistence.professional;

import com.nutrisoft.core.component.professional.domain.Professional;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port for Professional persistence.
 *
 * <p>Located in: Core\Ports\Persistence\Professional
 *
 * <p>Defines the contract for professional persistence operations. Implemented by the persistence
 * adapter in the infrastructure layer.
 */
public interface ProfessionalRepositoryPort {

  /** Save a professional. */
  void save(Professional professional);

  /** Find a professional by ID. */
  Optional<Professional> findById(UUID id);

  /** Check if a professional exists. */
  boolean exists(UUID id);
}
