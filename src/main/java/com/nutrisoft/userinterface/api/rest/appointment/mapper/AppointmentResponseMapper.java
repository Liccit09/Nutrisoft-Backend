package com.nutrisoft.userinterface.api.rest.appointment.mapper;

import com.nutrisoft.core.component.appointment.domain.Appointment;
import com.nutrisoft.core.shared.mapper.DateTimeMapper;
import com.nutrisoft.core.shared.mapper.ValueObjectMapper;
import com.nutrisoft.userinterface.api.rest.appointment.generated.model.AppointmentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting Domain Appointments to API Response DTOs.
 *
 * <p>Located in: UserInterface\API\Rest\Appointment\Mapper
 *
 * <p>Responsibility: Convert domain appointment objects to API-generated response objects for JSON
 * serialization. Uses MapStruct for efficient mapping.
 *
 * <p>Conversion Layer: Domain → UserInterface
 */
@Mapper(uses = {ValueObjectMapper.class, DateTimeMapper.class})
public interface AppointmentResponseMapper {

  @Mapping(
      target = "virtualMeetingLink",
      expression = "java(appointment.getVirtualMeetingLink().toString())")
  AppointmentResponse toResponse(Appointment appointment);
}
