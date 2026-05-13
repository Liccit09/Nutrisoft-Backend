# Nutrisoft Backend - Architecture and Implementation Guide

## Overview

Nutrisoft Backend is a Spring Boot 3.3.0 application implementing an appointment scheduling system for nutrition clinics. The architecture follows:

- **Hexagonal Architecture (Ports & Adapters)** for clear separation of concerns
- **Domain-Driven Design (DDD)** with Aggregates, Entities, and Value Objects
- **Package by Component** structure for explicit and scalable domain organization
- **Multiple Bounded Contexts**: Appointment (principal), Patient, Professional, Service, and Schedule
- **Event-Driven Architecture** with domain events and event publishing
- **Prepared for Microservices**: Can evolve to independent services when needed

## Architecture Principles

### Hexagonal Architecture

The application is organized into concentric layers:

```
┌─────────────────────────────────────────────────┐
│           External Systems (REST)               │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│    Primary Adapter (Controller)                 │
│    - REST API endpoints                         │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│    Application Layer (Use Cases)                │
│    - AppointmentApplicationService              │
│    - Orchestrates domain logic                  │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│    Domain Layer                                 │
│    - Aggregate Roots (Appointment)              │
│    - Entities (Patient, Professional, Service) │
│    - Value Objects (TimeSlot, Status, etc)     │
└──────────┬──────────────────────────┬──────────┘
           │                          │
           │ Domain Ports             │
           │ (Interfaces)             │
           │                          │
┌──────────▼──────────┐  ┌───────────▼────────────┐
│ Repository Ports   │  │ External Service Ports │
│ (AppointmentRepo)  │  │ (PatientPort, etc)     │
└──────────┬──────────┘  └───────────┬────────────┘
           │                          │
┌──────────▼──────────────────────────▼──────────┐
│    Secondary Adapters                          │
│    - JPA Repository Implementation             │
│    - Persistence Adapters                      │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│    External Infrastructure (PostgreSQL)        │
└─────────────────────────────────────────────────┘
```

### Domain-Driven Design

#### Bounded Contexts: Componentes Principales

El proyecto implementa múltiples bounded contexts organizados por componentes:

**1. Appointment (Contexto Principal)**
- Gestiona citas con ciclo de vida completo
- Orquesta la interacción entre Paciente, Profesional y Servicio
- Aggregate Root: `Appointment`
- Value Objects: `AppointmentStatus`, `AppointmentMode`
- Eventos: `AppointmentCreatedEvent`, `AppointmentConfirmedEvent`, etc.

**2. Patient (Contexto de Pacientes)**
- Gestiona información de pacientes
- Entity: `Patient`
- Casos de uso: crear, actualizar, obtener pacientes

**3. Professional (Contexto de Profesionales)**
- Gestiona información de profesionales
- Entity: `Professional`
- Casos de uso: crear, actualizar, obtener profesionales

**4. Service (Contexto de Servicios)**
- Gestiona servicios/procedimientos disponibles
- Entity: `Service`
- Casos de uso: crear, actualizar, obtener servicios

**5. Schedule (Contexto de Disponibilidad)**
- Gestiona horarios de profesionales y disponibilidad
- Aggregate Root: `Schedule`
- Value Objects: `WorkingHours`, `BreakSlot`, `SpecialDay`
- Entidades: `AvailabilityCalculator` (Lógica de cálculo de disponibilidad)
- Casos de uso: obtener disponibilidad, gestionar horarios

### Package by Component Structure

La estructura del proyecto implementa una arquitectura de componentes/bounded contexts con separación clara entre capas:

