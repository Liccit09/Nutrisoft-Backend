package com.nutrisoft.core.component.appointment.application.listener;

import com.nutrisoft.core.component.appointment.application.notification.SendAppointmentCancelledEmailUseCase;
import com.nutrisoft.core.shared.component.appointment.AppointmentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Appointment Cancelled Event Listener.
 *
 * <p>Located in: Core\Components\Appointment\Application\Listener
 *
 * <p>This component listens to AppointmentCancelledEvent and orchestrates the notification flow.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AppointmentCancelledEventListener {
  private final SendAppointmentCancelledEmailUseCase sendAppointmentCancelledEmailUseCase;

  /**
   * Handle appointment cancelled event and orchestrate email notification.
   *
   * @param event The AppointmentCancelledEvent
   */
  @EventListener
  public void handleAppointmentCancelledEvent(
      final AppointmentEvent.AppointmentCancelledEvent event) {

    log.info("Handling appointment cancelled event for appointment: {}", event.getAggregateId());
    try {
      sendAppointmentCancelledEmailUseCase.execute(
          event.getAggregateId(),
          event.patientId(),
          event.professionalId(),
          event.serviceId(),
          event.startTime());

      log.info(
          "Appointment cancelled emails orchestrated successfully for appointment: {}",
          event.getAggregateId());
    } catch (Exception e) {
      log.error(
          "Error handling appointment cancelled event for appointment: {}",
          event.getAggregateId(),
          e);
    }
  }
}


