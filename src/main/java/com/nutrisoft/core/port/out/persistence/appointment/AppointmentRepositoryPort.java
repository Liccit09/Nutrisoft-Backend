package com.nutrisoft.core.port.out.persistence.appointment;

import com.nutrisoft.core.component.appointment.domain.Appointment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Appointment Repository Port. This is a secondary/driven port that defines how the domain needs to
 * persist Appointment aggregates.
 *
 * <p>Located in: Ports Layer - Core This interface abstracts the persistence mechanism from the
 * domain. Implementations are located in: Infrastructure\Persistence\JPA\Appointment
 *
 * <p>The actual implementation is in the Infrastructure layer, and it adapts this port to use a
 * specific tool (JPA/Hibernate).
 */
public interface AppointmentRepositoryPort {

  /**
   * Save or update an appointment.
   *
   * @param appointment The appointment to save
   */
  void save(Appointment appointment);

  /**
   * Find an appointment by ID.
   *
   * @param id The appointment ID
   * @return Optional containing the appointment if found
   */
  Optional<Appointment> findById(UUID id);

  /**
   * Find all appointments.
   *
   * @return List of all appointments
   */
  List<Appointment> findAll();

  /**
   * Find all appointments for a specific patient.
   *
   * @param patientId The patient ID
   * @return List of appointments for the patient
   */
  List<Appointment> findByPatientId(UUID patientId);

  /**
   * Find all appointments for a specific professional.
   *
   * @param professionalId The professional ID
   * @return List of appointments for the professional
   */
  List<Appointment> findByProfessionalId(UUID professionalId);

  /**
   * Delete an appointment by ID.
   *
   * @param id The appointment ID
   */
  void delete(UUID id);

  /**
   * Check if an appointment exists by ID.
   *
   * @param id The appointment ID
   * @return true if appointment exists, false otherwise
   */
  boolean exists(UUID id);
}
