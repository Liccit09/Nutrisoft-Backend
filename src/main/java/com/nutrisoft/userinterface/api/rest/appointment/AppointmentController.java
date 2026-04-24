package com.nutrisoft.userinterface.api.rest.appointment;

import com.nutrisoft.core.component.appointment.application.command.AppointmentCommandDto;
import com.nutrisoft.core.component.appointment.domain.Appointment;
import com.nutrisoft.core.component.appointment.application.usecase.CancelAppointmentUseCase;
import com.nutrisoft.core.component.appointment.application.usecase.ConfirmAppointmentUseCase;
import com.nutrisoft.core.component.appointment.application.usecase.CreateAppointmentUseCase;
import com.nutrisoft.core.component.appointment.application.usecase.GetAppointmentByIdUseCase;
import com.nutrisoft.core.component.appointment.application.usecase.ListAllAppointmentsUseCase;
import com.nutrisoft.core.component.appointment.application.usecase.ListAppointmentsByPatientUseCase;
import com.nutrisoft.core.component.appointment.application.usecase.ListAppointmentsByProfessionalUseCase;
import com.nutrisoft.core.component.appointment.application.usecase.MarkAppointmentAsCompletedUseCase;
import com.nutrisoft.core.component.appointment.application.usecase.MarkAppointmentAsNoShowUseCase;
import com.nutrisoft.core.component.appointment.application.usecase.RegisterVirtualMeetingLinkUseCase;
import com.nutrisoft.core.component.appointment.application.usecase.RescheduleAppointmentUseCase;
import com.nutrisoft.userinterface.api.rest.appointment.generated.AppointmentsApi;
import com.nutrisoft.userinterface.api.rest.appointment.generated.model.AppointmentResponse;
import com.nutrisoft.userinterface.api.rest.appointment.generated.model.CreateAppointmentRequest;
import com.nutrisoft.userinterface.api.rest.appointment.generated.model.RegisterVirtualMeetingLinkRequest;
import com.nutrisoft.userinterface.api.rest.appointment.generated.model.RescheduleAppointmentRequest;
import com.nutrisoft.userinterface.api.rest.appointment.mapper.AppointmentCommandDtoMapper;
import com.nutrisoft.userinterface.api.rest.appointment.mapper.AppointmentResponseMapper;
import com.nutrisoft.core.shared.mapper.DateTimeMapper;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

