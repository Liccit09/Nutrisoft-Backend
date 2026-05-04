package com.nutrisoft.core.shared.component.auth.domain;

import com.nutrisoft.core.shared.ddd.DomainEvent;
import java.time.LocalDateTime;

import lombok.NonNull;

public class CredentialEvent {

  public record CredentialCreatedDomainEvent(
      String aggregateId, String email, String password, LocalDateTime occurredAt)
      implements DomainEvent {

    @Override
    public LocalDateTime getOccurredAt() {
      return occurredAt;
    }

    @Override
    public String getEventType() {
      return "CredentialCreated";
    }

    @Override
    public String getAggregateId() {
      return aggregateId;
    }
  }

  /** Event published when a credential password is updated. */
  public static class CredentialPasswordUpdatedEvent
      implements DomainEvent {
    private final String credentialId;
    private final String email;
    private final LocalDateTime occurredAt;

    public CredentialPasswordUpdatedEvent(
        @NonNull String credentialId, @NonNull String email, LocalDateTime occurredAt) {
      this.credentialId = credentialId;
      this.email = email;
      this.occurredAt = occurredAt;
    }

    public String getCredentialId() {
      return credentialId;
    }

    public String getEmail() {
      return email;
    }

    @Override
    public String getEventType() {
      return "CREDENTIAL_PASSWORD_UPDATED";
    }

    @Override
    public String getAggregateId() {
      return credentialId;
    }

    @Override
    public LocalDateTime getOccurredAt() {
      return occurredAt;
    }
  }

  /** Event published when a credential is deactivated. */
  public static class CredentialDeactivatedEvent
      implements DomainEvent {
    private final String credentialId;
    private final String email;
    private final LocalDateTime occurredAt;

    public CredentialDeactivatedEvent(
        @NonNull String credentialId, @NonNull String email, LocalDateTime occurredAt) {
      this.credentialId = credentialId;
      this.email = email;
      this.occurredAt = occurredAt;
    }

    public String getCredentialId() {
      return credentialId;
    }

    public String getEmail() {
      return email;
    }

    @Override
    public String getEventType() {
      return "CREDENTIAL_DEACTIVATED";
    }

    @Override
    public String getAggregateId() {
      return credentialId;
    }

    @Override
    public LocalDateTime getOccurredAt() {
      return occurredAt;
    }
  }

  /** Event published when a credential is activated. */
  public static class CredentialActivatedEvent
      implements DomainEvent {
    private final String credentialId;
    private final String email;
    private final LocalDateTime occurredAt;

    public CredentialActivatedEvent(
        @NonNull String credentialId, @NonNull String email, LocalDateTime occurredAt) {
      this.credentialId = credentialId;
      this.email = email;
      this.occurredAt = occurredAt;
    }

    public String getCredentialId() {
      return credentialId;
    }

    public String getEmail() {
      return email;
    }

    @Override
    public String getEventType() {
      return "CREDENTIAL_ACTIVATED";
    }

    @Override
    public String getAggregateId() {
      return credentialId;
    }

    @Override
    public LocalDateTime getOccurredAt() {
      return occurredAt;
    }
  }
}
