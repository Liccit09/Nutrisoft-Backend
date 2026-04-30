package com.nutrisoft.userinterface.api.rest.professional;

import com.nutrisoft.core.port.out.auth.IdentityManager;
import com.nutrisoft.core.shared.component.common.Email;
import com.nutrisoft.userinterface.api.rest.auth.security.JwtTokenProvider;
import com.nutrisoft.userinterface.api.rest.auth.service.AuthenticationService;
import com.nutrisoft.userinterface.api.rest.professional.generated.ProfessionalsApi;
import com.nutrisoft.userinterface.api.rest.professional.generated.model.ProfessionalAuthResponse;
import com.nutrisoft.userinterface.api.rest.professional.generated.model.RegisterProfessionalRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

/**
 * Professional Controller (Primary Adapter - REST).
 *
 * <p>Located in: UserInterface\API\REST\Professional
 *
 * <p>Responsabilidades:
 * - Recibir solicitudes HTTP de profesionales
 * - Validar DTOs
 * - Orquestar seguridad y generación de JWT
 * - Retornar respuestas HTTP
 *
 * <p>Delega lógica de negocio a AuthenticationService y casos de uso.
 *
 * <p>API-First: Implementa la interfaz {@link ProfessionalsApi} generada desde
 * /src/main/resources/openapi/professional-api-v1.yaml
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ProfessionalController implements ProfessionalsApi {

  private final AuthenticationService authenticationService;
  private final IdentityManager identityManager;
  private final JwtTokenProvider jwtTokenProvider;

  /**
   * Register a new professional.
   * POST /api/v1/professionals/register
   *
   * @param request Professional registration data
   * @return ResponseEntity with ProfessionalAuthResponse (201 Created)
   * @throws IllegalArgumentException if validation fails
   * @throws RuntimeException if credential not found after registration
   */
  @Override
  public ResponseEntity<ProfessionalAuthResponse> registerProfessional(
      RegisterProfessionalRequest request) {

    log.info("Professional registration endpoint called for email: {}", request.getEmail());

    // Delegar al caso de uso
    var professionalId = authenticationService.registerProfessional(request);

    // Recuperar credential para obtener rol
    Email emailVO = Email.of(request.getEmail());
    var credential =
        identityManager
            .findByEmail(emailVO)
            .orElseThrow(() -> new RuntimeException("Credential not found after registration"));

    // Generar JWT token
    Authentication authentication =
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
    String token = jwtTokenProvider.generateToken(authentication, credential.getRole().name());

    // Construir respuesta
    ProfessionalAuthResponse response =
        new ProfessionalAuthResponse()
            .token(token)
            .type("Bearer")
            .aggregateId(professionalId)
            .email(request.getEmail())
            .role(ProfessionalAuthResponse.RoleEnum.PROFESSIONAL);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}

