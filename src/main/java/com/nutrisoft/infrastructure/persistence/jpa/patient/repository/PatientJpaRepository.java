package com.nutrisoft.infrastructure.persistence.jpa.patient.repository;

import com.nutrisoft.infrastructure.persistence.jpa.patient.entity.PatientEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for Patient entities.
 *
 * <p>Located in: Infrastructure\Persistence\JPA\Patient\Repository
 *
 * <p>Provides CRUD and query operations for Patient entities.
 */
@Repository
public interface PatientJpaRepository extends JpaRepository<PatientEntity, UUID> {}
