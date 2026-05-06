package com.nutrisoft.core.port.out.persistence.schedule;

import com.nutrisoft.core.component.schedule.domain.Schedule;
import com.nutrisoft.core.shared.component.professional.ProfessionalId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Output Port: Schedule Repository.
 *
 * <p>Located in: Core\Ports\Out\Persistence\Schedule
 *
 * <p>Defines the interface for persisting and querying Schedule aggregates. This is a Secondary
 * Output Port that abstracts away persistence details.
 */
public interface ScheduleRepositoryPort {

  /**
   * Saves a schedule to persistence.
   *
   * @param schedule The schedule to save
   */
  void save(Schedule schedule);

  /**
   * Finds a schedule by ID.
   *
   * @param id The schedule ID
   * @return Optional containing the schedule, or empty if not found
   */
  Optional<Schedule> findById(UUID id);

  /**
   * Finds a schedule by professional ID.
   *
   * @param professionalId The professional ID
   * @return Optional containing the schedule, or empty if not found
   */
  Optional<Schedule> findByProfessionalId(ProfessionalId professionalId);

  /**
   * Finds all schedules.
   *
   * @return List of all schedules
   */
  List<Schedule> findAll();

  /**
   * Deletes a schedule by ID.
   *
   * @param id The schedule ID
   */
  void delete(UUID id);

  /**
   * Checks if a schedule exists.
   *
   * @param id The schedule ID
   * @return true if exists, false otherwise
   */
  boolean exists(UUID id);
}
