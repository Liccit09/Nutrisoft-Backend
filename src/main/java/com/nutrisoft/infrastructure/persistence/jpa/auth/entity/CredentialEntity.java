package com.nutrisoft.infrastructure.persistence.jpa.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA Entity for authentication credentials.
 *
 * <p>Located in: Infrastructure\Persistence\JPA\Auth\Entity
 *
 * <p>This entity stores authentication data (email, password, role) without representing a User.
 * Credentials are linked to domain aggregates (Patient, Professional, etc.) via aggregateId.
 */
@Entity
@Table(name = "credentials", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CredentialEntity {

  @Id
  @Column(columnDefinition = "UUID")
  private UUID id;

  @Column(nullable = false, unique = true, length = 255)
  private String email;

  @Column(nullable = false)
  private String passwordHash;

  @Column(nullable = false, length = 20)
  private String role;

  @Column(nullable = false)
  private UUID aggregateId;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(nullable = false)
  @Builder.Default
  private Boolean active = true;
}
