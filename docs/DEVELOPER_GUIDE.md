# Developer Quick Start Guide

## Project Overview

Nutrisoft Backend is a Spring Boot 4 application for appointment scheduling in nutrition clinics, built with Hexagonal Architecture and Domain-Driven Design.

**Current Status**: ✅ Ready for development

## Getting Started (5 minutes)

### 1. Prerequisites Check
```bash
java -version          # Should be 21+
mvn -version          # Should be 3.8+
docker --version      # For PostgreSQL
```

### 2. Start Development Database
```bash
cd nutrisoft-backend
docker-compose up -d
```

**Verify it's running**:
```bash
docker ps | grep nutrisoft-postgres
```

### 3. Build the Project
```bash
mvn clean install
```

### 4. Run the Application
```bash
mvn spring-boot:run
```

**Success indicators**:
- Application starts on port 8080
- No errors in console
- You see "Started NutrisoftBackendApplication"

### 5. Test the API
```bash
# Open in browser:
http://localhost:8080/api/swagger-ui.html
```

You should see the Swagger UI with all API endpoints documented.

---

## Project Structure at a Glance

```
nutrisoft-backend/
├── src/main/java/com/nutrisoft/
│   ├── appointment/          ← Main Bounded Context
│   │   ├── domain/           ← Business logic (Appointment, Patient, etc)
│   │   ├── application/      ← Use cases (AppointmentApplicationService)
│   │   ├── primary/          ← REST Controller
│   │   └── secondary/        ← Database adapters
│   └── shared/               ← Shared infrastructure
│
├── src/main/resources/
│   ├── openapi/              ← API specification
│   ├── db/migration/         ← SQL scripts
│   └── application.yml       ← Configuration
│
├── pom.xml                   ← Dependencies
├── docker-compose.yml        ← PostgreSQL setup
└── ARCHITECTURE.md           ← Detailed architecture guide
```

---

## Key Commands

### Build
```bash
mvn clean install              # Full build
mvn clean compile              # Just compile
mvn clean verify               # Full verification
```

### Run
```bash
mvn spring-boot:run           # Run with Maven
java -jar target/*.jar        # Run JAR directly
```

### Database
```bash
# Start PostgreSQL
docker-compose up -d

# Stop PostgreSQL
docker-compose down

# View logs
docker-compose logs -f postgres

# Connect to database
docker-compose exec postgres psql -U nutrisoft_user -d nutrisoft_db
```

### Testing Endpoints (curl)

**Create Appointment**:
```bash
curl -X POST http://localhost:8080/api/v1/appointments \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": "550e8400-e29b-41d4-a716-446655440001",
    "professionalId": "650e8400-e29b-41d4-a716-446655440001",
    "serviceId": "750e8400-e29b-41d4-a716-446655440001",
    "startTime": "2026-05-15T10:00:00",
    "endTime": "2026-05-15T11:00:00",
    "notes": "Initial consultation"
  }'
```

**Get Appointment**:
```bash
curl http://localhost:8080/api/v1/appointments/850e8400-e29b-41d4-a716-446655440001
```

**List All Appointments**:
```bash
curl http://localhost:8080/api/v1/appointments
```

---

## Common Development Tasks

### Adding a New Endpoint

1. **Define in domain layer** (if needed):
   ```java
   // Update AppointmentUseCasePort interface
   AppointmentResponseDto someNewUseCase(UUID id);
   ```

2. **Implement in application layer**:
   ```java
   // AppointmentApplicationService.java
   @Override
   public AppointmentResponseDto someNewUseCase(UUID id) {
       // Implementation
   }
   ```

3. **Add REST controller method**:
   ```java
   // AppointmentController.java
   @GetMapping("/some-endpoint/{id}")
   public ResponseEntity<AppointmentResponseDto> someEndpoint(@PathVariable UUID id) {
       return ResponseEntity.ok(appointmentUseCasePort.someNewUseCase(id));
   }
   ```

4. **Update OpenAPI spec**:
   ```yaml
   # src/main/resources/openapi/appointments-api-v1.yaml
   /v1/appointments/some-endpoint/{id}:
     get:
       operationId: someEndpoint
       # ... rest of spec
   ```

### Adding a New Value Object

```java
// In: src/main/java/com/nutrisoft/appointment/domain/model/
@Getter
@ToString
@EqualsAndHashCode
public class MyValueObject extends ValueObject {
    private final String value;
    
    public MyValueObject(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Value cannot be empty");
        }
        this.value = value;
    }
    
    @Override
    public boolean equals(Object o) {
        // Implementation
    }
    
    @Override
    public int hashCode() {
        // Implementation
    }
}
```

### Adding a Query Method to Repository

1. **Add to domain interface**:
   ```java
   // AppointmentRepository.java
   List<Appointment> findByStatus(AppointmentStatus status);
   ```

2. **Add to JPA repository**:
   ```java
   // AppointmentJpaRepository.java
   List<AppointmentEntity> findByStatus(String status);
   ```

3. **Implement in adapter**:
   ```java
   // AppointmentPersistenceAdapter.java
   @Override
   public List<Appointment> findByStatus(AppointmentStatus status) {
       return appointmentJpaRepository.findByStatus(status.getValue())
           .stream()
           .map(this::reconstructAppointment)
           .toList();
   }
   ```

---

## Configuration

