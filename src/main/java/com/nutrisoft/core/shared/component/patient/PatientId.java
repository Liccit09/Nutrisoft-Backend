package com.nutrisoft.core.shared.component.patient;

import com.nutrisoft.core.shared.ddd.AggregateRootId;
import java.util.UUID;

public record PatientId(UUID value) implements AggregateRootId<UUID> {

  public static PatientId create() {
    return new PatientId(UUID.randomUUID());
  }

  public static PatientId of(final UUID value) {
    return new PatientId(value);
  }
}
