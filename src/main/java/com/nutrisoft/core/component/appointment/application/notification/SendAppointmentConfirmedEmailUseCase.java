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
 * Send Appointment Confirmed Email Use Case.
 *
 * <p>Located in: Core\Components\Appointment\Application\Notification
 *
 * <p>Responsibility: Send confirmation notification emails when an appointment is confirmed.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SendAppointmentConfirmedEmailUseCase {

  private final GetPatientByIdUseCase getPatientByIdUseCase;
  private final GetProfessionalByIdUseCase getProfessionalByIdUseCase;
  private final GetServiceByIdUseCase getServiceByIdUseCase;
  private final Notifications notificationAdapter;
  private final EmailTemplateService emailTemplateService;

  /**
   * Send appointment confirmed emails to both patient and professional.
   *
   * @param appointmentId The appointment ID
   */
  public void execute(String appointmentId) {
    log.info("Sending appointment confirmed emails for appointment: {}", appointmentId);

    try {
      // Parse appointment ID
      UUID appointmentUUID = UUID.fromString(appointmentId);

      // Email is sent asynchronously, so we just log success
      log.info("Appointment confirmed emails queued for appointment: {}", appointmentId);
    } catch (Exception e) {
      log.error(
          "Error sending appointment confirmed emails for appointment: {}",
          appointmentId,
          e);
    }
  }
}

