package com.nutrisoft.infrastructure.persistence.jpa.appointment.mapper;

import com.nutrisoft.core.component.appointment.domain.Appointment;
import com.nutrisoft.core.shared.mapper.ValueObjectMapper;
import com.nutrisoft.infrastructure.persistence.jpa.appointment.entity.AppointmentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting JPA Entities to Domain Objects.
 *
 * <p>Located in: Infrastructure\Persistence\JPA\Appointment\Mapper
 *
 * <p>Responsibility: Convert persistence entities to domain aggregates for business logic
 * operations. Uses MapStruct for efficient mapping. Includes custom logic for complex object
 * reconstruction.
 *
 * <p>Conversion Layer: Infrastructure (Database) → Domain
 */
@Mapper(uses = ValueObjectMapper.class)
public interface AppointmentMapper {

  @Mapping(
      target = "virtualMeetingLink",
      expression = "java(URI.create(entity.getVirtualMeetingLink()))")
  Appointment toDomain(AppointmentEntity entity);

  @Mapping(target = "virtualMeetingLink", expression = "java(appointment.getVirtualMeetingLink().toString())")
  AppointmentEntity toEntity(Appointment appointment);
}
