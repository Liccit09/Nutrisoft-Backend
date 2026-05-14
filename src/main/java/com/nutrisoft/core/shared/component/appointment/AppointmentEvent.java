package com.nutrisoft.core.shared.component.appointment;

import com.nutrisoft.core.shared.ddd.DomainEvent;
import java.time.LocalDateTime;

/**
 * Appointment Domain Events.
 *
 * <p>Located in: Shared Kernel - Core Layer
 *
 * <p>Contains all domain events emitted by the Appointment aggregate. These events are used to
 * capture important occurrences within the appointment domain and can be consumed by event
 * listeners throughout the application.
 */
public class AppointmentEvent {

  /**
   * Event published when a new appointment is created.
   *
   * <p>Contains all necessary information for downstream consumers (e.g., notification service).
   */
  public record AppointmentCreatedEvent(
      String aggregateId,
      LocalDateTime occurredAt,
      String patientId,
      String professionalId,
      String serviceId,
      LocalDateTime startTime,
      String appointmentMode)
      implements DomainEvent {
    @Override
    public String getEventType() {
      return "APPOINTMENT_CREATED";
    }

    @Override
    public String getAggregateId() {
      return aggregateId;
    }

    @Override
    public LocalDateTime getOccurredAt() {
      return occurredAt;
    }
  }

  /**
   * Event published when an appointment is confirmed.
   */
  public record AppointmentConfirmedEvent(String aggregateId, LocalDateTime occurredAt)
      implements DomainEvent {
    @Override
    public String getEventType() {
      return "APPOINTMENT_CONFIRMED";
    }

    @Override
    public String getAggregateId() {
      return aggregateId;
    }

    @Override
    public LocalDateTime getOccurredAt() {
      return occurredAt;
    }
  }

  /**
   * Event published when an appointment is cancelled.
   */
  public record AppointmentCancelledEvent(
      String aggregateId,
      LocalDateTime occurredAt,
      String patientId,
      String professionalId,
      String serviceId,
      LocalDateTime startTime)
      implements DomainEvent {
    @Override
    public String getEventType() {
      return "APPOINTMENT_CANCELLED";
    }

    @Override
    public String getAggregateId() {
      return aggregateId;
    }

    @Override
    public LocalDateTime getOccurredAt() {
      return occurredAt;
    }
  }

  /**
   * Event published when an appointment is marked as completed.
   */
  public record AppointmentCompletedEvent(String aggregateId, LocalDateTime occurredAt)
      implements DomainEvent {
    @Override
    public String getEventType() {
      return "APPOINTMENT_COMPLETED";
    }

    @Override
    public String getAggregateId() {
      return aggregateId;
    }

    @Override
    public LocalDateTime getOccurredAt() {
      return occurredAt;
    }
  }

  /**
   * Event published when an appointment is marked as no-show.
   */
  public record AppointmentNoShowEvent(String aggregateId, LocalDateTime occurredAt)
      implements DomainEvent {
    @Override
    public String getEventType() {
      return "APPOINTMENT_NO_SHOW";
    }

    @Override
    public String getAggregateId() {
      return aggregateId;
    }

    @Override
    public LocalDateTime getOccurredAt() {
      return occurredAt;
    }
  }

  /**
   * Event published when an appointment is updated (e.g., rescheduled or virtual meeting link
   * registered).
   */
  public record AppointmentUpdatedEvent(
      String aggregateId,
      LocalDateTime occurredAt,
      String patientId,
      String professionalId,
      String serviceId,
      LocalDateTime newStartTime,
      String virtualMeetingLink)
      implements DomainEvent {
    @Override
    public String getEventType() {
      return "APPOINTMENT_UPDATED";
    }

    @Override
    public String getAggregateId() {
      return aggregateId;
    }

    @Override
    public LocalDateTime getOccurredAt() {
      return occurredAt;
    }
  }
}

