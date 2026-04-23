package com.nutrisoft.core.component.professional.application.usecase;

import com.nutrisoft.core.component.professional.domain.Professional;
import com.nutrisoft.core.port.out.persistence.professional.ProfessionalRepositoryPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case: Get a Professional by ID.
 *
 * <p>Located in: Core\Components\Professional\Application
 *
 * <p>Responsibility: Retrieve a specific professional by its ID
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetProfessionalByIdUseCase {

  private final ProfessionalRepositoryPort professionalRepository;

  /** Execute the get professional by ID use case. */
  public Professional execute(final UUID professionalId) {
    log.info("Retrieving professional: {}", professionalId);

    return professionalRepository
        .findById(professionalId)
        .orElseThrow(
            () -> new IllegalArgumentException("Professional not found: " + professionalId));
  }
}
