package com.nutrisoft.userinterface.api.rest.patient;

import com.nutrisoft.core.port.out.auth.IdentityManager;
import com.nutrisoft.core.shared.component.common.Email;
import com.nutrisoft.userinterface.api.rest.auth.security.JwtTokenProvider;
import com.nutrisoft.userinterface.api.rest.auth.service.AuthenticationService;
import com.nutrisoft.userinterface.api.rest.patient.generated.PatientsApi;
import com.nutrisoft.userinterface.api.rest.patient.generated.model.PatientAuthResponse;
import com.nutrisoft.userinterface.api.rest.patient.generated.model.RegisterPatientRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

/**
 * Patient Controller (Primary Adapter - REST).
 *
 * <p>Located in: UserInterface\API\REST\Patient
 *
 * <p>Responsabilidades: - Recibir solicitudes HTTP de pacientes - Validar DTOs - Orquestar
 * seguridad y generación de JWT - Retornar respuestas HTTP
 *
 * <p>Delega lógica de negocio a AuthenticationService y casos de uso.
 *
 * <p>API-First: Implementa la interfaz {@link PatientsApi} generada desde
 * /src/main/resources/openapi/patient-api-v1.yaml
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class PatientController implements PatientsApi {

  private final AuthenticationService authenticationService;
  private final IdentityManager identityManager;
  private final JwtTokenProvider jwtTokenProvider;

  /**
   * Register a new patient. POST /api/v1/patients/register
   *
   * @param request Patient registration data
   * @return ResponseEntity with PatientAuthResponse (201 Created)
   * @throws IllegalArgumentException if validation fails
   * @throws RuntimeException if credential not found after registration
   */
  @Override
  public ResponseEntity<PatientAuthResponse> registerPatient(RegisterPatientRequest request) {

    log.info("Patient registration endpoint called for email: {}", request.getEmail());

    // Delegar al caso de uso
    var patientId = authenticationService.registerPatient(request);

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
    PatientAuthResponse response =
        new PatientAuthResponse()
            .token(token)
            .type("Bearer")
            .aggregateId(patientId)
            .email(request.getEmail())
            .role(PatientAuthResponse.RoleEnum.PATIENT);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
