package com.nutrisoft.core.shared.component.professional;

import com.nutrisoft.core.shared.ddd.AggregateRootId;
import java.util.UUID;

public record ProfessionalId(UUID value) implements AggregateRootId<UUID> {

  public static ProfessionalId create() {
    return new ProfessionalId(UUID.randomUUID());
  }

  public static ProfessionalId of(final UUID value) {
    return new ProfessionalId(value);
  }
}