/**
 * Implementation of AppointmentsApi interface.
 *
 * <p>This is a PRIMARY/DRIVING ADAPTER that implements the auto-generated AppointmentsApi
 * interface. It directly delegates to individual use case classes which orchestrate the domain
 * logic.
 *
 * <p>The AppointmentsApi interface is auto-generated from the OpenAPI specification
 * (appointments-api-v1.yaml) using the openapi-generator-maven-plugin.
 *
 * <p>Strategy: API-First - The contract (YAML) drives the implementation.
 *
 * <p>All conversions between layers are delegated to specialized mapper classes.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class AppointmentController implements AppointmentsApi {

  private final CreateAppointmentUseCase createAppointmentUseCase;
  private final GetAppointmentByIdUseCase getAppointmentByIdUseCase;
  private final ListAllAppointmentsUseCase listAllAppointmentsUseCase;
  private final ListAppointmentsByPatientUseCase listAppointmentsByPatientUseCase;
  private final ListAppointmentsByProfessionalUseCase listAppointmentsByProfessionalUseCase;
  private final ConfirmAppointmentUseCase confirmAppointmentUseCase;
  private final CancelAppointmentUseCase cancelAppointmentUseCase;
  private final MarkAppointmentAsCompletedUseCase markAppointmentAsCompletedUseCase;
  private final MarkAppointmentAsNoShowUseCase markAppointmentAsNoShowUseCase;
  private final RescheduleAppointmentUseCase rescheduleAppointmentUseCase;
  private final RegisterVirtualMeetingLinkUseCase registerVirtualMeetingLinkUseCase;

  private final AppointmentCommandDtoMapper appointmentCommandDtoMapper;
  private final AppointmentResponseMapper appointmentResponseMapper;
  private final DateTimeMapper dateTimeMapper;

  @Override
  public ResponseEntity<AppointmentResponse> createAppointment(
      final CreateAppointmentRequest createAppointmentRequest) {
    log.info("REST: Creating appointment for patient: {}", createAppointmentRequest.getPatientId());

    // Map API request to command DTO using mapper
    AppointmentCommandDto commandDto =
        appointmentCommandDtoMapper.toApplication(createAppointmentRequest);

    // Execute use case
    Appointment appointment = createAppointmentUseCase.execute(commandDto);

    // Map domain object to API response using mapper
    AppointmentResponse apiResponse = appointmentResponseMapper.toResponse(appointment);

    return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
  }

  @Override
  public ResponseEntity<AppointmentResponse> getAppointment(UUID appointmentId) {
    log.info("REST: Getting appointment: {}", appointmentId);

    Appointment appointment = getAppointmentByIdUseCase.execute(appointmentId);

    AppointmentResponse apiResponse = appointmentResponseMapper.toResponse(appointment);

    return ResponseEntity.ok(apiResponse);
  }

  @Override
  public ResponseEntity<List<AppointmentResponse>> listAppointments() {
    log.info("REST: Listing all appointments");

    List<Appointment> appointments = listAllAppointmentsUseCase.execute();

    List<AppointmentResponse> apiResponses =
        appointments.stream().map(appointmentResponseMapper::toResponse).toList();

    return ResponseEntity.ok(apiResponses);
  }

  @Override
  public ResponseEntity<AppointmentResponse> confirmAppointment(UUID appointmentId) {
    log.info("REST: Confirming appointment: {}", appointmentId);

    Appointment appointment = confirmAppointmentUseCase.execute(appointmentId);

    AppointmentResponse apiResponse = appointmentResponseMapper.toResponse(appointment);

    return ResponseEntity.ok(apiResponse);
  }

  @Override
  public ResponseEntity<AppointmentResponse> cancelAppointment(UUID appointmentId) {
    log.info("REST: Cancelling appointment: {}", appointmentId);

    Appointment appointment = cancelAppointmentUseCase.execute(appointmentId);

    AppointmentResponse apiResponse = appointmentResponseMapper.toResponse(appointment);

    return ResponseEntity.ok(apiResponse);
  }

  @Override
  public ResponseEntity<AppointmentResponse> completeAppointment(UUID appointmentId) {
    log.info("REST: Marking appointment as completed: {}", appointmentId);

    Appointment appointment = markAppointmentAsCompletedUseCase.execute(appointmentId);

    AppointmentResponse apiResponse = appointmentResponseMapper.toResponse(appointment);

    return ResponseEntity.ok(apiResponse);
  }

  @Override
  public ResponseEntity<AppointmentResponse> markNoShowAppointment(UUID appointmentId) {
    log.info("REST: Marking appointment as no-show: {}", appointmentId);

    Appointment appointment = markAppointmentAsNoShowUseCase.execute(appointmentId);

    AppointmentResponse apiResponse = appointmentResponseMapper.toResponse(appointment);

    return ResponseEntity.ok(apiResponse);
  }

  @Override
  public ResponseEntity<AppointmentResponse> rescheduleAppointment(
      final UUID appointmentId, final RescheduleAppointmentRequest rescheduleAppointmentRequest) {
    log.info("REST: Rescheduling appointment: {} to new time: {}", 
        appointmentId, rescheduleAppointmentRequest.getStartTime());

    // Convert OffsetDateTime from API to LocalDateTime for domain layer
    var localDateTime = dateTimeMapper.map(rescheduleAppointmentRequest.getStartTime());

    // Execute use case
    Appointment appointment = rescheduleAppointmentUseCase.execute(appointmentId, localDateTime);

    // Map domain object to API response using mapper
    AppointmentResponse apiResponse = appointmentResponseMapper.toResponse(appointment);

    return ResponseEntity.ok(apiResponse);
  }

  @Override
  public ResponseEntity<AppointmentResponse> registerVirtualMeetingLink(
      final UUID appointmentId,
      final RegisterVirtualMeetingLinkRequest registerVirtualMeetingLinkRequest) {
    log.info("REST: Registering virtual meeting link for appointment: {}", appointmentId);

    // Execute use case
    Appointment appointment = registerVirtualMeetingLinkUseCase.execute(
        appointmentId, registerVirtualMeetingLinkRequest.getVirtualMeetingLink());

    // Map domain object to API response using mapper
    AppointmentResponse apiResponse = appointmentResponseMapper.toResponse(appointment);

    return ResponseEntity.ok(apiResponse);
  }

  @Override
  public ResponseEntity<List<AppointmentResponse>> listAppointmentsByPatient(UUID patientId) {
    log.info("REST: Listing appointments for patient: {}", patientId);

    List<Appointment> appointments = listAppointmentsByPatientUseCase.execute(patientId);

    List<AppointmentResponse> apiResponses =
        appointments.stream().map(appointmentResponseMapper::toResponse).toList();

    return ResponseEntity.ok(apiResponses);
  }

  @Override
  public ResponseEntity<List<AppointmentResponse>> listAppointmentsByProfessional(
      final UUID professionalId) {
    log.info("REST: Listing appointments for professional: {}", professionalId);

    List<Appointment> appointments =
        listAppointmentsByProfessionalUseCase.execute(professionalId);

    List<AppointmentResponse> apiResponses =
        appointments.stream().map(appointmentResponseMapper::toResponse).toList();

    return ResponseEntity.ok(apiResponses);
  }
}
