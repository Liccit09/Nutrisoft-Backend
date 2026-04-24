package com.nutrisoft.infrastructure.persistence.jpa.appointment.repository;

import com.nutrisoft.infrastructure.persistence.jpa.appointment.entity.AppointmentEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA Repository for Appointment persistence.
 *
 * <p>Located in: Infrastructure\Persistence\JPA\Appointment\Repository
 *
 * <p>Provides database operations for AppointmentEntity.
 */
@Repository
public interface AppointmentJpaRepository extends JpaRepository<AppointmentEntity, UUID> {

  /**
   * Find all appointments for a specific patient.
   *
   * @param patientId The patient ID
   * @return List of appointments
   */
  List<AppointmentEntity> findByPatientId(UUID patientId);

  /**
   * Find all appointments for a specific professional.
   *
   * @param professionalId The professional ID
   * @return List of appointments
   */
  List<AppointmentEntity> findByProfessionalId(UUID professionalId);
}
