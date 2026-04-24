package com.nutrisoft.userinterface.api.rest.appointment.mapper;

import com.nutrisoft.core.component.appointment.application.command.AppointmentCommandDto;
import com.nutrisoft.core.shared.mapper.DateTimeMapper;
import com.nutrisoft.userinterface.api.rest.appointment.generated.model.CreateAppointmentRequest;
import org.mapstruct.Mapper;

/**
 * Mapper for converting API Appointment Request DTOs to Application CommandDto.
 *
 * <p>Located in: UserInterface\API\Rest\Appointment\Mapper
 *
 * <p>Responsibility: Convert incoming API requests to domain command DTOs Handles both create and
 * update operations for appointments. Uses MapStruct for efficient runtime mapping.
 *
 * <p>Conversion Layer: UserInterface → Application
 */
@Mapper(uses = DateTimeMapper.class)
public interface AppointmentCommandDtoMapper {

  AppointmentCommandDto toApplication(CreateAppointmentRequest request);
}
