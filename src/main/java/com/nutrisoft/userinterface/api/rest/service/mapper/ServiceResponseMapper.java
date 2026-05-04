package com.nutrisoft.userinterface.api.rest.service.mapper;

import com.nutrisoft.core.component.service.domain.Service;
import com.nutrisoft.core.shared.mapper.ValueObjectMapper;
import com.nutrisoft.userinterface.api.rest.service.generated.model.ServiceResponse;
import org.mapstruct.Mapper;

/**
 * Mapper for converting Service Domain objects to API ServiceResponse DTOs.
 *
 * <p>Located in: UserInterface\API\Rest\Service\Mapper
 *
 * <p>Conversion Layer: Domain ↔ API (REST)
 */
@Mapper(uses = ValueObjectMapper.class)
public interface ServiceResponseMapper {

  ServiceResponse toResponse(Service service);
}
