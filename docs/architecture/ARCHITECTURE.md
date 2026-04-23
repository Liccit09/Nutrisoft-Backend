# Nutrisoft Backend - Architecture and Implementation Guide

## Overview

Nutrisoft Backend is a Spring Boot 4 application implementing an appointment scheduling system for nutrition clinics. The architecture follows:

- **Hexagonal Architecture (Ports & Adapters)** for clear separation of concerns
- **Domain-Driven Design (DDD)** with Aggregates, Entities, and Value Objects
- **Package by Component** structure for explicit and scalable domain organization
- **Single Bounded Context** (Appointment) with anti-corruption layers for external entities

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

#### Bounded Context: Appointment

The single bounded context handles all appointment-related operations.

**Aggregate Root: Appointment**
- Manages appointments with full lifecycle
- Enforces business invariants
- Publishes domain events

**Entities:**
- `Appointment` (Aggregate Root)
- `Patient` (within appointment context)
- `Professional` (within appointment context)
- `Service` (within appointment context)

**Value Objects:**
- `AppointmentStatus` - Immutable appointment state
- `TimeSlot` - Represents start/end time with validation
- `ContactInfo` - Email and phone information

**Repository:**
- `AppointmentRepository` - Domain port for persistence

#### Anti-Corruption Layer

External entities are accessed via secondary ports:
- `PatientPort` - Provides patient data
- `ProfessionalPort` - Provides professional data
- `ServicePort` - Provides service data

When these entities become separate Bounded Contexts, implementations can switch to message brokers or HTTP clients without domain layer changes.

### Package by Component Structure

```
com.nutrisoft/
├── appointment/                              # Bounded Context: Appointment
│   ├── domain/                              # Domain Layer (No dependencies on external libs except value objects)
│   │   ├── model/                           # Domain models
│   │   │   ├── Appointment.java            # Aggregate root
│   │   │   ├── Patient.java                # Entity
│   │   │   ├── Professional.java           # Entity
│   │   │   ├── Service.java                # Entity
│   │   │   ├── AppointmentStatus.java      # Value Object
│   │   │   ├── TimeSlot.java               # Value Object
│   │   │   └── ContactInfo.java            # Value Object
│   │   ├── repository/                      # Domain repository interfaces
│   │   │   └── AppointmentRepository.java
│   │   └── event/                           # Domain events
│   │
│   ├── application/                         # Application Layer
│   │   ├── service/                         # Use case implementations
│   │   │   └── AppointmentApplicationService.java
│   │   ├── dto/                             # Data Transfer Objects
│   │   │   ├── AppointmentCommandDto.java
│   │   │   └── AppointmentResponseDto.java
│   │   ├── port/                            # Application ports
│   │   │   ├── AppointmentUseCasePort.java # Input port (use cases)
│   │   │   ├── PatientPort.java            # Output port (external)
│   │   │   ├── ProfessionalPort.java       # Output port (external)
│   │   │   └── ServicePort.java            # Output port (external)
│   │   └── usecase/                         # Specific use cases (if needed)
│   │
│   ├── primary/                             # Primary Adapter (REST)
│   │   └── adapter/
│   │       └── api/
│   │           ├── AppointmentController.java
│   │           └── mapper/
│   │
│   └── secondary/                           # Secondary Adapter (Persistence)
│       └── adapter/
│           └── persistence/
│               ├── AppointmentPersistenceAdapter.java
│               ├── PatientPersistenceAdapter.java
│               ├── ProfessionalPersistenceAdapter.java
│               ├── ServicePersistenceAdapter.java
│               ├── entity/                  # JPA Entities
│               ├── mapper/                  # MapStruct mappers
│               └── repository/              # JPA Repository interfaces
│
└── shared/                                  # Shared kernel
    ├── domain/                              # Base domain classes
    │   ├── DomainEntity.java
    │   ├── AggregateRoot.java
    │   ├── ValueObject.java
    │   └── DomainEvent.java
    ├── application/
    └── infrastructure/
```

## Technology Stack

- **Java 21** - Latest LTS version
- **Spring Boot 3.3.0** - Web framework
- **Spring Data JPA** - ORM and persistence
- **PostgreSQL 16** - Database
- **MapStruct 1.5.5** - Object mapping
- **Lombok** - Boilerplate reduction
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

The current architecture supports future expansion:

### Patient Bounded Context (Future)

When Patient becomes a separate BC:
1. Create `com.nutrisoft.patient` package
2. Implement Patient model in its own domain
3. Replace `PatientPort` adapter with HTTP or message broker client
4. Use shared events/Saga pattern for inter-context communication

### Professional Bounded Context (Future)

When Professional becomes a separate BC:
1. Create `com.nutrisoft.professional` package
2. Implement Professional model in its own domain
3. Replace `ProfessionalPort` adapter with HTTP or message broker client
4. Coordinate changes with Appointment context

### Service Bounded Context (Future)

When Service becomes a separate BC:
1. Create `com.nutrisoft.service` package
2. Implement Service model in its own domain
3. Replace `ServicePort` adapter with HTTP or message broker client
4. Handle service availability and pricing updates

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