```
com.nutrisoft/
│
├── core/                                    # Núcleo de la aplicación (Domain + Application)
│   │
│   ├── component/                           # Componentes por Bounded Context
│   │   │
│   │   ├── appointment/                     # Bounded Context: Appointment (Contexto Principal)
│   │   │   ├── domain/
│   │   │   │   ├── Appointment.java        # Aggregate Root
│   │   │   │   ├── AppointmentStatus.java  # Value Object
│   │   │   │   └── AppointmentMode.java    # Value Object
│   │   │   └── application/
│   │   │       ├── command/                # DTOs para comandos
│   │   │       ├── listener/               # Event listeners
│   │   │       ├── notification/           # Notificaciones
│   │   │       └── usecase/                # Casos de uso
│   │   │
│   │   ├── patient/                         # Bounded Context: Patient
│   │   │   ├── domain/
│   │   │   │   └── Patient.java            # Entity
│   │   │   └── application/
│   │   │       └── usecase/                # Casos de uso para pacientes
│   │   │
│   │   ├── professional/                    # Bounded Context: Professional
│   │   │   ├── domain/
│   │   │   │   └── Professional.java       # Entity
│   │   │   └── application/
│   │   │       └── usecase/                # Casos de uso para profesionales
│   │   │
│   │   ├── service/                         # Bounded Context: Service
│   │   │   ├── domain/
│   │   │   │   └── Service.java            # Entity
│   │   │   └── application/
│   │   │       └── usecase/                # Casos de uso para servicios
│   │   │
│   │   └── schedule/                        # Bounded Context: Schedule (Disponibilidad)
│   │       ├── domain/
│   │       │   ├── Schedule.java           # Entity
│   │       │   ├── WorkingHours.java       # Value Object
│   │       │   ├── BreakSlot.java          # Value Object
│   │       │   ├── SpecialDay.java         # Value Object
│   │       │   ├── DayOfWeek.java          # Enum
│   │       │   └── AvailabilityCalculator.java # Lógica de cálculo
│   │       └── application/
│   │           ├── dto/                    # DTOs para schedule
│   │           └── usecase/                # Casos de uso
│   │
│   ├── port/                                # Puertos (Interfaces de contrato)
│   │   ├── in/
│   │   │   ├── commandbus/                 # Input Port: Command Bus
│   │   │   └── querybus/                   # Input Port: Query Bus
│   │   └── out/
│   │       ├── persistence/                # Output Port: Persistencia
│   │       ├── eventbus/                   # Output Port: Event Bus
│   │       ├── notifications/              # Output Port: Notificaciones
│   │       └── auth/                       # Output Port: Autenticación
│   │
│   └── shared/                              # Shared Kernel (Código compartido)
│       ├── ddd/
│       │   ├── AggregateRoot.java          # Base para Aggregate Roots
│       │   ├── AggregateRootId.java        # Base para IDs de agregados
│       │   ├── DomainEntity.java           # Base para entidades
│       │   ├── DomainEvent.java            # Base para eventos de dominio
│       │   ├── Identifier.java             # Base para identificadores
│       │   └── ValueObject.java            # Base para value objects
│       ├── component/                       # Componentes compartidos
│       └── mapper/                          # Mappers compartidos
│
├── infrastructure/                          # Adaptadores Secundarios (Implementaciones)
│   │
│   ├── persistence/
│   │   └── jpa/                            # Implementación JPA/Hibernate
│   │       ├── appointment/                # Entities JPA para Appointment
│   │       ├── auth/                       # Entities JPA para Auth
│   │       ├── patient/                    # Entities JPA para Patient
│   │       ├── professional/               # Entities JPA para Professional
│   │       ├── schedule/                   # Entities JPA para Schedule
│   │       └── service/                    # Entities JPA para Service
│   │
│   ├── auth/
│   │   └── BCryptPasswordHasher.java       # Implementación de hash de contraseñas
│   │
│   ├── event/
│   │   └── SpringEventBusAdapter.java      # Implementación del Event Bus con Spring
│   │
│   └── notification/
│       ├── adapter/                        # Adaptadores de notificación
│       ├── service/                        # Servicios de notificación
│       └── template/                       # Plantillas de notificación
│
└── userinterface/                           # Adaptadores Primarios (Entry Points)
    └── api/
        └── rest/                           # REST API
            ├── appointment/                # REST Controllers para Appointments
            ├── auth/                       # REST Controllers para Autenticación
            ├── availability/               # REST Controllers para Disponibilidad
            ├── patient/                    # REST Controllers para Patients
            ├── professional/               # REST Controllers para Professionals
            ├── service/                    # REST Controllers para Services
            ├── config/                     # Configuración de REST
            └── exception/                  # Manejo de excepciones REST
```

## Puertos y Adaptadores (Hexagonal Architecture)

### Input Ports (Puertos de Entrada)
- **CommandBus** (`com.nutrisoft.core.port.in.commandbus/`) - Orquesta comandos de aplicación
- **QueryBus** (`com.nutrisoft.core.port.in.querybus/`) - Orquesta consultas de aplicación
- **REST Controllers** (`com.nutrisoft.userinterface.api.rest/`) - Adaptadores primarios HTTP

### Output Ports (Puertos de Salida)
- **PersistencePort** (`com.nutrisoft.core.port.out.persistence/`) - Contrato para acceso a datos
  - Implementación: `com.nutrisoft.infrastructure.persistence.jpa/`
  - Usa JPA/Hibernate con PostgreSQL
- **EventBusPort** (`com.nutrisoft.core.port.out.eventbus/`) - Publicación de eventos
  - Implementación: `com.nutrisoft.infrastructure.event.SpringEventBusAdapter`
