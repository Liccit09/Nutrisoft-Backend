package com.nutrisoft.core.shared.component.service;

import com.nutrisoft.core.shared.ddd.AggregateRootId;
import java.util.UUID;

public record ServiceId(UUID value) implements AggregateRootId<UUID> {

  public static ServiceId create() {
    return new ServiceId(UUID.randomUUID());
  }

  public static ServiceId of(final UUID value) {
    return new ServiceId(value);
  }
}
