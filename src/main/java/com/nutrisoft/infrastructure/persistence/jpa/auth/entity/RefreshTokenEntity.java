package com.nutrisoft.infrastructure.persistence.jpa.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA Entity for refresh tokens.
 *
 * <p>Located in: Infrastructure\Persistence\JPA\Auth\Entity
 *
 * <p>Persists refresh tokens to enable:
 * - Token revocation (logout)
 * - Session management
 * - Refresh token rotation
 * - Audit trail of token usage
 */
@Entity
@Table(name = "refresh_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(columnDefinition = "UUID")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "credential_id", nullable = false, foreignKey = @ForeignKey(name = "fk_refresh_token_credential"))
  private CredentialEntity credential;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String token;

  @Column(nullable = false)
  private LocalDateTime expiresAt;

  @Column(name = "revoked")
  @Builder.Default
  private Boolean revoked = false;

  @Column(name = "revoked_at")
  private LocalDateTime revokedAt;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "used_at")
  private LocalDateTime usedAt;

  @Column(name = "user_agent", length = 500)
  private String userAgent;

  @Column(name = "ip_address", length = 45)
  private String ipAddress;

  /**
   * Checks if the refresh token is still valid (not revoked and not expired)
   */
  public boolean isValid() {
    return !revoked && LocalDateTime.now().isBefore(expiresAt);
  }

  /**
   * Revokes the refresh token
   */
  public void revoke() {
    this.revoked = true;
    this.revokedAt = LocalDateTime.now();
  }

  /**
   * Marks the refresh token as used
   */
  public void markAsUsed() {
    this.usedAt = LocalDateTime.now();
  }
}

