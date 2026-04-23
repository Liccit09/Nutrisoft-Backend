package com.nutrisoft.core.shared.component.appointment;

import com.nutrisoft.core.shared.ddd.AggregateRootId;
import java.util.UUID;

public record AppointmentId(UUID value) implements AggregateRootId<UUID> {

  public static AppointmentId create() {
    return new AppointmentId(UUID.randomUUID());
  }

  public static AppointmentId of(final UUID value) {
    return new AppointmentId(value);
  }
}
