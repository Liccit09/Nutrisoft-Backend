package com.nutrisoft.userinterface.api.rest.auth;

import com.nutrisoft.core.port.out.auth.IdentityManager;
import com.nutrisoft.core.shared.component.common.Email;
import com.nutrisoft.userinterface.api.rest.auth.generated.AuthenticationApi;
import com.nutrisoft.userinterface.api.rest.auth.generated.model.AuthResponse;
import com.nutrisoft.userinterface.api.rest.auth.generated.model.LoginRequest;
import com.nutrisoft.userinterface.api.rest.auth.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

  private final IdentityManager identityManager;
  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;

  /** Login user. POST /api/v1/auth/login */
  @Override
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    log.info("Login endpoint called for email: {}", request.getEmail());

    // Recuperar credential para validar que existe y está activa
    Email emailVO = Email.of(request.getEmail());
    var credential =
        identityManager
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

    // Generar JWT token
    String token = jwtTokenProvider.generateToken(authentication, credential.getRole().name());
    log.info("User successfully authenticated: {}", request.getEmail());

    // Construir respuesta
    AuthResponse response =
        new AuthResponse()
            .token(token)
            .type("Bearer")
            .aggregateId(credential.getAggregateId())
            .email(request.getEmail())
            .role(AuthResponse.RoleEnum.valueOf(credential.getRole().name()));

    return ResponseEntity.ok(response);
  }
}
