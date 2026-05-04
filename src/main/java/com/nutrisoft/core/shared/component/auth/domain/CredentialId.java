package com.nutrisoft.core.shared.component.auth.domain;

import com.nutrisoft.core.shared.ddd.AggregateRootId;
import java.util.UUID;
import lombok.NonNull;

/**
 * Credential Aggregate Root Identifier.
 *
 * <p>Located in: Core\Shared\Component\Auth\Domain
 *
 * <p>Unique identifier for Credential aggregates.
 */
public record CredentialId(UUID value) implements AggregateRootId<UUID> {

  public static CredentialId create() {
    return new CredentialId(UUID.randomUUID());
  }

  public static CredentialId of(@NonNull final UUID value) {
    return new CredentialId(value);
  }
}
