package com.nutrisoft.userinterface.api.rest.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Utility class for JWT token generation and validation.
 *
 * <p>Genera tokens JWT incluyendo claims de email y role. No depende de UserPrincipal para generar
 * tokens, lo que permite crear tokens en cualquier contexto (no solo cuando ya existe UserPrincipal
 * en SecurityContext).
 */
@Slf4j
@Component
public class JwtTokenProvider {

  @Value(
      "${app.jwtSecret:nutrisoft-secret-key-for-jwt-token-generation-and-validation-purpose-only}")
  private String jwtSecret;

  @Value("${app.jwtExpirationMs:86400000}")
  private int jwtExpirationMs;

  @Value("${app.jwtRefreshExpirationMs:604800000}")
  private int jwtRefreshExpirationMs; // 7 days by default

  /**
   * Generate JWT token from Authentication object. Usa el principal de la Authentication (que puede
   * ser email o UserPrincipal).
   *
   * @param authentication Debe contener el email como principal (getName())
   * @param role El rol del usuario (PATIENT, PROFESSIONAL, ADMIN)
   * @return JWT token con claims: sub, email, role
   */
  public String generateToken(Authentication authentication, String role) {
    // El principal puede ser:
    // 1. String (email) - cuando se crea en AuthController
    // 2. UserPrincipal - cuando viene de SecurityContext

    String email = authentication.getName(); // Siempre retorna el principal como String

    return generateTokenFromEmailAndRole(email, role);
  }

  /**
   * Generate JWT token from email and role. Método principal que genera el token con los claims
   * necesarios.
   *
   * @param email Email del usuario
   * @param role Rol del usuario
   * @return JWT token
   */
  public String generateTokenFromEmailAndRole(String email, String role) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

    return Jwts.builder()
        .subject(email) // Email como subject
        .claim("email", email)
        .claim("role", role)
        .claim("token_type", "access")
        .issuedAt(now)
        .expiration(expiryDate)
        .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
        .compact();
  }

  /**
   * Generate refresh JWT token. Este token tiene mayor duración y debe ser persistido en BD.
   *
   * @param email Email del usuario
   * @param role Rol del usuario
   * @return Refresh JWT token
   */
  public String generateRefreshToken(String email, String role) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtRefreshExpirationMs);

    return Jwts.builder()
        .subject(email) // Email como subject
        .claim("email", email)
        .claim("role", role)
        .claim("token_type", "refresh")
        .issuedAt(now)
        .expiration(expiryDate)
        .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
        .compact();
  }

  /** Get email from JWT token. */
  public String getEmailFromToken(String token) {
    Claims claims =
        Jwts.parser()
            .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
            .build()
            .parseClaimsJws(token)
            .getBody();

    return (String) claims.get("email");
  }

  /** Get role from JWT token. */
  public String getRoleFromToken(String token) {
    Claims claims =
        Jwts.parser()
            .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
            .build()
            .parseClaimsJws(token)
            .getBody();

    return (String) claims.get("role");
  }

  /** Get token type from JWT token (access or refresh). */
  public String getTokenTypeFromToken(String token) {
    Claims claims =
        Jwts.parser()
            .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
            .build()
            .parseClaimsJws(token)
            .getBody();

    return (String) claims.getOrDefault("token_type", "access");
  }

  /** Validate JWT token. */
  public boolean validateToken(String authToken) {
    try {
      Jwts.parser()
          .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
          .build()
          .parseClaimsJws(authToken);
      return true;
    } catch (SecurityException e) {
      log.error("Invalid JWT signature: {}", e.getMessage());
    } catch (MalformedJwtException e) {
      log.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      log.error("Expired JWT token: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      log.error("Unsupported JWT token: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      log.error("JWT claims string is empty: {}", e.getMessage());
    }
    return false;
  }
}
