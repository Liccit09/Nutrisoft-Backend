package com.nutrisoft.core.shared.component.auth.domain;

import com.nutrisoft.core.shared.component.common.Email;
import com.nutrisoft.core.shared.ddd.ValueObject;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/**
 * Credential Value Object. Represents authentication credentials for a user. This object does not
 * represent a User entity (which doesn't exist in DDD). Instead, it holds authentication data that
 * can be associated with any aggregate root (Patient, Professional, etc.) via their ID.
 *
 * <p>Located in: Shared Kernel - Core Layer
 */
@Getter
@EqualsAndHashCode
public class Credential implements ValueObject {

  private final UUID aggregateId; // ID of the aggregate (Patient, Professional, etc.)
  private final Email email;
  private final String passwordHash;
  private final UserRole role;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;
  private final boolean active;

  public Credential(
      @NonNull UUID aggregateId,
      @NonNull Email email,
      @NonNull String passwordHash,
      @NonNull UserRole role,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      boolean active) {
    this.aggregateId = aggregateId;
    this.email = email;
    this.passwordHash = passwordHash;
    this.role = role;
    this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    this.active = active;
  }

  public static Credential create(
      @NonNull UUID aggregateId,
      @NonNull Email email,
      @NonNull String passwordHash,
      @NonNull UserRole role) {
    return new Credential(
        aggregateId, email, passwordHash, role, LocalDateTime.now(), LocalDateTime.now(), true);
  }

  public Credential updatePassword(@NonNull String newPasswordHash) {
    return new Credential(
        this.aggregateId,
        this.email,
        newPasswordHash,
        this.role,
        this.createdAt,
        LocalDateTime.now(),
        this.active);
  }

  public Credential deactivate() {
    return new Credential(
        this.aggregateId,
        this.email,
        this.passwordHash,
        this.role,
        this.createdAt,
        LocalDateTime.now(),
        false);
  }
}
