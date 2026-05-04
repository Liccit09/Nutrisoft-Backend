package com.nutrisoft.userinterface.api.rest.patient;

import com.nutrisoft.core.component.patient.application.usecase.RegisterPatientUseCase;
import com.nutrisoft.userinterface.api.rest.patient.generated.PatientsApi;
import com.nutrisoft.userinterface.api.rest.patient.generated.model.PatientAuthResponse;
import com.nutrisoft.userinterface.api.rest.patient.generated.model.RegisterPatientRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Patient Controller (Primary Adapter - REST).
 *
 * <p>Located in: UserInterface\API\REST\Patient
 *
 * <p>Responsabilidades: - Recibir solicitudes HTTP de pacientes - Validar DTOs - Orquestar
 * registro de pacientes - Retornar respuestas HTTP
 *
 * <p>Delega lógica de negocio a RegisterPatientUseCase. Las credenciales se crean de forma
 * asincrónica mediante eventos de dominio.
 *
 * <p>API-First: Implementa la interfaz {@link PatientsApi} generada desde
 * /src/main/resources/openapi/patient-api-v1.yaml
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class PatientController implements PatientsApi {

  private final RegisterPatientUseCase registerPatientUseCase;

  /**
   * Register a new patient. POST /api/v1/patients/register
   *
   * <p>This endpoint registers a new patient and returns the patient ID. Credentials are created
   * asynchronously via the event-driven flow (PatientRegistrationEventListener).
   *
   * @param request Patient registration data
   * @return ResponseEntity with PatientAuthResponse (201 Created) containing patient ID
   * @throws IllegalArgumentException if validation fails
   */
  @Override
  public ResponseEntity<PatientAuthResponse> registerPatient(RegisterPatientRequest request) {

    log.info("Patient registration endpoint called for email: {}", request.getEmail());

    // Register patient using the use case
    var patientId =
        registerPatientUseCase.execute(
            request.getFirstName(),
            request.getLastName(),
            request.getEmail(),
            request.getPhoneNumber()
        ); // Password is null - handled by AuthenticationService if needed

    // Construir respuesta con ID del paciente
    PatientAuthResponse response =
        new PatientAuthResponse()
            .aggregateId(patientId.value());

    log.info("Patient registered successfully with ID: {}", patientId.value());

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
