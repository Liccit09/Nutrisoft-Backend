package com.nutrisoft.core.shared.component.auth.application.listener;

import com.nutrisoft.core.shared.component.auth.application.notification.SendWelcomeEmailUseCase;
import com.nutrisoft.core.shared.component.auth.domain.CredentialEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Credential Registration Event Listener.
 *
 * <p>Located in: Core\Shared\Component\Notification\Application
 *
 * <p>This component listens to CredentialEvent.CredentialCreatedDomainEvent and coordinates the
 * notification flow by delegating to SendWelcomeEmailUseCase.
 *
 * <p>The listener is invoked AFTER the transaction is committed (@TransactionalEventListener with
 * phase = AFTER_COMMIT) to ensure the credential has been persisted before sending the email.
 *
 * <p>NOTE: Credential registration is handled by PatientRegistrationEventListener and
 * ProfessionalRegistrationEventListener in the auth component. This listener is responsible ONLY
 * for orchestrating notification delivery.
 *
 * <p>Event Flow: RegistrarPaciente -> PacienteRegistradoDomainEvent -> ConsumidorAuth ->
 * RegistrarCedencial -> CredentialCreatedDomainEvent -> ConsumidorNotificacion ->
 * EnviarCorreoElectronico
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CredentialRegistrationEventListener {
  private final SendWelcomeEmailUseCase sendWelcomeEmailUseCase;

  /**
   * Handle credential registration event and orchestrate email notification.
   *
   * <p>This method is called after the transaction is committed, ensuring the credential has been
   * persisted. Delegates to SendWelcomeEmailUseCase to send the welcome email.
   *
   * <p>This listener is responsible ONLY for coordinating notification. The credential registration
   * is handled by PatientRegistrationEventListener and ProfessionalRegistrationEventListener in the
   * auth component.
   *
   * <p>Uses @Async to execute in a separate thread/transaction context, preventing transaction
   * context issues when called from nested event listeners.
   *
   * @param event The wrapped DomainEvent
   */
  @TransactionalEventListener
  public void handleCredentialRegistrationEvent(
      CredentialEvent.CredentialCreatedDomainEvent event) {

    log.info("Handling credential registration event for user: {}", event.email());
    try {
      // Delegate to use case to send welcome email
      sendWelcomeEmailUseCase.execute(event.email(), event.password());
      log.info("Notification orchestrated successfully for: {}", event.email());
    } catch (Exception e) {
      log.error("Error handling credential registration event for user: {}", event.email(), e);
      // Don't throw exception - this is an async operation
    }
  }
}
