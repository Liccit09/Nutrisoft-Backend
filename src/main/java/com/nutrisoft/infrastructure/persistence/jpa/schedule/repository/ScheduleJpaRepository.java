package com.nutrisoft.infrastructure.persistence.jpa.schedule.repository;

import com.nutrisoft.infrastructure.persistence.jpa.schedule.entity.ScheduleEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA Repository for Schedule persistence.
 *
 * <p>Located in: Infrastructure\Persistence\JPA\Schedule\Repository
 *
 * <p>Provides database operations for ScheduleEntity.
 */
@Repository
public interface ScheduleJpaRepository extends JpaRepository<ScheduleEntity, UUID> {

  /**
   * Find schedule by professional ID.
   *
   * @param professionalId The professional ID
   * @return Optional containing the schedule
   */
  Optional<ScheduleEntity> findByProfessionalId(UUID professionalId);
}
