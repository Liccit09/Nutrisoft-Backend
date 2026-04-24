package com.nutrisoft.infrastructure.persistence.jpa.service.repository;

import com.nutrisoft.infrastructure.persistence.jpa.service.entity.ServiceEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for Service entities.
 *
 * <p>Located in: Infrastructure\Persistence\JPA\Service\Repository
 */
@Repository
public interface ServiceJpaRepository extends JpaRepository<ServiceEntity, UUID> {}
