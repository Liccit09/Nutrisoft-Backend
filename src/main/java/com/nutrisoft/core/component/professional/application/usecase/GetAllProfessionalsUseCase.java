package com.nutrisoft.core.component.professional.application.usecase;

import com.nutrisoft.core.component.professional.domain.Professional;
import com.nutrisoft.core.port.out.persistence.professional.ProfessionalRepositoryPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case: Get all Professionals.
 *
 * <p>Located in: Core\Components\Professional\Application
 *
 * <p>Responsibility: Retrieve all professionals from the database
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAllProfessionalsUseCase {

  private final ProfessionalRepositoryPort professionalRepository;

  /** Execute the get all professionals use case. */
  public List<Professional> execute() {
    log.info("Retrieving all professionals");

    var professionals = professionalRepository.findAll();

    log.info("Retrieved {} professionals", professionals.size());

    return professionals;
  }
}

