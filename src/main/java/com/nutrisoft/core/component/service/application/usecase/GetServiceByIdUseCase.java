package com.nutrisoft.core.component.service.application.usecase;

import com.nutrisoft.core.component.service.domain.Service;
import com.nutrisoft.core.port.out.persistence.service.ServiceRepositoryPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case: Get a Service by ID.
 *
 * <p>Located in: Core\Components\Service\Application
 *
 * <p>Responsibility: Retrieve a specific service by its ID
 */
@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetServiceByIdUseCase {

  private final ServiceRepositoryPort serviceRepository;

  /** Execute the get service by ID use case. */
  public Service execute(final UUID serviceId) {
    log.info("Retrieving service: {}", serviceId);

    return serviceRepository
        .findById(serviceId)
        .orElseThrow(() -> new IllegalArgumentException("Service not found: " + serviceId));
  }
}
