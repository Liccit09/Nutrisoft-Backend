package com.nutrisoft.userinterface.api.rest.service;

import com.nutrisoft.core.component.service.application.usecase.ListAllServicesUseCase;
import com.nutrisoft.core.component.service.domain.Service;
import com.nutrisoft.userinterface.api.rest.service.generated.ServicesApi;
import com.nutrisoft.userinterface.api.rest.service.generated.model.ServiceResponse;
import com.nutrisoft.userinterface.api.rest.service.mapper.ServiceResponseMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

/**
 * Implementation of ServicesApi interface.
 *
 * <p>This is a PRIMARY/DRIVING ADAPTER that implements the auto-generated ServicesApi
 * interface. It directly delegates to individual use case classes which orchestrate the domain
 * logic.
 *
 * <p>The ServicesApi interface is auto-generated from the OpenAPI specification
 * (service-api-v1.yaml) using the openapi-generator-maven-plugin.
 *
 * <p>Strategy: API-First - The contract (YAML) drives the implementation.
 *
 * <p>All conversions between layers are delegated to specialized mapper classes.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class ServiceController implements ServicesApi {

  private final ListAllServicesUseCase listAllServicesUseCase;
  private final ServiceResponseMapper serviceResponseMapper;

  @Override
  public ResponseEntity<List<ServiceResponse>> listServices() {
    log.info("REST: Listing all services");

    List<Service> services = listAllServicesUseCase.execute();

    List<ServiceResponse> apiResponses =
        services.stream().map(serviceResponseMapper::toResponse).toList();

    return ResponseEntity.ok(apiResponses);
  }
}