### Environment Variables
```bash
# Edit as needed
nano .env
```

### Application Properties
File: `src/main/resources/application.yml`

Key settings:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/nutrisoft_db
  jpa:
    hibernate:
      ddl-auto: validate   # Production setting
      
server:
  port: 8080
  servlet:
    context-path: /api
```

---

## Debugging

### Enable Debug Logging
```yaml
# application.yml
logging:
  level:
    com.nutrisoft: DEBUG
    org.springframework.web: DEBUG
    org.hibernate: DEBUG
```

### IDE Debugging
**IntelliJ IDEA**:
1. Set breakpoint (click line number)
2. Run → Debug 'NutrisoftBackendApplication'
3. Use debug console to step through

**VS Code**:
1. Install Extension Pack for Java
2. Set breakpoint
3. Run → Start Debugging
4. Select "Java" when prompted

### View SQL Queries
```yaml
# application.yml
spring:
  jpa:
    properties:
      hibernate:
        format_sql: true
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

---

## Database Inspection

### View Tables
```bash
docker-compose exec postgres psql -U nutrisoft_user -d nutrisoft_db

# In psql:
\dt                           # List all tables
\d appointments               # Describe appointments table
SELECT * FROM appointments;   # View appointments
```

### Insert Test Data
```bash
# Run seed script:
docker-compose exec postgres psql -U nutrisoft_user -d nutrisoft_db < seed-data.sql
```

---

## API Documentation

### Swagger UI
- **URL**: http://localhost:8080/api/swagger-ui.html
- **Interactive**: Try endpoints directly from browser
- **Export**: Download OpenAPI spec as JSON/YAML

### OpenAPI Spec
- **JSON**: http://localhost:8080/api/v1/api-docs
- **YAML**: http://localhost:8080/api/v1/api-docs.yaml
- **Source**: `src/main/resources/openapi/appointments-api.yaml`

---

## Troubleshooting

### Port 8080 Already in Use
```bash
# Find what's using port 8080
lsof -i :8080

# Kill the process (macOS/Linux)
kill -9 <PID>

# Or use different port
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

### Database Connection Error
```bash
# Verify PostgreSQL is running
docker ps | grep nutrisoft-postgres

# Check logs
docker-compose logs postgres

# Restart if needed
docker-compose restart postgres
```

### MapStruct Not Generating Mappers
```bash
# Clean build cache
mvn clean

# Force recompile
mvn compile

# Check target/generated-sources/annotations
```

### Tests Failing
```bash
# Run with verbose output
mvn test -e -X

# Run specific test
mvn test -Dtest=AppointmentApplicationServiceTest

# Skip tests during build
mvn clean install -DskipTests
```

---

## Architecture Layers Quick Reference

### Domain Layer (No Spring dependencies here!)
- **Location**: `appointment/domain/`
- **Contains**: Models, Value Objects, Aggregates, Repositories (interface only)
- **Examples**: `Appointment.java`, `AppointmentStatus.java`
- **Rule**: Pure business logic, framework-agnostic

### Application Layer
- **Location**: `appointment/application/`
- **Contains**: Use cases, DTOs, Ports
- **Examples**: `AppointmentApplicationService.java`, `AppointmentUseCasePort.java`
- **Rule**: Orchestrates domain logic, handles cross-cutting concerns

### Primary Adapter (REST)
- **Location**: `appointment/primary/adapter/api/`
- **Contains**: Controllers, Request/Response objects
- **Examples**: `AppointmentController.java`
- **Rule**: HTTP interface to application services

### Secondary Adapter (Persistence)
- **Location**: `appointment/secondary/adapter/persistence/`
- **Contains**: JPA entities, Repositories, Mappers
- **Examples**: `AppointmentEntity.java`, `AppointmentPersistenceAdapter.java`
- **Rule**: Database operations, JPA configuration

### Shared Kernel
- **Location**: `shared/`
- **Contains**: Base classes, utilities, cross-cutting concerns
- **Examples**: `DomainEntity.java`, `GlobalExceptionHandler.java`
- **Rule**: Common to all bounded contexts

---

## Next Steps for Development

1. ✅ **Architecture Setup** - Done! Project is scaffolded
2. ✅ **Core Domain** - Appointment aggregate defined
3. ✅ **API Contracts** - OpenAPI specification created
4. ✅ **Database Schema** - Tables and migrations ready
5. ⏳ **Integration Tests** - Add test coverage
6. ⏳ **Event Publishing** - Connect to message broker (future)
7. ⏳ **API Security** - Add authentication/authorization
8. ⏳ **Performance Tuning** - Optimize queries (future)

---

## Getting Help

- **Architecture Questions**: See `ARCHITECTURE.md`
- **Domain Questions**: See `docs/domain/BOUNDED_CONTEXTS.md`
- **Database Schema**: See `docs/domain/DATA_MODEL.md`
- **Code Issues**: Check `GlobalExceptionHandler.java` for error handling

---

## Good Practices

✅ **DO**:
- Keep domain logic in domain layer
- Use domain ports for external dependencies
- Write meaningful commit messages
- Add logging for important operations
- Handle exceptions at appropriate layers

❌ **DON'T**:
- Put business logic in controllers
- Use @Autowired in domain classes
- Catch exceptions silently
- Create tight coupling between layers
- Skip validation of inputs

---

**Last Updated**: April 2026  
**Ready for**: Active Development

