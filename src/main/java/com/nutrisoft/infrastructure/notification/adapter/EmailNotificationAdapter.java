package com.nutrisoft.infrastructure.notification.adapter;

import com.nutrisoft.core.port.out.notifications.Notifications;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * Email Notification Adapter.
 *
 * <p>Located in: Infrastructure\Notification\Adapter
 *
 * <p>Responsibility: Implement the Notifications port for sending notifications via email. This
 * adapter handles the integration with JavaMailSender and the SMTP server.
 *
 * <p>This is the first adapter implementation. Future adapters (SMS, WhatsApp, etc.) should also
 * implement the Notifications interface.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationAdapter implements Notifications {

  private final JavaMailSender mailSender;

  @Value("${spring.mail.from:noreply@nutrisoft.com}")
  private String fromEmail;

  @Value("${spring.mail.from-name:Nutrisoft}")
  private String fromName;

  /**
   * Send an HTML email with custom subject and content.
   *
   * @param to The recipient email address
   * @param subject The email subject
   * @param htmlContent The HTML email content
   */
  @Override
  public void send(String to, String subject, String htmlContent) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setFrom(fromEmail, fromName);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(htmlContent, true);

      mailSender.send(message);
      log.info("HTML email sent to: {} with subject: {}", to, subject);
    } catch (Exception e) {
      log.error("Error sending HTML email to: {}", to, e);
      throw new RuntimeException("Failed to send email notification", e);
    }
  }
}
