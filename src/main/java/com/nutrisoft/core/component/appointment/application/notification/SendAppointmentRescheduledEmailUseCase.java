package com.nutrisoft.core.component.appointment.application.notification;

import com.nutrisoft.core.component.patient.application.usecase.GetPatientByIdUseCase;
import com.nutrisoft.core.component.professional.application.usecase.GetProfessionalByIdUseCase;
import com.nutrisoft.core.component.service.application.usecase.GetServiceByIdUseCase;
import com.nutrisoft.core.port.out.notifications.Notifications;
import com.nutrisoft.infrastructure.notification.service.EmailTemplateService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Send Appointment Rescheduled Email Use Case.
 *
 * <p>Located in: Core\Components\Appointment\Application\Notification
 *
 * <p>Responsibility: Send rescheduling notification emails when an appointment is rescheduled.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SendAppointmentRescheduledEmailUseCase {

  private final GetPatientByIdUseCase getPatientByIdUseCase;
  private final GetProfessionalByIdUseCase getProfessionalByIdUseCase;
  private final GetServiceByIdUseCase getServiceByIdUseCase;
  private final Notifications notificationAdapter;
  private final EmailTemplateService emailTemplateService;

  /**
   * Send appointment rescheduled emails to both patient and professional.
   *
   * @param appointmentId The appointment ID
   * @param patientId The patient ID (as string)
   * @param professionalId The professional ID (as string)
   * @param serviceId The service ID (as string)
   * @param newStartTime The new appointment start time
   */
  public void execute(
      String appointmentId,
      String patientId,
      String professionalId,
      String serviceId,
      LocalDateTime newStartTime) {

    log.info("Sending appointment rescheduled emails for appointment: {}", appointmentId);

    try {
      // Retrieve patient, professional, and service information
      final var patient = getPatientByIdUseCase.execute(UUID.fromString(patientId));
      final var professional = getProfessionalByIdUseCase.execute(UUID.fromString(professionalId));
      final var service = getServiceByIdUseCase.execute(UUID.fromString(serviceId));

      // Format date and time for display
      final var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
      final var formattedDateTime = newStartTime.format(formatter);

      // Send email to patient
      sendPatientRescheduleEmail(
          patient.getContactInfo().email().value(),
          patient.getFirstName(),
          professional.getFirstName(),
          professional.getLastName(),
          service.getName(),
          formattedDateTime);

      // Send email to professional
      sendProfessionalRescheduleEmail(
          professional.getContactInfo().email().value(),
          professional.getFirstName(),
          patient.getFirstName(),
          patient.getLastName(),
          service.getName(),
          formattedDateTime);

      log.info("Appointment rescheduled emails sent successfully for appointment: {}", appointmentId);
    } catch (Exception e) {
      log.error(
          "Error sending appointment rescheduled emails for appointment: {}",
          appointmentId,
          e);
    }
  }

  private void sendPatientRescheduleEmail(
      String patientEmail,
      String patientName,
      String professionalFirstName,
      String professionalLastName,
      String serviceName,
      String newAppointmentDateTime) {

    Map<String, Object> variables = new HashMap<>();
    variables.put("patientName", patientName);
    variables.put("professionalName", professionalFirstName + " " + professionalLastName);
    variables.put("serviceName", serviceName);
    variables.put("newAppointmentDateTime", newAppointmentDateTime);

    String htmlContent = emailTemplateService.renderTemplate("appointment-rescheduled-patient", variables);
    notificationAdapter.send(patientEmail, "Cita Reprogramada - Nutrisoft", htmlContent);
  }

  private void sendProfessionalRescheduleEmail(
      String professionalEmail,
      String professionalName,
      String patientFirstName,
      String patientLastName,
      String serviceName,
      String newAppointmentDateTime) {

    Map<String, Object> variables = new HashMap<>();
    variables.put("professionalName", professionalName);
    variables.put("patientName", patientFirstName + " " + patientLastName);
    variables.put("serviceName", serviceName);
    variables.put("newAppointmentDateTime", newAppointmentDateTime);

    String htmlContent = emailTemplateService.renderTemplate("appointment-rescheduled-professional", variables);
    notificationAdapter.send(professionalEmail, "Cita Reprogramada - Nutrisoft", htmlContent);
  }
}

