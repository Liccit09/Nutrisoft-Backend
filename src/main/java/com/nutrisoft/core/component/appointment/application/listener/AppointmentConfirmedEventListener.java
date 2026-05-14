package com.nutrisoft.core.component.appointment.application.listener;

import com.nutrisoft.core.component.appointment.application.notification.SendAppointmentConfirmedEmailUseCase;
import com.nutrisoft.core.shared.component.appointment.AppointmentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Appointment Confirmed Event Listener.
 *
 * <p>Located in: Core\Components\Appointment\Application\Listener
 *
 * <p>This component listens to AppointmentConfirmedEvent and orchestrates the notification flow.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AppointmentConfirmedEventListener {
  private final SendAppointmentConfirmedEmailUseCase sendAppointmentConfirmedEmailUseCase;

  /**
   * Handle appointment confirmed event and orchestrate email notification.
   *
   * @param event The AppointmentConfirmedEvent
   */
  @EventListener
  public void handleAppointmentConfirmedEvent(
      final AppointmentEvent.AppointmentConfirmedEvent event) {

    log.info("Handling appointment confirmed event for appointment: {}", event.getAggregateId());
    try {
      sendAppointmentConfirmedEmailUseCase.execute(event.getAggregateId());
      log.info(
          "Appointment confirmed emails orchestrated successfully for appointment: {}",
          event.getAggregateId());
    } catch (Exception e) {
      log.error(
          "Error handling appointment confirmed event for appointment: {}",
          event.getAggregateId(),
          e);
    }
  }
}

