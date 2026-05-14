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
 * Send Virtual Meeting Link Registered Email Use Case.
 *
 * <p>Located in: Core\Components\Appointment\Application\Notification
 *
 * <p>Responsibility: Send virtual meeting link notification emails when a link is registered.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SendVirtualMeetingLinkEmailUseCase {

  private final GetPatientByIdUseCase getPatientByIdUseCase;
  private final GetProfessionalByIdUseCase getProfessionalByIdUseCase;
  private final GetServiceByIdUseCase getServiceByIdUseCase;
  private final Notifications notificationAdapter;
  private final EmailTemplateService emailTemplateService;

  /**
   * Send virtual meeting link emails to both patient and professional.
   *
   * @param appointmentId The appointment ID
   * @param patientId The patient ID (as string)
   * @param professionalId The professional ID (as string)
   * @param serviceId The service ID (as string)
   * @param startTime The appointment start time
   * @param virtualMeetingLink The virtual meeting link URL
   */
  public void execute(
      String appointmentId,
      String patientId,
      String professionalId,
      String serviceId,
      LocalDateTime startTime,
      String virtualMeetingLink) {

    log.info("Sending virtual meeting link emails for appointment: {}", appointmentId);

    try {
      // Retrieve patient, professional, and service information
      final var patient = getPatientByIdUseCase.execute(UUID.fromString(patientId));
      final var professional = getProfessionalByIdUseCase.execute(UUID.fromString(professionalId));
      final var service = getServiceByIdUseCase.execute(UUID.fromString(serviceId));

      // Format date and time for display
      final var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
      final var formattedDateTime = startTime.format(formatter);

      // Send email to patient
      sendPatientVirtualMeetingEmail(
          patient.getContactInfo().email().value(),
          patient.getFirstName(),
          professional.getFirstName(),
          professional.getLastName(),
          service.getName(),
          formattedDateTime,
          virtualMeetingLink);

      // Send email to professional
      sendProfessionalVirtualMeetingEmail(
          professional.getContactInfo().email().value(),
          professional.getFirstName(),
          patient.getFirstName(),
          patient.getLastName(),
          service.getName(),
          formattedDateTime,
          virtualMeetingLink);

      log.info("Virtual meeting link emails sent successfully for appointment: {}", appointmentId);
    } catch (Exception e) {
      log.error(
          "Error sending virtual meeting link emails for appointment: {}",
          appointmentId,
          e);
    }
  }

  private void sendPatientVirtualMeetingEmail(
      String patientEmail,
      String patientName,
      String professionalFirstName,
      String professionalLastName,
      String serviceName,
      String appointmentDateTime,
      String virtualMeetingLink) {

    Map<String, Object> variables = new HashMap<>();
    variables.put("patientName", patientName);
    variables.put("professionalName", professionalFirstName + " " + professionalLastName);
    variables.put("serviceName", serviceName);
    variables.put("appointmentDateTime", appointmentDateTime);
    variables.put("virtualMeetingLink", virtualMeetingLink);

    String htmlContent = emailTemplateService.renderTemplate("appointment-virtual-meeting-patient", variables);
    notificationAdapter.send(patientEmail, "Enlace de Consulta Virtual - Nutrisoft", htmlContent);
  }

  private void sendProfessionalVirtualMeetingEmail(
      String professionalEmail,
      String professionalName,
      String patientFirstName,
      String patientLastName,
      String serviceName,
      String appointmentDateTime,
      String virtualMeetingLink) {

    Map<String, Object> variables = new HashMap<>();
    variables.put("professionalName", professionalName);
    variables.put("patientName", patientFirstName + " " + patientLastName);
    variables.put("serviceName", serviceName);
    variables.put("appointmentDateTime", appointmentDateTime);
    variables.put("virtualMeetingLink", virtualMeetingLink);

    String htmlContent = emailTemplateService.renderTemplate("appointment-virtual-meeting-professional", variables);
    notificationAdapter.send(professionalEmail, "Enlace de Consulta Virtual - Nutrisoft", htmlContent);
  }
}