- **NotificationsPort** (`com.nutrisoft.core.port.out.notifications/`) - Envío de notificaciones
  - Implementación: `com.nutrisoft.infrastructure.notification/`
  - Incluye: adapter, service, template
- **AuthPort** (`com.nutrisoft.core.port.out.auth/`) - Autenticación y autorización
  - Implementación: `com.nutrisoft.infrastructure.auth.BCryptPasswordHasher`

### Flujo de Datos (Request → Response)

```
HTTP Request
    ↓
REST Controller (userinterface/api/rest/)
    ↓
Use Case / Application Service (core/component/*/application/usecase/)
    ↓
Domain Model (core/component/*/domain/)
    ↓
Output Ports (core/port/out/)
    ↓
Adapters Implementation (infrastructure/*)
    ↓
External Systems (Database, Event Bus, Email, etc.)
```

## Technology Stack

- **Java 21** - Latest LTS version
- **Spring Boot 3.3.0** - Web framework
- **Spring Data JPA** - ORM and persistence
- **PostgreSQL 42.7.3** - Database driver
- **MapStruct 1.5.5.Final** - Object mapping
- **Lombok 1.18.30** - Boilerplate reduction
- **SpringDoc OpenAPI 2.3.0** - API documentation
- **Docker Compose** - Local development database

## Project Setup

### Prerequisites

- Java 21+
- Maven 3.8+
- Docker & Docker Compose

### Starting the Development Environment

#### 1. Start PostgreSQL with Docker Compose

```bash
cd nutrisoft-backend
docker-compose up -d
```

This starts a PostgreSQL container with:
- Database: `nutrisoft_db`
- User: `nutrisoft_user`
- Password: `nutrisoft_password`
- Port: `5432`

#### 2. Build the Project

```bash
mvn clean install
```

#### 3. Run the Application

```bash
mvn spring-boot:run
```

Or run the main class directly:
```bash
java -jar target/nutrisoft-backend-1.0.0-SNAPSHOT.jar
```

The application will be available at: `http://localhost:8080/api`

### Accessing the API Documentation

Once the application is running:

- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api/v1/api-docs
- **OpenAPI YAML**: http://localhost:8080/api/v1/api-docs.yaml

## Use Cases

The Appointment bounded context supports the following use cases:

### Create Appointment
- **Path**: `POST /v1/appointments`
- **Input**: Patient, Professional, Service, TimeSlot, Notes
- **Output**: Appointment details
- **Status**: SCHEDULED
- **Event**: AppointmentCreatedEvent

### Confirm Appointment
- **Path**: `POST /v1/appointments/{appointmentId}/confirm`
- **Precondition**: Status must be SCHEDULED
- **New Status**: CONFIRMED
- **Event**: AppointmentConfirmedEvent

### Update Appointment
- **Path**: `PUT /v1/appointments/{appointmentId}`
- **Input**: TimeSlot, Notes
- **Precondition**: Status must not be COMPLETED or NO_SHOW
- **Event**: AppointmentUpdatedEvent

### Cancel Appointment
- **Path**: `POST /v1/appointments/{appointmentId}/cancel`
- **Precondition**: Status must be SCHEDULED or CONFIRMED
- **New Status**: CANCELLED
- **Event**: AppointmentCancelledEvent

### Complete Appointment
- **Path**: `POST /v1/appointments/{appointmentId}/complete`
- **New Status**: COMPLETED
- **Event**: AppointmentCompletedEvent

### Mark No-Show
- **Path**: `POST /v1/appointments/{appointmentId}/no-show`
- **New Status**: NO_SHOW
- **Event**: AppointmentNoShowEvent

### Get Appointment
- **Path**: `GET /v1/appointments/{appointmentId}`
- **Output**: Appointment details with all nested information

### List Appointments
- **Path**: `GET /v1/appointments`
- **Output**: All appointments

### List by Patient
- **Path**: `GET /v1/appointments/patient/{patientId}`
- **Output**: Appointments for specific patient

### List by Professional
- **Path**: `GET /v1/appointments/professional/{professionalId}`
- **Output**: Appointments for specific professional

### Delete Appointment
- **Path**: `DELETE /v1/appointments/{appointmentId}`
- **Precondition**: Status must not be COMPLETED or NO_SHOW

## Domain Events

The appointment aggregate emits the following domain events:

1. **AppointmentCreatedEvent** - When an appointment is created
2. **AppointmentConfirmedEvent** - When an appointment is confirmed
3. **AppointmentUpdatedEvent** - When an appointment is updated
4. **AppointmentCancelledEvent** - When an appointment is cancelled
5. **AppointmentCompletedEvent** - When an appointment is marked as completed
6. **AppointmentNoShowEvent** - When an appointment is marked as no-show

