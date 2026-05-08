package com.nutrisoft.userinterface.api.rest.professional;

import com.nutrisoft.core.component.professional.application.usecase.GetAllProfessionalsUseCase;
import com.nutrisoft.core.component.professional.application.usecase.RegisterProfessionalUseCase;
import com.nutrisoft.userinterface.api.rest.professional.generated.ProfessionalsApi;
import com.nutrisoft.userinterface.api.rest.professional.generated.model.ProfessionalAuthResponse;
import com.nutrisoft.userinterface.api.rest.professional.generated.model.ProfessionalResponse;
import com.nutrisoft.userinterface.api.rest.professional.generated.model.RegisterProfessionalRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Professional Controller (Primary Adapter - REST).
 *
 * <p>Located in: UserInterface\API\REST\Professional
 *
 * <p>Responsabilidades:
 * - Recibir solicitudes HTTP de profesionales
 * - Validar DTOs
 * - Orquestar registro de profesionales
 * - Retornar respuestas HTTP
 *
 * <p>Delega lógica de negocio a RegisterProfessionalUseCase. Las credenciales se crean de forma
 * asincrónica mediante eventos de dominio.
 *
 * <p>API-First: Implementa la interfaz {@link ProfessionalsApi} generada desde
 * /src/main/resources/openapi/professional-api-v1.yaml
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ProfessionalController implements ProfessionalsApi {

  private final RegisterProfessionalUseCase registerProfessionalUseCase;
  private final GetAllProfessionalsUseCase getAllProfessionalsUseCase;

  /**
   * Get all professionals.
   * GET /api/v1/professionals
   *
   * <p>This endpoint retrieves all professionals from the database. It is a public endpoint.
   *
   * @return ResponseEntity with list of ProfessionalResponse (200 OK)
   */
  @Override
  public ResponseEntity<java.util.List<ProfessionalResponse>> getAllProfessionals() {

    log.info("Get all professionals endpoint called");

    // Get all professionals using the use case
    var professionals = getAllProfessionalsUseCase.execute();

    // Convert domain professionals to API responses
    java.util.List<ProfessionalResponse> responses = professionals.stream()
        .map(professional -> new ProfessionalResponse()
            .aggregateId(professional.getId().value())
            .firstName(professional.getFirstName())
            .lastName(professional.getLastName())
            .specialization(professional.getSpecialization())
            .phoneNumber(professional.getContactInfo().phoneNumber())
            .email(professional.getContactInfo().email().value())
        )
        .toList();

    log.info("Retrieved {} professionals successfully", responses.size());

    return ResponseEntity.ok(responses);
  }

  /**
   * Register a new professional.
   * POST /api/v1/professionals/register
   *
   * <p>This endpoint registers a new professional and returns the professional ID. Credentials are
   * created asynchronously via the event-driven flow (ProfessionalRegistrationEventListener).
   *
   * @param request Professional registration data
   * @return ResponseEntity with ProfessionalAuthResponse (201 Created) containing professional ID
   * @throws IllegalArgumentException if validation fails
   */
  @Override
  public ResponseEntity<ProfessionalAuthResponse> registerProfessional(
      RegisterProfessionalRequest request) {

    log.info("Professional registration endpoint called for email: {}", request.getEmail());

    // Register professional using the use case
    var professionalId =
        registerProfessionalUseCase.execute(
            request.getFirstName(),
            request.getLastName(),
            request.getEmail(),
            request.getPhoneNumber(),
            request.getSpecialization()
        ); // Password is null - handled by AuthenticationService if needed

    // Construir respuesta con ID del profesional
    ProfessionalAuthResponse response =
        new ProfessionalAuthResponse()
            .aggregateId(professionalId.value());

    log.info("Professional registered successfully with ID: {}", professionalId.value());

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}




