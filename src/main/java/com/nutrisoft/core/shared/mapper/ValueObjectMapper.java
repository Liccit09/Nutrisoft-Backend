package com.nutrisoft.core.shared.mapper;

import java.util.Objects;
import java.util.UUID;

import org.mapstruct.Mapper;

import com.nutrisoft.core.shared.component.appointment.AppointmentId;
import com.nutrisoft.core.shared.component.patient.PatientId;
import com.nutrisoft.core.shared.component.professional.ProfessionalId;
import com.nutrisoft.core.shared.component.service.ServiceId;
import com.nutrisoft.core.shared.ddd.Identifier;

@Mapper
public interface ValueObjectMapper {

  default <T> T toValue(final Identifier<T> vo) {
    return Objects.isNull(vo) ? null : vo.value();
  }

  ProfessionalId toProfessionalId(UUID value);

  AppointmentId toAppointmentId(UUID value);

  PatientId toPatientId(UUID value);

  ServiceId toServiceId(UUID value);
}