## Appointment Status Lifecycle

```
                    ┌──────────────┐
                    │  SCHEDULED   │
                    └──────┬───────┘
                           │
            ┌──────────────┼──────────────┐
            │              │              │
         CONFIRM        UPDATE         CANCEL
            │              │              │
            ▼              ▼              ▼
       ┌─────────┐   ┌─────────┐   ┌──────────┐
       │CONFIRMED│   │SCHEDULED│   │CANCELLED │
       └────┬────┘   └─────────┘   └──────────┘
            │              │
         COMPLETE       CANCEL
            │              │
            ▼              ▼
       ┌─────────┐   ┌──────────┐
       │COMPLETED│   │CANCELLED │
       └─────────┘   └──────────┘

NO_SHOW: Can transition to NO_SHOW from SCHEDULED or CONFIRMED
```

## Scalability & Future Bounded Contexts

La arquitectura actual ya está preparada para evolucionar a múltiples Bounded Contexts independientes. Actualmente, Patient, Professional, Service y Schedule están co-localizados en el mismo proyecto pero en componentes separados. Cuando sea necesario escalar:

### Patient Bounded Context (Futuro)

Cuando Patient se separe en un BC independiente:
1. Crear `com.nutrisoft.patient` en un proyecto separado
2. Implementar Patient model en su propio dominio
3. Exponer APIs REST para patient management
4. El Appointment context consumirá datos de Patient BC via HTTP o message broker
5. Usar patrones de Saga para coordinar cambios entre contextos

### Professional Bounded Context (Futuro)

Cuando Professional se separe en un BC independiente:
1. Crear `com.nutrisoft.professional` en un proyecto separado
2. Implementar Professional model en su propio dominio
3. Exponer APIs REST para professional management
4. El Appointment context consumirá datos de Professional BC via HTTP o message broker
5. Coordinar cambios en disponibilidad con Schedule context

### Service Bounded Context (Futuro)

Cuando Service se separe en un BC independiente:
1. Crear `com.nutrisoft.service` en un proyecto separado
2. Implementar Service model en su propio dominio
3. Exponer APIs REST para service management
4. El Appointment context consumirá datos de Service BC via HTTP o message broker
5. Gestionar actualizaciones de precios y disponibilidad de servicios

### Schedule Bounded Context (Futuro)

Cuando Schedule se separe en un BC independiente:
1. Crear `com.nutrisoft.schedule` en un proyecto separado
2. Implementar Schedule model en su propio dominio
3. Exponer APIs REST para schedule management
4. Mantener sincronización de disponibilidad con Professional context
5. Usar event sourcing para auditar cambios en horarios

## Database Schema

### Patients Table
```sql
CREATE TABLE patients (
    id UUID PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    medical_history TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

### Professionals Table
```sql
CREATE TABLE professionals (
    id UUID PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    specialization VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

### Services Table
```sql
CREATE TABLE services (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    duration_in_minutes INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

### Appointments Table (Aggregate Root)
```sql
CREATE TABLE appointments (
    id UUID PRIMARY KEY,
    patient_id UUID NOT NULL REFERENCES patients(id),
    professional_id UUID NOT NULL REFERENCES professionals(id),
    service_id UUID NOT NULL REFERENCES services(id),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

## MapStruct Mapping Strategy

The application uses MapStruct for efficient object transformation:

### DTO ↔ Domain
- **AppointmentResponseMapper**: Maps Appointment domain → AppointmentResponseDto
- Flattens nested objects for API responses

### Domain ↔ Persistence
- **AppointmentPersistenceMapper**: Aggregate ↔ Entity
- **PatientPersistenceMapper**: Patient + ContactInfo ↔ PatientEntity
- **ProfessionalPersistenceMapper**: Professional + ContactInfo ↔ ProfessionalEntity
- **ServicePersistenceMapper**: Service ↔ ServiceEntity

### Configuration
- Component model: `spring` (Spring dependency injection)
- Auto-configuration for all mappers
- Custom methods for complex mappings (ContactInfo extraction, etc.)

## References

- [Explicit Architecture by Herberto Graça](https://herbertograca.com/2017/11/16/explicit-architecture-01-ddd-hexagonal-onion-clean-cqrs-how-i-put-it-all-together/)
- [Reflecting Architecture in Code by Herberto Graça](https://herbertograca.com/2019/06/05/reflecting-architecture-and-domain-in-code/)
- [Domain-Driven Design by Eric Evans](https://www.domainlanguage.com/ddd/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [MapStruct Documentation](https://mapstruct.org/)

## License

Copyright © 2026 Nutrisoft. All rights reserved.

