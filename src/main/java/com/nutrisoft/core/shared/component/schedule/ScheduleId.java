package com.nutrisoft.core.shared.component.schedule;

import com.nutrisoft.core.shared.ddd.AggregateRootId;
import java.util.UUID;

/**
 * ScheduleId Value Object (Identifier).
 *
 * <p>Located in: Core\Shared\Component\Schedule
 *
 * <p>Uniquely identifies a Schedule aggregate.
 */
public record ScheduleId(UUID value) implements AggregateRootId<UUID> {

  public static ScheduleId create() {
    return new ScheduleId(UUID.randomUUID());
  }

  public static ScheduleId of(final UUID value) {
    return new ScheduleId(value);
  }
}
