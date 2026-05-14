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
 * Send Appointment Confirmation Email Use Case.
 *
 * <p>Located in: Core\Components\Appointment\Application\Notification
 *
 * <p>Responsibility: Orchestrate the sending of appointment confirmation emails to both patient and
 * professional when an appointment is created.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SendAppointmentConfirmationEmailUseCase {

  private final GetPatientByIdUseCase getPatientByIdUseCase;
  private final GetProfessionalByIdUseCase getProfessionalByIdUseCase;
  private final GetServiceByIdUseCase getServiceByIdUseCase;
  private final Notifications notificationAdapter;
  private final EmailTemplateService emailTemplateService;

  /**
   * Send appointment confirmation emails to both patient and professional.
   *
   * @param appointmentId The appointment ID
   * @param patientId The patient ID
   * @param professionalId The professional ID
   * @param serviceId The service ID
   * @param startTime The appointment start time
   * @param appointmentMode The appointment mode (PRESENCIAL or VIRTUAL)
   */
  public void execute(
      String appointmentId,
      String patientId,
      String professionalId,
      String serviceId,
      LocalDateTime startTime,
      String appointmentMode) {

    log.info("Sending appointment confirmation emails for appointment: {}", appointmentId);

    try {
      // Retrieve patient, professional, and service information
      final var patient = getPatientByIdUseCase.execute(UUID.fromString(patientId));
      final var professional = getProfessionalByIdUseCase.execute(UUID.fromString(professionalId));
      final var service = getServiceByIdUseCase.execute(UUID.fromString(serviceId));

      // Format date and time for display
      final var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
      final var formattedDateTime = startTime.format(formatter);

      // Send email to patient
      sendPatientConfirmationEmail(
          patient.getContactInfo().email().value(),
          patient.getFirstName(),
          professional.getFirstName(),
          professional.getLastName(),
          service.getName(),
          formattedDateTime,
          appointmentMode);

      // Send email to professional
      sendProfessionalConfirmationEmail(
          professional.getContactInfo().email().value(),
          professional.getFirstName(),
          patient.getFirstName(),
          patient.getLastName(),
          service.getName(),
          formattedDateTime,
          appointmentMode);

      log.info("Appointment confirmation emails sent successfully for appointment: {}", appointmentId);
    } catch (Exception e) {
      log.error(
          "Error sending appointment confirmation emails for appointment: {}",
          appointmentId,
          e);
      // Don't throw exception - this is an async operation
    }
  }

  private void sendPatientConfirmationEmail(
      String patientEmail,
      String patientName,
      String professionalFirstName,
      String professionalLastName,
      String serviceName,
      String appointmentDateTime,
      String appointmentMode) {

    Map<String, Object> variables = new HashMap<>();
    variables.put("patientName", patientName);
    variables.put("professionalName", professionalFirstName + " " + professionalLastName);
    variables.put("serviceName", serviceName);
    variables.put("appointmentDateTime", appointmentDateTime);
    variables.put("appointmentMode", appointmentMode);

    String htmlContent = emailTemplateService.renderTemplate("appointment-confirmation-patient", variables);
    notificationAdapter.send(patientEmail, "Confirmación de cita - Nutrisoft", htmlContent);
  }

  private void sendProfessionalConfirmationEmail(
      String professionalEmail,
      String professionalName,
      String patientFirstName,
      String patientLastName,
      String serviceName,
      String appointmentDateTime,
      String appointmentMode) {

    Map<String, Object> variables = new HashMap<>();
    variables.put("professionalName", professionalName);
    variables.put("patientName", patientFirstName + " " + patientLastName);
    variables.put("serviceName", serviceName);
    variables.put("appointmentDateTime", appointmentDateTime);
    variables.put("appointmentMode", appointmentMode);

    String htmlContent = emailTemplateService.renderTemplate("appointment-confirmation-professional", variables);
    notificationAdapter.send(professionalEmail, "Nueva cita programada - Nutrisoft", htmlContent);
  }
}




