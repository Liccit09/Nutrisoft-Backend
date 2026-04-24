package com.nutrisoft.infrastructure.persistence.jpa.patient.mapper;

import com.nutrisoft.core.component.patient.domain.Patient;
import com.nutrisoft.core.shared.mapper.ValueObjectMapper;
import com.nutrisoft.infrastructure.persistence.jpa.patient.entity.PatientEntity;
import org.mapstruct.Mapper;

/**
 * Mapper for converting Patient Domain to JPA Entity and vice versa.
 *
 * <p>Located in: Infrastructure\Persistence\JPA\Patient\Mapper
 *
 * <p>Responsibility: Convert between domain aggregates and persistence entities.
 *
 * <p>Conversion Layer: Domain ↔ Infrastructure (Database)
 */
@Mapper(uses = ValueObjectMapper.class)
public interface PatientMapper {

  PatientEntity toEntity(Patient patient);

  Patient toDomain(PatientEntity entity);
}
