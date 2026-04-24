package com.nutrisoft.infrastructure.persistence.jpa.professional.repository;

import com.nutrisoft.infrastructure.persistence.jpa.professional.entity.ProfessionalEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for Professional entities.
 *
 * <p>Located in: Infrastructure\Persistence\JPA\Professional\Repository
 */
@Repository
public interface ProfessionalJpaRepository extends JpaRepository<ProfessionalEntity, UUID> {}
