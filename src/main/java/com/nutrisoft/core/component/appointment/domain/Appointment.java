package com.nutrisoft.core.component.appointment.domain;

import com.nutrisoft.core.shared.component.appointment.AppointmentEvent;
import com.nutrisoft.core.shared.component.appointment.AppointmentId;
import com.nutrisoft.core.shared.component.patient.PatientId;
import com.nutrisoft.core.shared.component.professional.ProfessionalId;
import com.nutrisoft.core.shared.component.service.ServiceId;
import com.nutrisoft.core.shared.ddd.AggregateRoot;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

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
  private String virtualMeetingLink;
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
      final String virtualMeetingLink,
      @NonNull final LocalDateTime createdAt,
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

    validate();
  }

  private void validate() {
    assertVirtualModeIsValid();
  }

  private void assertVirtualModeIsValid() {
    if (!StringUtils.isBlank(virtualMeetingLink) && !mode.isVirtual()) {
      throw new IllegalStateException(
          "Cannot register virtual meeting link for non-virtual appointment");
    }
  }

  private static void assertStartTimeIsInFuture(final LocalDateTime startTime) {
    if (startTime.isBefore(LocalDateTime.now())) {
      throw new IllegalStateException("Appointment start time must be in the future");
    }
  }

  public static Appointment create(
      @NonNull final PatientId patientId,
      @NonNull final ProfessionalId professionalId,
      @NonNull final ServiceId serviceId,
      @NonNull final LocalDateTime startTime,
      final AppointmentMode mode,
      final String virtualMeetingLink) {
    assertStartTimeIsInFuture(startTime);

    final var now = LocalDateTime.now();
    final var appointmentId = AppointmentId.create();

    final var appointment =
        new Appointment(
            appointmentId,
            patientId,
            professionalId,
            serviceId,
            startTime,
            mode,
            AppointmentStatus.SCHEDULED,
            virtualMeetingLink,
            now,
            now);

    // Register domain event
    appointment.registerEvent(
        new AppointmentEvent.AppointmentCreatedEvent(
            appointmentId.toString(),
            now,
            patientId.value().toString(),
            professionalId.value().toString(),
            serviceId.value().toString(),
            startTime,
            mode.name()));

    return appointment;
  }

  /** Confirm the appointment. An appointment can only be confirmed if it's in SCHEDULED status. */
  public void confirm() {
    if (!status.canBeConfirmed()) {
      throw new IllegalStateException("Cannot confirm appointment with status: " + status);
    }

    this.status = AppointmentStatus.CONFIRMED;
    this.updatedAt = LocalDateTime.now();
    validate();
    this.registerEvent(
        new AppointmentEvent.AppointmentConfirmedEvent(this.id.toString(), LocalDateTime.now()));
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
    validate();
    this.registerEvent(
        new AppointmentEvent.AppointmentCancelledEvent(
            this.id.toString(),
            LocalDateTime.now(),
            patientId.value().toString(),
            professionalId.value().toString(),
            serviceId.value().toString(),
            startTime));
  }

  /** Mark the appointment as completed. */
  public void markAsCompleted() {
    if (!status.canBeMarkedAsCompleted()) {
      throw new IllegalStateException(
          "Cannot mark appointment as completed with status: " + status);
    }

    this.status = AppointmentStatus.COMPLETED;
    this.updatedAt = LocalDateTime.now();
    validate();
    this.registerEvent(
        new AppointmentEvent.AppointmentCompletedEvent(this.id.toString(), LocalDateTime.now()));
  }

  /** Mark the appointment as no-show. */
  public void markAsNoShow() {
    if (!status.canBeMarkedAsNoShow()) {
      throw new IllegalStateException("Cannot mark appointment as no-show with status: " + status);
    }

    this.status = AppointmentStatus.NO_SHOW;
    this.updatedAt = LocalDateTime.now();
    validate();
    this.registerEvent(
        new AppointmentEvent.AppointmentNoShowEvent(this.id.toString(), LocalDateTime.now()));
  }

  /**
   * Reschedule the appointment to a new time. Cannot reschedule if appointment is completed or
   * no-show.
   */
  public void reschedule(final LocalDateTime newStartTime) {
    assertStartTimeIsInFuture(newStartTime);

    if (!status.canBeUpdated()) {
      throw new IllegalStateException("Cannot reschedule appointment with status: " + status);
    }

    this.startTime = newStartTime;
    this.updatedAt = LocalDateTime.now();
    validate();
    this.registerEvent(
        new AppointmentEvent.AppointmentUpdatedEvent(
            this.id.toString(),
            LocalDateTime.now(),
            patientId.value().toString(),
            professionalId.value().toString(),
            serviceId.value().toString(),
            newStartTime,
            virtualMeetingLink));
  }

  /**
   * Register the virtual meeting link for the appointment. Cannot register if appointment mode is
   * not virtual. Cannot register if appointment is completed or no-show.
   */
  public void registerVirtualMeetingLink(final String virtualMeetingLink) {
    if (!status.canBeUpdated()) {
      throw new IllegalStateException(
          "Cannot register virtual meeting link for appointment with status: " + status);
    }

    if (StringUtils.isBlank(virtualMeetingLink)) {
      throw new IllegalArgumentException("Virtual meeting link cannot be blank");
    }

    this.virtualMeetingLink = virtualMeetingLink;
    this.updatedAt = LocalDateTime.now();
    validate();
    this.registerEvent(
        new AppointmentEvent.AppointmentUpdatedEvent(
            this.id.toString(),
            LocalDateTime.now(),
            patientId.value().toString(),
            professionalId.value().toString(),
            serviceId.value().toString(),
            startTime,
            virtualMeetingLink));
  }
}
