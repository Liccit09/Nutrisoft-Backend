package com.nutrisoft.infrastructure.persistence.jpa.professional.mapper;

import com.nutrisoft.core.component.professional.domain.Professional;
import com.nutrisoft.core.shared.mapper.ValueObjectMapper;
import com.nutrisoft.infrastructure.persistence.jpa.professional.entity.ProfessionalEntity;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting Professional Domain to JPA Entity and vice versa.
 *
 * <p>Located in: Infrastructure\Persistence\JPA\Professional\Mapper
 *
 * <p>Conversion Layer: Domain ↔ Infrastructure (Database)
 */
@Mapper(uses = ValueObjectMapper.class)
public interface ProfessionalMapper {

  @Mapping(target = "email", source = "contactInfo.email.value")
  @Mapping(target = "phoneNumber", source = "contactInfo.phoneNumber")
  ProfessionalEntity toEntity(Professional professional);

  @InheritInverseConfiguration
  Professional toDomain(ProfessionalEntity entity);
}
