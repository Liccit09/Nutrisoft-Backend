package com.nutrisoft.core.port.out.persistence.service;

import com.nutrisoft.core.component.service.domain.Service;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port for Service persistence.
 *
 * <p>Located in: Core\Ports\Persistence\Service
 *
 * <p>Defines the contract for service persistence operations. Implemented by the persistence
 * adapter in the infrastructure layer.
 */
public interface ServiceRepositoryPort {

  /** Save a service. */
  void save(Service service);

  /** Find a service by ID. */
  Optional<Service> findById(UUID id);

  /** Check if a service exists. */
  boolean exists(UUID id);
}
