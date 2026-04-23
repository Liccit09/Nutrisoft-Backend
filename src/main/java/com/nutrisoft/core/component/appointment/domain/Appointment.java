package com.nutrisoft.core.component.appointment.domain;

import com.nutrisoft.core.shared.component.appointment.AppointmentId;
import com.nutrisoft.core.shared.component.patient.PatientId;
import com.nutrisoft.core.shared.component.professional.ProfessionalId;
import com.nutrisoft.core.shared.component.service.ServiceId;
import com.nutrisoft.core.shared.ddd.AggregateRoot;
import com.nutrisoft.core.shared.ddd.DomainEvent;
import java.net.URI;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NonNull;

/**
 * Appointment Aggregate Root. This is the main aggregate that manages appointments in the system.
 *
 * <p>Located in: Core\Components\Appointment\Domain Part of the Appointment Component bounded
 * context
 *
 * <p>Invariants: - An appointment must have a patient, professional, and service - An appointment
 * must have a valid time slot (start before end) - An appointment cannot be modified once it is
 * completed or no-show - An appointment status transitions must follow business rules
 */
@Getter
public class Appointment extends AggregateRoot<AppointmentId> {

  private final PatientId patientId;
  private final ProfessionalId professionalId;
  private final ServiceId serviceId;
  private LocalDateTime startTime;
  private final AppointmentMode mode;
  private AppointmentStatus status;
  private URI virtualMeetingLink;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public Appointment(
      @NonNull final AppointmentId id,
      @NonNull final PatientId patientId,
      @NonNull final ProfessionalId professionalId,
      @NonNull final ServiceId serviceId,
      @NonNull final LocalDateTime startTime,
      @NonNull final AppointmentMode mode,
      @NonNull final AppointmentStatus status,
      final URI virtualMeetingLink,
      final LocalDateTime createdAt,
      final LocalDateTime updatedAt) {
    super(id);
    this.patientId = patientId;
    this.professionalId = professionalId;
    this.serviceId = serviceId;
    this.startTime = startTime;
    this.mode = mode;
    this.status = status;
    this.virtualMeetingLink = virtualMeetingLink;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public static Appointment create(
      @NonNull final PatientId patientId,
      @NonNull final ProfessionalId professionalId,
      @NonNull final ServiceId serviceId,
      @NonNull final LocalDateTime startTime,
      final AppointmentMode mode,
      final String virtualMeetingLink) {

    final var now = LocalDateTime.now();
    final var appointmentId = AppointmentId.create();

    Appointment appointment =
        new Appointment(
            appointmentId,
            patientId,
            professionalId,
            serviceId,
            startTime,
            mode,
            AppointmentStatus.SCHEDULED,
            URI.create(virtualMeetingLink),
            now,
            now);

    // Register domain event
    appointment.registerEvent(new AppointmentCreatedEvent(appointmentId.toString(), now));

    return appointment;
  }

  /** Confirm the appointment. An appointment can only be confirmed if it's in SCHEDULED status. */
  public void confirm() {
    if (!status.canBeConfirmed()) {
      throw new IllegalStateException("Cannot confirm appointment with status: " + status);
    }
    this.status = AppointmentStatus.CONFIRMED;
    this.updatedAt = LocalDateTime.now();
    this.registerEvent(new AppointmentConfirmedEvent(this.id.toString(), LocalDateTime.now()));
  }

  /**
   * Cancel the appointment. An appointment can only be cancelled if it's SCHEDULED or CONFIRMED.
   */
  public void cancel() {
    if (!status.canBeCancelled()) {
      throw new IllegalStateException("Cannot cancel appointment with status: " + status);
    }
    this.status = AppointmentStatus.CANCELLED;
    this.updatedAt = LocalDateTime.now();
    this.registerEvent(new AppointmentCancelledEvent(this.id.toString(), LocalDateTime.now()));
  }

  /** Mark the appointment as completed. */
  public void markAsCompleted() {
    this.status = AppointmentStatus.COMPLETED;
    this.updatedAt = LocalDateTime.now();
    this.registerEvent(new AppointmentCompletedEvent(this.id.toString(), LocalDateTime.now()));
  }

  /** Mark the appointment as no-show. */
  public void markAsNoShow() {
    this.status = AppointmentStatus.NO_SHOW;
    this.updatedAt = LocalDateTime.now();
    this.registerEvent(new AppointmentNoShowEvent(this.id.toString(), LocalDateTime.now()));
  }

  /**
   * Reschedule the appointment to a new time. Cannot reschedule if appointment is completed or
   * no-show.
   */
  public void reschedule(final LocalDateTime newStartTime) {
    if (!status.canBeUpdated()) {
      throw new IllegalStateException("Cannot reschedule appointment with status: " + status);
    }

    this.startTime = newStartTime;
    this.updatedAt = LocalDateTime.now();
    this.registerEvent(new AppointmentUpdatedEvent(this.id.toString(), LocalDateTime.now()));
  }

  /**
   * Register the virtual meeting link for the appointment. Cannot register if appointment mode is
   * not virtual. Cannot register if appointment is completed or no-show.
   */
  public void registerVirtualMeetingLink(final String virtualMeetingLink) {
    if (mode != AppointmentMode.VIRTUAL) {
      throw new IllegalStateException(
          "Cannot register virtual meeting link for non-virtual appointment");
    }

    if (!status.canBeUpdated()) {
      throw new IllegalStateException(
          "Cannot register virtual meeting link for appointment with status: " + status);
    }

    this.virtualMeetingLink = URI.create(virtualMeetingLink);
    this.updatedAt = LocalDateTime.now();
    this.registerEvent(new AppointmentUpdatedEvent(this.id.toString(), LocalDateTime.now()));
  }

  // Domain Events

  public record AppointmentCreatedEvent(String aggregateId, LocalDateTime occurredAt)
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

  public record AppointmentCancelledEvent(String aggregateId, LocalDateTime occurredAt)
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

  public record AppointmentUpdatedEvent(String aggregateId, LocalDateTime occurredAt)
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
