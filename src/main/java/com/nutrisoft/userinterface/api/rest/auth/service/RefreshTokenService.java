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
   * Creates and persists a new refresh token
   *
   * @param credential The credential entity
   * @param token The refresh token JWT
   * @param userAgent Optional user agent for auditing
   * @param ipAddress Optional IP address for auditing
   * @return Saved RefreshTokenEntity
   */
  public RefreshTokenEntity createRefreshToken(
      CredentialEntity credential, String token, String userAgent, String ipAddress) {
    // Clean up old refresh tokens for this credential (keep only latest ones)
    // This is optional - depends on business requirements
    // refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());

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
    Optional<RefreshTokenEntity> refreshToken = refreshTokenRepository.findByToken(token);

    if (refreshToken.isEmpty()) {
      log.warn("Refresh token not found in database");
      return Optional.empty();
    }

    RefreshTokenEntity rt = refreshToken.get();

    if (rt.getRevoked()) {
      log.warn("Refresh token has been revoked for credential: {}", rt.getCredential().getEmail());
      return Optional.empty();
    }

    if (LocalDateTime.now().isAfter(rt.getExpiresAt())) {
      log.warn("Refresh token has expired for credential: {}", rt.getCredential().getEmail());
      return Optional.empty();
    }

    // Mark as used
    rt.markAsUsed();
    refreshTokenRepository.save(rt);

    return Optional.of(rt);
  }

  /**
   * Revokes a refresh token (e.g., on logout)
   *
   * @param token The refresh token JWT
   */
  public void revokeRefreshToken(String token) {
    Optional<RefreshTokenEntity> refreshToken = refreshTokenRepository.findByToken(token);
    refreshToken.ifPresent(
        rt -> {
          rt.revoke();
          refreshTokenRepository.save(rt);
          log.info("Refresh token revoked for credential: {}", rt.getCredential().getEmail());
        });
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


