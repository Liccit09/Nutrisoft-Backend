package com.nutrisoft.userinterface.api.rest.auth;

import com.nutrisoft.core.port.out.auth.CredentialRepository;
import com.nutrisoft.core.shared.component.common.Email;
import com.nutrisoft.infrastructure.persistence.jpa.auth.entity.CredentialEntity;
import com.nutrisoft.infrastructure.persistence.jpa.auth.repository.CredentialJpaRepository;
import com.nutrisoft.infrastructure.persistence.jpa.auth.repository.RefreshTokenJpaRepository;
import com.nutrisoft.userinterface.api.rest.auth.generated.AuthenticationApi;
import com.nutrisoft.userinterface.api.rest.auth.generated.model.AuthResponse;
import com.nutrisoft.userinterface.api.rest.auth.generated.model.LoginRequest;
import com.nutrisoft.userinterface.api.rest.auth.generated.model.ErrorResponse;
import com.nutrisoft.userinterface.api.rest.auth.security.JwtTokenProvider;
import com.nutrisoft.userinterface.api.rest.auth.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Authentication Controller (Primary Adapter - REST).
 *
 * <p>Located in: UserInterface\API\REST\Auth
 *
 * <p>Responsabilidades: - Recibir solicitudes HTTP - Validar DTOs - Orquestar seguridad y
 * generación de JWT - Retornar respuestas HTTP
 *
 * <p>Delega lógica de negocio a AuthenticationService y casos de uso.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController implements AuthenticationApi {

  private final CredentialRepository credentialRepository;
  private final CredentialJpaRepository credentialJpaRepository;
  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;
  private final RefreshTokenService refreshTokenService;
  private final RefreshTokenJpaRepository refreshTokenRepository;

  /** Login user. POST /api/v1/auth/login */
  @Override
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    log.info("Login endpoint called for email: {}", request.getEmail());

    // Recuperar credential para validar que existe y está activa
    Email emailVO = Email.of(request.getEmail());
    var credential =
        credentialRepository
            .findByEmail(emailVO)
            .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

    if (!credential.isActive()) {
      log.warn("Login attempt for inactive credential: {}", request.getEmail());
      throw new IllegalArgumentException("Account is disabled");
    }

    // Autenticar con Spring Security
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    // Generar JWT access token (24 horas)
    String token = jwtTokenProvider.generateToken(authentication, credential.getRole().name());
    
    // Generar refresh token (7 días) y persistir en BD
    String refreshTokenJwt = jwtTokenProvider.generateRefreshToken(request.getEmail(), credential.getRole().name());
    
    // Obtener información adicional para auditoría
    String userAgent = null;
    String ipAddress = null;
    ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attrs != null) {
      userAgent = attrs.getRequest().getHeader("User-Agent");
      ipAddress = attrs.getRequest().getRemoteAddr();
    }
    
    // Obtener la entidad de credential JPA para persistencia
    CredentialEntity credentialEntity = credentialJpaRepository
        .findByEmail(request.getEmail())
        .orElseThrow(() -> new IllegalArgumentException("Credential not found"));
    
    // Persistir el refresh token en BD
    refreshTokenService.createRefreshToken(credentialEntity, refreshTokenJwt, userAgent, ipAddress);

    log.info("User successfully authenticated: {}", request.getEmail());

    // Construir respuesta
    AuthResponse response =
        new AuthResponse()
            .token(token)
            .type("Bearer")
            .aggregateId(credential.getAggregateId())
            .email(request.getEmail())
            .role(AuthResponse.RoleEnum.valueOf(credential.getRole().name()));

    // Set refresh token as HttpOnly cookie (7 days)
    ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshTokenJwt)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(7 * 24 * 60 * 60) // 7 días
        .sameSite("Strict")
        .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(response);
  }

  /** Refresh access token using refresh token cookie */
  @Override
  public ResponseEntity<AuthResponse> refreshToken(
      @CookieValue(value = "refreshToken", required = false) final String refreshToken,
      @org.springframework.web.bind.annotation.RequestBody(required = false) Object body) {
    // Validar que el refresh token fue proporcionado
    if (refreshToken == null || refreshToken.isEmpty()) {
      log.warn("Refresh token endpoint called without refresh token cookie");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(null);
    }

    // Validar el refresh token en BD
    var refreshTokenEntity = refreshTokenService.validateRefreshToken(refreshToken);
    if (refreshTokenEntity.isEmpty()) {
      log.warn("Invalid or revoked refresh token");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(null);
    }

    // Validar que el JWT es válido
    if (!jwtTokenProvider.validateToken(refreshToken)) {
      log.warn("Refresh token JWT validation failed");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(null);
    }

    // Extraer información del token
    String email = jwtTokenProvider.getEmailFromToken(refreshToken);
    String role = jwtTokenProvider.getRoleFromToken(refreshToken);
    String tokenType = jwtTokenProvider.getTokenTypeFromToken(refreshToken);

    // Validar que es un refresh token, no un access token
    if (!"refresh".equals(tokenType)) {
      log.warn("Token provided to refresh endpoint is not a refresh token");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(null);
    }

    // Recuperar el credential para obtener el aggregateId
    CredentialEntity credentialEntity = credentialJpaRepository
        .findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("Credential not found"));

    // Generar nuevo access token
    String newAccessToken = jwtTokenProvider.generateTokenFromEmailAndRole(email, role);

    // Generar nuevo refresh token (token rotation)
    String newRefreshToken = jwtTokenProvider.generateRefreshToken(email, role);
    
    // Obtener información adicional para auditoría
    String userAgent = null;
    String ipAddress = null;
    ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attrs != null) {
      userAgent = attrs.getRequest().getHeader("User-Agent");
      ipAddress = attrs.getRequest().getRemoteAddr();
    }

    // Persistir el nuevo refresh token
    refreshTokenService.createRefreshToken(credentialEntity, newRefreshToken, userAgent, ipAddress);

    // Revocar el antiguo refresh token (token rotation)
    refreshTokenService.revokeRefreshToken(refreshToken);

    log.info("Access token refreshed for user: {}", email);

    // Construir respuesta con nuevo access token
    AuthResponse response = new AuthResponse()
        .token(newAccessToken)
        .type("Bearer")
        .aggregateId(credentialEntity.getAggregateId())
        .email(email)
        .role(AuthResponse.RoleEnum.valueOf(role));

    // Set new refresh token as HttpOnly cookie
    ResponseCookie cookie = ResponseCookie.from("refreshToken", newRefreshToken)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(7 * 24 * 60 * 60) // 7 días
        .sameSite("Strict")
        .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(response);
  }

  /** Logout user by revoking refresh token POST /api/v1/auth/logout */
  @Override
  public ResponseEntity<Void> logout(final String refreshToken) {
    if (refreshToken != null && !refreshToken.isEmpty()) {
      try {
        // Revocar el refresh token
        refreshTokenService.revokeRefreshToken(refreshToken);
        log.info("User logged out successfully");
      } catch (Exception e) {
        log.warn("Error revoking refresh token during logout: {}", e.getMessage());
      }
    }

    // Limpiar la cookie (borrar contenido y expirar)
    ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(0)  // Expira inmediatamente
        .sameSite("Strict")
        .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .build();
  }
}
