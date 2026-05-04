package com.nutrisoft.core.shared.component.professional;

import com.nutrisoft.core.shared.ddd.DomainEvent;
import java.time.LocalDateTime;

public class ProfessionalEvent {

  public record ProfessionalCreatedEvent(
      String aggregateId, String email, String firstName, String lastName, LocalDateTime occurredAt)
      implements DomainEvent {

    @Override
    public String getEventType() {
      return "PROFESIONAL_REGISTRADO";
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
