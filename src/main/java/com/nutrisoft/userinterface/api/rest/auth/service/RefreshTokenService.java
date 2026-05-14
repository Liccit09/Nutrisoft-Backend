package com.nutrisoft.userinterface.api.rest.auth.service;

import com.nutrisoft.infrastructure.persistence.jpa.auth.entity.CredentialEntity;
import com.nutrisoft.infrastructure.persistence.jpa.auth.entity.RefreshTokenEntity;
import com.nutrisoft.infrastructure.persistence.jpa.auth.repository.RefreshTokenJpaRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing refresh tokens.
 *
 * <p>Handles:
 * - Token persistence
 * - Token validation
 * - Token revocation
 * - Token rotation
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

  private final RefreshTokenJpaRepository refreshTokenRepository;

  @Value("${app.jwtRefreshExpirationMs:604800000}")
  private int jwtRefreshExpirationMs;

  /**
   * Creates and persists a new refresh token.
   * Automatically revokes all previous active tokens for this credential to prevent duplicates.
   *
   * @param credential The credential entity
   * @param token The refresh token JWT
   * @param userAgent Optional user agent for auditing
   * @param ipAddress Optional IP address for auditing
   * @return Saved RefreshTokenEntity
   */
  public RefreshTokenEntity createRefreshToken(
      CredentialEntity credential, String token, String userAgent, String ipAddress) {
    // Revoke all active (non-revoked) tokens for this credential to prevent duplicates
    // This ensures only the latest token is valid for each credential
    refreshTokenRepository.revokeAllByCredentialId(credential.getId());
    log.debug("Revoked all previous tokens for credential: {}", credential.getEmail());

    LocalDateTime expiresAt =
        LocalDateTime.now().plus(jwtRefreshExpirationMs, ChronoUnit.MILLIS);

    RefreshTokenEntity refreshTokenEntity =
        RefreshTokenEntity.builder()
            .credential(credential)
            .token(token)
            .expiresAt(expiresAt)
            .createdAt(LocalDateTime.now())
            .userAgent(userAgent)
            .ipAddress(ipAddress)
            .revoked(false)
            .build();

    return refreshTokenRepository.save(refreshTokenEntity);
  }

  /**
   * Validates a refresh token
   *
   * @param token The refresh token JWT
   * @return RefreshTokenEntity if valid, Optional.empty() if invalid or revoked
   */
  public Optional<RefreshTokenEntity> validateRefreshToken(String token) {
    java.util.List<RefreshTokenEntity> refreshTokens = refreshTokenRepository.findByToken(token);

    if (refreshTokens.isEmpty()) {
      log.warn("Refresh token not found, revoked, or expired in database");
      return Optional.empty();
    }

    // Get the first (most recent) token - in case of duplicates, use the latest one
    RefreshTokenEntity rt = refreshTokens.get(0);

    // Mark as used
    rt.markAsUsed();
    refreshTokenRepository.save(rt);

    return Optional.of(rt);
  }

  /**
   * Revokes a refresh token (e.g., on logout).
   * Revokes all records with the same token to handle duplicates.
   *
   * @param token The refresh token JWT
   */
  public void revokeRefreshToken(String token) {
    java.util.List<RefreshTokenEntity> refreshTokens = refreshTokenRepository.findByTokenAnyStatus(token);
    
    for (RefreshTokenEntity rt : refreshTokens) {
      if (!rt.getRevoked()) {  // Only revoke if not already revoked
        rt.revoke();
        refreshTokenRepository.save(rt);
        log.info("Refresh token revoked for credential: {}", rt.getCredential().getEmail());
      }
    }
  }

  /**
   * Revokes all active refresh tokens for a credential (e.g., on password change)
   *
   * @param credentialId The credential ID
   */
  public void revokeAllForCredential(java.util.UUID credentialId) {
    refreshTokenRepository.revokeAllByCredentialId(credentialId);
    log.info("All refresh tokens revoked for credential: {}", credentialId);
  }

  /**
   * Cleanup expired refresh tokens (should be run periodically)
   */
  public void cleanupExpiredTokens() {
    refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    log.info("Expired refresh tokens cleaned up");
  }
}


