package com.nutrisoft.core.shared.component.patient;

import com.nutrisoft.core.shared.ddd.DomainEvent;
import java.time.LocalDateTime;

public class PatientEvent {

  public record PatientCreatedEvent(
      String aggregateId, String email, String firstName, String lastName, LocalDateTime occurredAt)
      implements DomainEvent {

    @Override
    public String getEventType() {
      return "PATIENT_CREATED";
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
