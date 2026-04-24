package com.nutrisoft.infrastructure.persistence.jpa.service;

import com.nutrisoft.core.component.service.domain.Service;
import com.nutrisoft.core.port.out.persistence.service.ServiceRepositoryPort;
import com.nutrisoft.infrastructure.persistence.jpa.service.mapper.ServiceMapper;
import com.nutrisoft.infrastructure.persistence.jpa.service.repository.ServiceJpaRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Secondary Adapter: Service Persistence Adapter.
 *
 * <p>Located in: Infrastructure\Persistence\JPA\Service
 *
 * <p>This adapter implements the ServiceRepositoryPort (defined in Core\Ports). Conversion Layer:
 * Domain ↔ Infrastructure (Database)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ServicePersistenceAdapter implements ServiceRepositoryPort {

  private final ServiceJpaRepository serviceJpaRepository;
  private final ServiceMapper serviceMapper;

  @Override
  public void save(final Service service) {
    log.debug("Saving service with ID: {}", service.getId());

    var entity = serviceMapper.toEntity(service);
    serviceJpaRepository.save(entity);

    log.debug("Service saved successfully: {}", service.getId());
  }

  @Override
  public Optional<Service> findById(final UUID id) {
    log.debug("Retrieving service from database with ID: {}", id);

    return serviceJpaRepository.findById(id).map(serviceMapper::toDomain);
  }

  @Override
  public boolean exists(final UUID id) {
    return serviceJpaRepository.existsById(id);
  }
}
