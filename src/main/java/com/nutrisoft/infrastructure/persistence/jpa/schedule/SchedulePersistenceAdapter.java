package com.nutrisoft.infrastructure.persistence.jpa.schedule;

import com.nutrisoft.core.component.schedule.domain.Schedule;
import com.nutrisoft.core.port.out.persistence.schedule.ScheduleRepositoryPort;
import com.nutrisoft.core.shared.component.professional.ProfessionalId;
import com.nutrisoft.infrastructure.persistence.jpa.schedule.mapper.ScheduleMapper;
import com.nutrisoft.infrastructure.persistence.jpa.schedule.repository.ScheduleJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Secondary Adapter: Schedule Persistence Adapter.
 *
 * <p>Located in: Infrastructure\Persistence\JPA\Schedule
 *
 * <p>This adapter implements the ScheduleRepositoryPort (defined in Core\Ports). It translates
 * between domain objects and JPA entities for persistence.
 *
 * <p>All conversions between domain and infrastructure layers are delegated to specialized mapper
 * classes for maintainability and clarity.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulePersistenceAdapter implements ScheduleRepositoryPort {

  private final ScheduleJpaRepository scheduleJpaRepository;
  private final ScheduleMapper scheduleMapper;

  @Override
  public void save(final Schedule schedule) {
    log.debug("Saving schedule for professional: {}", schedule.getProfessionalId());

    final var entity = scheduleMapper.toEntity(schedule);
    scheduleJpaRepository.save(entity);

    log.debug("Schedule saved successfully for professional: {}", schedule.getProfessionalId());
  }

  @Override
  public Optional<Schedule> findById(final UUID id) {
    log.debug("Retrieving schedule with ID: {}", id);

    return scheduleJpaRepository.findById(id).map(scheduleMapper::toDomain);
  }

  @Override
  public Optional<Schedule> findByProfessionalId(final ProfessionalId professionalId) {
    log.debug("Retrieving schedule for professional: {}", professionalId.value());

    return scheduleJpaRepository
        .findByProfessionalId(professionalId.value())
        .map(scheduleMapper::toDomain);
  }

  @Override
  public List<Schedule> findAll() {
    log.debug("Retrieving all schedules");

    return scheduleJpaRepository.findAll().stream().map(scheduleMapper::toDomain).toList();
  }

  @Override
  public void delete(final UUID id) {
    log.debug("Deleting schedule with ID: {}", id);

    scheduleJpaRepository.deleteById(id);

    log.debug("Schedule deleted successfully: {}", id);
  }

  @Override
  public boolean exists(final UUID id) {
    return scheduleJpaRepository.existsById(id);
  }
}
