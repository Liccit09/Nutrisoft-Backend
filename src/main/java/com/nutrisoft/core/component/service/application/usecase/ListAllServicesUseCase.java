package com.nutrisoft.core.component.service.application.usecase;

import com.nutrisoft.core.component.service.domain.Service;
import com.nutrisoft.core.port.out.persistence.service.ServiceRepositoryPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case: List all services.
 *
 * <p>Located in: Core\Components\Service\Application
 *
 * <p>Responsibility: Retrieve all services from the repository
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListAllServicesUseCase {

  private final ServiceRepositoryPort serviceRepository;

  /**
   * Execute the list all services use case.
   *
   * @return List of all services
   */
  public List<Service> execute() {
    log.info("Listing all services");

    return serviceRepository.findAll();
  }
}
