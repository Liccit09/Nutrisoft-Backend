package com.nutrisoft.infrastructure.persistence.jpa.appointment;

import com.nutrisoft.core.component.appointment.domain.Appointment;
import com.nutrisoft.core.port.out.persistence.appointment.AppointmentRepositoryPort;
import com.nutrisoft.infrastructure.persistence.jpa.appointment.entity.AppointmentEntity;
import com.nutrisoft.infrastructure.persistence.jpa.appointment.mapper.AppointmentMapper;
import com.nutrisoft.infrastructure.persistence.jpa.appointment.repository.AppointmentJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Secondary Adapter: Appointment Persistence Adapter.
 *
 * <p>Located in: Infrastructure\Persistence\JPA\Appointment
 *
 * <p>This adapter implements the AppointmentRepositoryPort (defined in Core\Ports). It translates
 * between domain objects and JPA entities for persistence.
 *
 * <p>All conversions between domain and infrastructure layers are delegated to specialized
 * MapStruct mappers for maintainability and clarity.
 *
 * <p>The adapter's role is to adapt the JPA repository to the domain's repository interface, acting
 * as a bridge between the core domain and the external persistence mechanism.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AppointmentPersistenceAdapter implements AppointmentRepositoryPort {

  private final AppointmentJpaRepository appointmentJpaRepository;
  private final AppointmentMapper appointmentMapper;

  @Override
  public void save(final Appointment appointment) {
    log.debug("Saving appointment with ID: {}", appointment.getId());

    // Map domain object to persistence entity using mapper
    AppointmentEntity entity = appointmentMapper.toEntity(appointment);
    appointmentJpaRepository.save(entity);

    log.debug("Appointment saved successfully: {}", appointment.getId());
  }

  @Override
  public Optional<Appointment> findById(final UUID id) {
    log.debug("Retrieving appointment from database with ID: {}", id);

    // Map persistence entity to domain object using mapper
    return appointmentJpaRepository.findById(id).map(appointmentMapper::toDomain);
  }

  @Override
  public List<Appointment> findAll() {
    log.debug("Retrieving all appointments from database");

    return appointmentJpaRepository.findAll().stream().map(appointmentMapper::toDomain).toList();
  }

  @Override
  public List<Appointment> findByPatientId(final UUID patientId) {
    log.debug("Retrieving appointments for patient: {}", patientId);

    return appointmentJpaRepository.findByPatientId(patientId).stream()
        .map(appointmentMapper::toDomain)
        .toList();
  }

  @Override
  public List<Appointment> findByProfessionalId(final UUID professionalId) {
    log.debug("Retrieving appointments for professional: {}", professionalId);

    return appointmentJpaRepository.findByProfessionalId(professionalId).stream()
        .map(appointmentMapper::toDomain)
        .toList();
  }

  @Override
  public void delete(final UUID id) {
    log.debug("Deleting appointment with ID: {}", id);

    appointmentJpaRepository.deleteById(id);

    log.debug("Appointment deleted successfully: {}", id);
  }

  @Override
  public boolean exists(final UUID id) {
    return appointmentJpaRepository.existsById(id);
  }
}
