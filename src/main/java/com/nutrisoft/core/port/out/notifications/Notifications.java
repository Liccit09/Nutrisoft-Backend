package com.nutrisoft.core.port.out.notifications;

/**
 * Output Port: Notifications.
 *
 * <p>Located in: Core\Port\Out\Notifications
 *
 * <p>Responsibility: Define the contract for sending notifications through different channels
 * (Email, SMS, WhatsApp, etc.). This port allows the application to be agnostic of the specific
 * notification implementation.
 *
 * <p>Design: This port is prepared to support multiple adapters, allowing different notification
 * channels to be used interchangeably.
 */
public interface Notifications {

  /**
   * Send a simple notification with custom subject and content.
   *
   * @param to The recipient address (email, phone, user ID, etc.)
   * @param subject The subject or title of the notification
   * @param content The content of the notification
   */
  void send(String to, String subject, String content);
}
