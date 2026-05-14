package com.nutrisoft.core.component.appointment.application.listener;

import com.nutrisoft.core.component.appointment.application.notification.SendAppointmentConfirmationEmailUseCase;
import com.nutrisoft.core.shared.component.appointment.AppointmentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Appointment Created Event Listener.
 *
 * <p>Located in: Core\Components\Appointment\Application\Listener
 *
 * <p>This component listens to AppointmentCreatedEvent and orchestrates the notification flow by
 * delegating to SendAppointmentConfirmationEmailUseCase.
 *
 * <p>The listener is invoked after the transaction is committed, ensuring the appointment has been
 * persisted before sending the confirmation emails.
 *
 * <p>Event Flow: CreateAppointment -> AppointmentCreatedEvent -> AppointmentCreatedEventListener ->
 * SendAppointmentConfirmationEmailUseCase -> Send emails to patient and professional
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AppointmentCreatedEventListener {

  private final SendAppointmentConfirmationEmailUseCase sendAppointmentConfirmationEmailUseCase;

  /**
   * Handle appointment created event and orchestrate email notification.
   *
   * <p>This method is called when an AppointmentCreatedEvent is published, ensuring the appointment
   * has been persisted. Delegates to SendAppointmentConfirmationEmailUseCase to send confirmation
   * emails to both patient and professional.
   *
   * @param event The AppointmentCreatedEvent
   */
  @EventListener
  public void handleAppointmentCreatedEvent(final AppointmentEvent.AppointmentCreatedEvent event) {

    log.info("Handling appointment created event for appointment: {}", event.getAggregateId());
    try {
      // Delegate to use case to send confirmation emails
      sendAppointmentConfirmationEmailUseCase.execute(
          event.getAggregateId(),
          event.patientId(),
          event.professionalId(),
          event.serviceId(),
          event.startTime(),
          event.appointmentMode());

      log.info(
          "Appointment confirmation emails orchestrated successfully for appointment: {}",
          event.getAggregateId());
    } catch (Exception e) {
      log.error(
          "Error handling appointment created event for appointment: {}",
          event.getAggregateId(),
          e);
      // Don't throw exception - this is an async operation
    }
  }
}
