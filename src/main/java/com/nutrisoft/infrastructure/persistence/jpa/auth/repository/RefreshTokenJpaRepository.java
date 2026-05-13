package com.nutrisoft.infrastructure.persistence.jpa.auth.repository;

import com.nutrisoft.infrastructure.persistence.jpa.auth.entity.RefreshTokenEntity;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * JPA Repository for RefreshToken entity.
 *
 * <p>Provides database operations for refresh token management.
 */
public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, UUID> {

  /**
   * Find a refresh token by its token string
   */
  Optional<RefreshTokenEntity> findByToken(String token);

  /**
   * Find valid (not revoked and not expired) refresh tokens for a credential
   */
  @Query("SELECT rt FROM RefreshTokenEntity rt WHERE rt.credential.id = :credentialId AND rt.revoked = false AND rt.expiresAt > CURRENT_TIMESTAMP")
  Optional<RefreshTokenEntity> findValidByCredentialId(@Param("credentialId") UUID credentialId);

  /**
   * Delete expired refresh tokens (cleanup old tokens)
   */
  @Query("DELETE FROM RefreshTokenEntity rt WHERE rt.expiresAt <= :now")
  void deleteExpiredTokens(@Param("now") LocalDateTime now);

  /**
   * Count active (non-revoked) refresh tokens for a credential
   */
  @Query("SELECT COUNT(rt) FROM RefreshTokenEntity rt WHERE rt.credential.id = :credentialId AND rt.revoked = false")
  int countValidByCredentialId(@Param("credentialId") UUID credentialId);

  /**
   * Revoke all active refresh tokens for a credential
   */
  @Query("UPDATE RefreshTokenEntity rt SET rt.revoked = true, rt.revokedAt = CURRENT_TIMESTAMP WHERE rt.credential.id = :credentialId AND rt.revoked = false")
  void revokeAllByCredentialId(@Param("credentialId") UUID credentialId);
}


