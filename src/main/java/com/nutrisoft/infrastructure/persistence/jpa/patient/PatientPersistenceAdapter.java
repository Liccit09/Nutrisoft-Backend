package com.nutrisoft.infrastructure.persistence.jpa.patient;

import com.nutrisoft.core.component.patient.domain.Patient;
import com.nutrisoft.core.port.out.persistence.patient.PatientRepositoryPort;
import com.nutrisoft.infrastructure.persistence.jpa.patient.mapper.PatientMapper;
import com.nutrisoft.infrastructure.persistence.jpa.patient.repository.PatientJpaRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Secondary Adapter: Patient Persistence Adapter.
 *
 * <p>Located in: Infrastructure\Persistence\JPA\Patient
 *
 * <p>This adapter implements the PatientRepositoryPort (defined in Core\Ports). It translates
 * between domain objects and JPA entities for persistence.
 *
 * <p>Conversion Layer: Domain ↔ Infrastructure (Database)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PatientPersistenceAdapter implements PatientRepositoryPort {

  private final PatientJpaRepository patientJpaRepository;
  private final PatientMapper patientMapper;

  @Override
  public void save(Patient patient) {
    log.debug("Saving patient with ID: {}", patient.getId());

    var entity = patientMapper.toEntity(patient);
    patientJpaRepository.save(entity);

    log.debug("Patient saved successfully: {}", patient.getId());
  }

  @Override
  public Optional<Patient> findById(UUID id) {
    log.debug("Retrieving patient from database with ID: {}", id);

    return patientJpaRepository.findById(id).map(patientMapper::toDomain);
  }

  @Override
  public boolean exists(final UUID id) {
    return patientJpaRepository.existsById(id);
  }
}
