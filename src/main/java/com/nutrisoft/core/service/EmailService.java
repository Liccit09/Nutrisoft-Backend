package com.nutrisoft.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Email Service.
 * Handles sending emails to users.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@nutrisoft.com}")
    private String fromEmail;

    @Value("${spring.mail.from-name:Nutrisoft}")
    private String fromName;

    /**
     * Send password reset email.
     */
    public void sendPasswordResetEmail(String to, String firstName, String resetLink) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Recuperar contraseña - Nutrisoft");

            String content = buildPasswordResetEmailContent(firstName, resetLink);
            message.setText(content);

            mailSender.send(message);
            log.info("Password reset email sent to: {}", to);
        } catch (Exception e) {
            log.error("Error sending password reset email to: {}", to, e);
            // Don't throw exception - silently fail for now
        }
    }

    /**
     * Send confirmation email after appointment booking.
     */
    public void sendAppointmentConfirmationEmail(String to, String firstName, String appointmentDetails) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Confirmación de cita - Nutrisoft");

            String content = buildAppointmentConfirmationContent(firstName, appointmentDetails);
            message.setText(content);

            mailSender.send(message);
            log.info("Appointment confirmation email sent to: {}", to);
        } catch (Exception e) {
            log.error("Error sending appointment confirmation email to: {}", to, e);
        }
    }

    /**
     * Build password reset email content.
     */
    private String buildPasswordResetEmailContent(String firstName, String resetLink) {
        return "Hola " + firstName + ",\n\n" +
                "Recibimos una solicitud para recuperar tu contraseña. " +
                "Haz clic en el siguiente enlace para establecer una nueva contraseña:\n\n" +
                resetLink + "\n\n" +
                "Este enlace expirará en 24 horas.\n\n" +
                "Si no solicitaste esta recuperación, ignora este email.\n\n" +
                "Saludos,\n" +
                "Equipo Nutrisoft";
    }

    /**
     * Build appointment confirmation email content.
     */
    private String buildAppointmentConfirmationContent(String firstName, String appointmentDetails) {
        return "Hola " + firstName + ",\n\n" +
                "Tu cita ha sido confirmada correctamente.\n\n" +
                appointmentDetails + "\n\n" +
                "Si necesitas cambiar o cancelar tu cita, inicia sesión en tu cuenta.\n\n" +
                "Saludos,\n" +
                "Equipo Nutrisoft";
    }
}
