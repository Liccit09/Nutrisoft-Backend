package com.nutrisoft.core.component.appointment.domain;

import com.nutrisoft.core.shared.ddd.ValueObject;

/**
 * AppointmentStatus Value Object. Represents the state of an appointment.
 *
 * <p>Located in: Shared Kernel - Core Layer Shared by appointment component and can be used by
 * other components
 *
 * <p>Possible states: - SCHEDULED: Appointment is scheduled - CONFIRMED: Appointment is confirmed
 * by patient - CANCELLED: Appointment is cancelled - COMPLETED: Appointment is completed - NO_SHOW:
 * Patient did not show up
 */
public enum AppointmentStatus implements ValueObject {
  SCHEDULED,
  CONFIRMED,
  CANCELLED,
  COMPLETED,
  NO_SHOW;

  public static AppointmentStatus fromString(String status) {
    try {
      return AppointmentStatus.valueOf(status.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid appointment status: " + status);
    }
  }

  public boolean isScheduled() {
    return SCHEDULED.equals(this);
  }

  public boolean isConfirmed() {
    return CONFIRMED.equals(this);
  }

  public boolean isCancelled() {
    return CANCELLED.equals(this);
  }

  public boolean isCompleted() {
    return COMPLETED.equals(this);
  }

  public boolean isNoShow() {
    return NO_SHOW.equals(this);
  }

  public boolean canBeCancelled() {
    return isScheduled() || isConfirmed();
  }

  public boolean canBeConfirmed() {
    return isScheduled();
  }

  public boolean canBeUpdated() {
    return isScheduled() || isConfirmed();
  }
}
