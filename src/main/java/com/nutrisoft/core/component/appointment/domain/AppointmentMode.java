package com.nutrisoft.core.component.appointment.domain;

import com.nutrisoft.core.shared.ddd.ValueObject;

public enum AppointmentMode implements ValueObject {
  IN_PERSON,
  VIRTUAL;

  public static AppointmentMode fromString(final String mode) {
    try {
      return AppointmentMode.valueOf(mode.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid appointment mode: " + mode);
    }
  }
}
