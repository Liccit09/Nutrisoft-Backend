package com.nutrisoft.core.component.appointment.domain;

import com.nutrisoft.core.shared.ddd.ValueObject;

public enum AppointmentMode implements ValueObject {
  IN_PERSON,
  VIRTUAL;

  public boolean isInPerson() {
    return IN_PERSON.equals(this);
  }

  public boolean isVirtual() {
    return VIRTUAL.equals(this);
  }
}
