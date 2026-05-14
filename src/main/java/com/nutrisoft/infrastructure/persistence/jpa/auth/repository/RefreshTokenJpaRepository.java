package com.nutrisoft.infrastructure.persistence.jpa.auth.repository;

import com.nutrisoft.infrastructure.persistence.jpa.auth.entity.RefreshTokenEntity;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * JPA Repository for RefreshToken entity.
 *
 * <p>Provides database operations for refresh token management.
 */
public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, UUID> {

  /**
   * Find a valid (not revoked and not expired) refresh token by its token string.
   * Returns a list (should typically have 0 or 1 result) to avoid NonUniqueResultException.
   */
  @Query("SELECT rt FROM RefreshTokenEntity rt WHERE rt.token = :token AND rt.revoked = false AND rt.expiresAt > CURRENT_TIMESTAMP ORDER BY rt.createdAt DESC")
  java.util.List<RefreshTokenEntity> findByToken(@Param("token") String token);

  /**
   * Find any refresh token by its token string (regardless of status - used for revocation).
   * Returns a list to avoid NonUniqueResultException when multiple records exist.
   */
  @Query("SELECT rt FROM RefreshTokenEntity rt WHERE rt.token = :token ORDER BY rt.createdAt DESC")
  java.util.List<RefreshTokenEntity> findByTokenAnyStatus(@Param("token") String token);

  /**
   * Find valid (not revoked and not expired) refresh tokens for a credential
   */
  @Query("SELECT rt FROM RefreshTokenEntity rt WHERE rt.credential.id = :credentialId AND rt.revoked = false AND rt.expiresAt > CURRENT_TIMESTAMP")
  Optional<RefreshTokenEntity> findValidByCredentialId(@Param("credentialId") UUID credentialId);

  /**
   * Delete expired refresh tokens (cleanup old tokens)
   */
  @Modifying
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
  @Modifying
  @Query("UPDATE RefreshTokenEntity rt SET rt.revoked = true, rt.revokedAt = CURRENT_TIMESTAMP WHERE rt.credential.id = :credentialId AND rt.revoked = false")
  void revokeAllByCredentialId(@Param("credentialId") UUID credentialId);
}


