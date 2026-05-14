package com.nutrisoft.core.component.professional.application.notification;

import com.nutrisoft.core.port.out.notifications.Notifications;
import com.nutrisoft.infrastructure.notification.service.EmailTemplateService;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Send Professional Welcome Email Use Case.
 *
 * <p>Located in: Core\Components\Professional\Application\Notification
 *
 * <p>Responsibility: Orchestrate the sending of welcome email to a newly registered professional.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SendProfessionalWelcomeEmailUseCase {

  private final Notifications notificationAdapter;
  private final EmailTemplateService emailTemplateService;

  /**
   * Send welcome email to the professional.
   *
   * @param email The professional's email address
   * @param firstName The professional's first name
   * @param lastName The professional's last name
   * @param specialization The professional's specialization
   */
  public void execute(
      String email, String firstName, String lastName, String specialization) {

    log.info("Sending welcome email to professional: {}", email);

    try {
      Map<String, Object> variables = new HashMap<>();
      variables.put("firstName", firstName);
      variables.put("lastName", lastName);
      variables.put("specialization", specialization);
      variables.put("email", email);

      String htmlContent = emailTemplateService.renderTemplate("professional-welcome-email", variables);
      notificationAdapter.send(email, "Bienvenido a Nutrisoft", htmlContent);

      log.info("Welcome email sent successfully to professional: {}", email);
    } catch (Exception e) {
      log.error("Error sending welcome email to professional: {}", email, e);
      // Don't throw exception - this is an async operation
    }
  }
}

