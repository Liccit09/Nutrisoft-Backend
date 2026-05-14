package com.nutrisoft.core.component.appointment.application.listener;

import com.nutrisoft.core.component.appointment.application.notification.SendAppointmentRescheduledEmailUseCase;
import com.nutrisoft.core.component.appointment.application.notification.SendVirtualMeetingLinkEmailUseCase;
import com.nutrisoft.core.shared.component.appointment.AppointmentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.StringUtils;

/**
 * Appointment Updated Event Listener.
 *
 * <p>Located in: Core\Components\Appointment\Application\Listener
 *
 * <p>This component listens to AppointmentUpdatedEvent and orchestrates the appropriate notification
 * flow based on the type of update (reschedule or virtual meeting link).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AppointmentUpdatedEventListener {
  private final SendAppointmentRescheduledEmailUseCase sendAppointmentRescheduledEmailUseCase;
  private final SendVirtualMeetingLinkEmailUseCase sendVirtualMeetingLinkEmailUseCase;

  /**
   * Handle appointment updated event and orchestrate appropriate email notification.
   *
   * <p>Determines whether the update is a reschedule or virtual meeting link registration and
   * delegates to the appropriate use case.
   *
   * @param event The AppointmentUpdatedEvent
   */
  @EventListener
  public void handleAppointmentUpdatedEvent(
      final AppointmentEvent.AppointmentUpdatedEvent event) {

    log.info("Handling appointment updated event for appointment: {}", event.getAggregateId());
    try {
      // Determine type of update based on virtual meeting link presence
      if (StringUtils.isNotBlank(event.virtualMeetingLink())) {
        // Virtual meeting link was registered
        sendVirtualMeetingLinkEmailUseCase.execute(
            event.getAggregateId(),
            event.patientId(),
            event.professionalId(),
            event.serviceId(),
            event.newStartTime(),
            event.virtualMeetingLink());

        log.info(
            "Virtual meeting link notification sent successfully for appointment: {}",
            event.getAggregateId());
      } else {
        // Appointment was rescheduled
        sendAppointmentRescheduledEmailUseCase.execute(
            event.getAggregateId(),
            event.patientId(),
            event.professionalId(),
            event.serviceId(),
            event.newStartTime());

        log.info(
            "Appointment rescheduled notification sent successfully for appointment: {}",
            event.getAggregateId());
      }
    } catch (Exception e) {
      log.error(
          "Error handling appointment updated event for appointment: {}",
          event.getAggregateId(),
          e);
    }
  }
}

