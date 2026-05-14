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
 * Send Appointment Cancelled Email Use Case.
 *
 * <p>Located in: Core\Components\Appointment\Application\Notification
 *
 * <p>Responsibility: Send cancellation notification emails when an appointment is cancelled.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SendAppointmentCancelledEmailUseCase {

  private final GetPatientByIdUseCase getPatientByIdUseCase;
  private final GetProfessionalByIdUseCase getProfessionalByIdUseCase;
  private final GetServiceByIdUseCase getServiceByIdUseCase;
  private final Notifications notificationAdapter;
  private final EmailTemplateService emailTemplateService;

  /**
   * Send appointment cancelled emails to both patient and professional.
   *
   * @param appointmentId The appointment ID
   * @param patientId The patient ID (as string)
   * @param professionalId The professional ID (as string)
   * @param serviceId The service ID (as string)
   * @param startTime The cancelled appointment's start time
   */
  public void execute(
      String appointmentId,
      String patientId,
      String professionalId,
      String serviceId,
      LocalDateTime startTime) {

    log.info("Sending appointment cancelled emails for appointment: {}", appointmentId);

    try {
      // Retrieve patient, professional, and service information
      final var patient = getPatientByIdUseCase.execute(UUID.fromString(patientId));
      final var professional = getProfessionalByIdUseCase.execute(UUID.fromString(professionalId));
      final var service = getServiceByIdUseCase.execute(UUID.fromString(serviceId));

      // Format date and time for display
      final var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
      final var formattedDateTime = startTime.format(formatter);

      // Send email to patient
      sendPatientCancellationEmail(
          patient.getContactInfo().email().value(),
          patient.getFirstName(),
          professional.getFirstName(),
          professional.getLastName(),
          service.getName(),
          formattedDateTime);

      // Send email to professional
      sendProfessionalCancellationEmail(
          professional.getContactInfo().email().value(),
          professional.getFirstName(),
          patient.getFirstName(),
          patient.getLastName(),
          service.getName(),
          formattedDateTime);

      log.info("Appointment cancelled emails sent successfully for appointment: {}", appointmentId);
    } catch (Exception e) {
      log.error(
          "Error sending appointment cancelled emails for appointment: {}",
          appointmentId,
          e);
    }
  }

  private void sendPatientCancellationEmail(
      String patientEmail,
      String patientName,
      String professionalFirstName,
      String professionalLastName,
      String serviceName,
      String appointmentDateTime) {

    Map<String, Object> variables = new HashMap<>();
    variables.put("patientName", patientName);
    variables.put("professionalName", professionalFirstName + " " + professionalLastName);
    variables.put("serviceName", serviceName);
    variables.put("appointmentDateTime", appointmentDateTime);

    String htmlContent = emailTemplateService.renderTemplate("appointment-cancelled-patient", variables);
    notificationAdapter.send(patientEmail, "Cita Cancelada - Nutrisoft", htmlContent);
  }

  private void sendProfessionalCancellationEmail(
      String professionalEmail,
      String professionalName,
      String patientFirstName,
      String patientLastName,
      String serviceName,
      String appointmentDateTime) {

    Map<String, Object> variables = new HashMap<>();
    variables.put("professionalName", professionalName);
    variables.put("patientName", patientFirstName + " " + patientLastName);
    variables.put("serviceName", serviceName);
    variables.put("appointmentDateTime", appointmentDateTime);

    String htmlContent = emailTemplateService.renderTemplate("appointment-cancelled-professional", variables);
    notificationAdapter.send(professionalEmail, "Cita Cancelada - Nutrisoft", htmlContent);
  }
}

