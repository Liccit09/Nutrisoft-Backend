package com.nutrisoft.infrastructure.persistence.jpa.service.mapper;

import com.nutrisoft.core.component.service.domain.Service;
import com.nutrisoft.core.shared.mapper.ValueObjectMapper;
import com.nutrisoft.infrastructure.persistence.jpa.service.entity.ServiceEntity;
import org.mapstruct.Mapper;

/**
 * Mapper for converting Service Domain to JPA Entity and vice versa.
 *
 * <p>Located in: Infrastructure\Persistence\JPA\Service\Mapper
 *
 * <p>Conversion Layer: Domain ↔ Infrastructure (Database)
 */
@Mapper(uses = ValueObjectMapper.class)
public interface ServiceMapper {

  ServiceEntity toEntity(Service service);

  Service toDomain(ServiceEntity entity);
}
