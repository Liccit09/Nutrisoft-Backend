# 🚀 Instrucciones de Instalación - Backend Nutrisoft

## 📋 Requisitos Iniciales

### Opción 1: Instalación Local
- **Java 21** (JDK)
  - Descarga desde: https://www.oracle.com/java/technologies/downloads/
  - Verifica: `java -version`

- **Maven 3.8+**
  - Descarga desde: https://maven.apache.org/download.cgi
  - Verifica: `mvn -version`

- **PostgreSQL 16+**
  - Descarga desde: https://www.postgresql.org/download/
  - Verifica: `psql --version`

### Opción 2: Docker (Recomendado)
- **Docker Desktop**
  - Descarga desde: https://www.docker.com/products/docker-desktop
  - Verifica: `docker --version` y `docker-compose --version`

---

## 🐳 Instalación Rápida con Docker

### Paso 1: Levanta la Base de Datos (PostgreSQL)

```bash
# Ve al directorio del backend
cd Nutrisoft-Backend

# Levanta PostgreSQL con Docker
docker-compose up -d

# Verifica que está corriendo
docker-compose ps
```

**Output esperado:**
```
NAME                COMMAND                STATUS          PORTS
nutrisoft-postgres  "docker-entrypoint.s…" Up X seconds    0.0.0.0:5432->5432/tcp
```

### Paso 2: Compila el Backend

```bash
# Descarga dependencias y compila
mvn clean install

# O solo compila (más rápido)
mvn compile
```

### Paso 3: Ejecuta el Servidor Spring Boot

```bash
# Opción A: Usando Maven
mvn spring-boot:run

# Opción B: Usando JAR compilado
java -jar target/nutrisoft-backend-1.0.0-SNAPSHOT.jar
```

**Servidor estará en:** `http://localhost:8080`

### ✅ Verificación

```bash
# Comprueba que está corriendo
curl http://localhost:8080/actuator/health

# Debería responder:
# {"status":"UP"}

# Ver Swagger UI
# http://localhost:8080/swagger-ui.html
```

---

## 💻 Instalación Local (Sin Docker)

### Paso 1: Crea la Base de Datos

```bash
# Conecta a PostgreSQL
psql -U postgres

# Crea la base de datos
CREATE DATABASE nutrisoft_db;
CREATE USER nutrisoft_user WITH PASSWORD 'nutrisoft_password';
ALTER ROLE nutrisoft_user SET client_encoding TO 'utf8';
ALTER ROLE nutrisoft_user SET default_transaction_isolation TO 'read committed';
ALTER ROLE nutrisoft_user SET default_transaction_deferrable TO on;
ALTER ROLE nutrisoft_user SET default_transaction_read_only TO off;
ALTER USER nutrisoft_user CREATEDB;
GRANT ALL PRIVILEGES ON DATABASE nutrisoft_db TO nutrisoft_user;

# Salir
\q
```

### Paso 2: Inicializa el Schema

```bash
# Conecta a la BD
psql -U nutrisoft_user -d nutrisoft_db -h localhost

# Ejecuta el script de inicialización
\i src/main/resources/db/migration/init-schema.sql

# Verifica
\dt
```

### Paso 3: Configura application.yml

```bash
# Edita src/main/resources/application.yml
# Asegúrate que la configuración de BD es correcta:
```

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/nutrisoft_db
    username: nutrisoft_user
    password: nutrisoft_password
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: validate  # O 'update' si necesitas que cree tablas
```

### Paso 4: Compila y Ejecuta

```bash
# Compila
mvn clean install

# Ejecuta
mvn spring-boot:run
```

---

## 🛠️ Comandos Docker (Backend)

```bash
# Levanta PostgreSQL
docker-compose up -d

# Ver logs
docker-compose logs -f postgres

# Detiene PostgreSQL
docker-compose down

# Reconstruye la imagen
docker-compose build --no-cache

# Ejecuta comando en el contenedor
docker-compose exec postgres psql -U nutrisoft_user -d nutrisoft_db

# Borra datos y reinicia
docker-compose down -v
docker-compose up -d
```

---

## 🔑 Configuración de Variables de Entorno

### application.yml - Configuración Base

```yaml
spring:
  application:
    name: nutrisoft-backend

  datasource:
    url: jdbc:postgresql://localhost:5432/nutrisoft_db
    username: nutrisoft_user
    password: nutrisoft_password
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true

  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME:tu-email@gmail.com}
    password: ${MAIL_PASSWORD:tu-app-password}
    from: noreply@nutrisoft.com

server:
  port: 8080

# JWT Configuration
app:
  jwtSecret: ${JWT_SECRET:nutrisoft-secret-key-for-jwt-token-generation}
  jwtExpirationMs: 86400000  # 24 horas
  resetTokenExpirationHours: 24
  frontend:
    baseUrl: ${FRONTEND_BASE_URL:http://localhost:3000}
```

### Variables de Entorno para Producción

```bash
# Base de Datos
SPRING_DATASOURCE_URL=jdbc:postgresql://prod-db:5432/nutrisoft_db
SPRING_DATASOURCE_USERNAME=nutrisoft_user
SPRING_DATASOURCE_PASSWORD=strong-password-here

# JWT
JWT_SECRET=tu-clave-secreta-muy-larga-aqui

# Email
MAIL_USERNAME=tu-email-produccion@gmail.com
MAIL_PASSWORD=tu-app-password

# Frontend
FRONTEND_BASE_URL=https://tudominio.com
```

---

## 📦 Estructura del Backend

```
Nutrisoft-Backend/
├── src/
│   ├── main/
│   │   ├── java/com/nutrisoft/
│   │   │   ├── core/
│   │   │   │   ├── domain/          # Entidades
│   │   │   │   ├── service/         # Lógica de negocio
│   │   │   │   └── port/            # Interfaces
│   │   │   ├── infrastructure/
│   │   │   │   ├── config/          # Configuración
│   │   │   │   ├── persistence/     # Repositorios
│   │   │   │   ├── security/        # JWT, seguridad
│   │   │   │   └── adapter/         # Adaptadores
│   │   │   └── userinterface/
│   │   │       └── api/
│   │   │           ├── auth/        # Autenticación
│   │   │           └── rest/        # REST controllers
│   │   └── resources/
│   │       ├── application.yml      # Configuración
│   │       └── db/migration/        # Scripts SQL
│   └── test/                        # Tests
├── docker-compose.yml
├── pom.xml                          # Dependencias
├── README.md
└── INSTALACION.md (este archivo)
```

---

## 🚀 Comandos Maven Útiles

```bash
# Compila y descarga dependencias
mvn clean install

# Solo compila
mvn compile

# Ejecuta tests
mvn test

# Empaqueta JAR
mvn package

# Limpia directorio target
mvn clean

# Ejecuta aplicación
mvn spring-boot:run

# Genera JavaDoc
mvn javadoc:javadoc

# Análisis de código
mvn checkstyle:check
```

---

## 🔗 Verificación de Endpoints

### 1. Health Check
```bash
curl http://localhost:8080/actuator/health
```

**Response:**
```json
{"status":"UP"}
```

### 2. API Swagger
```
http://localhost:8080/swagger-ui.html
```

### 3. Endpoints de Autenticación

**Registro:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "usuario@ejemplo.com",
    "firstName": "Juan",
    "lastName": "Pérez",
    "password": "password123"
  }'
```

**Login:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "usuario@ejemplo.com",
    "password": "password123"
  }'
```

---

## 🚨 Solución de Problemas

### Error: "Connection refused" a PostgreSQL

**Causa:** La BD no está corriendo

**Solución:**
```bash
# Opción 1: Docker
docker-compose up -d

# Opción 2: Local
# Asegúrate que PostgreSQL está ejecutándose
# Windows: Services -> PostgreSQL
# Mac: brew services start postgresql
# Linux: sudo systemctl start postgresql
```

---

### Error: "Database "nutrisoft_db" does not exist"

**Causa:** No creaste la BD

**Solución:**
```bash
# Local
psql -U postgres
CREATE DATABASE nutrisoft_db;
GRANT ALL PRIVILEGES ON DATABASE nutrisoft_db TO nutrisoft_user;

# Docker
docker-compose up -d
# Ya se crea automáticamente
```

---

### Error: "Port 5432 already in use"

**Causa:** Otro servicio usa el puerto

**Solución:**
```bash
# Opción 1: Cambiar puerto en docker-compose.yml
ports:
  - "5433:5432"  # Local:Container

# Opción 2: Matar el proceso
# Windows
netstat -ano | findstr :5432
taskkill /PID <PID> /F

# Mac/Linux
lsof -i :5432
kill -9 <PID>
```

---

### Error: "Build failure" con Maven

**Solución:**
```bash
# Limpia e instala de nuevo
mvn clean install -U

# Si persiste, reconstruye
mvn clean install -DskipTests

# Verifica Java version
java -version  # Debe ser 21+
```

---

### Error: "Application failed to start" con autenticación

**Causa:** Variables JWT no configuradas correctamente

**Solución:**
```bash
# Asegúrate que en application.yml tienes:
app:
  jwtSecret: tu-clave-aqui
  jwtExpirationMs: 86400000
```

---

## 📊 Stack Completo (Full-Stack)

Para levantar toda la aplicación de una vez:

```bash
# Terminal 1: PostgreSQL
cd Nutrisoft-Backend
docker-compose up -d

# Terminal 2: Backend (Spring Boot)
cd Nutrisoft-Backend
mvn spring-boot:run

# Terminal 3: Frontend (React)
cd Nutrisoft_frontend/react
npm install
npm start

# O con Docker
docker-compose up
```

### URLs Disponibles
- **Frontend:** http://localhost:3000
- **Backend API:** http://localhost:8080/api/v1
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **PostgreSQL:** localhost:5432
- **Health Check:** http://localhost:8080/actuator/health

---

## 🔐 Seguridad en Desarrollo

### Configuración para Desarrollo Local

```yaml
app:
  jwtSecret: nutrisoft-dev-secret-key-change-in-production
  jwtExpirationMs: 86400000

spring:
  mail:
    host: smtp.gmail.com
    username: tu-email@gmail.com
    password: tu-app-password
```

### Configuración para Producción

```yaml
app:
  jwtSecret: ${JWT_SECRET}  # Variable de entorno con clave fuerte
  jwtExpirationMs: ${JWT_EXPIRATION_MS:86400000}

spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USER}
    password: ${DATABASE_PASSWORD}

  mail:
    host: ${MAIL_HOST}
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
```

---

## 📚 Documentación Relacionada

- [README.md](./README.md) - Overview del proyecto
- [DEVELOPER_GUIDE.md](./docs/DEVELOPER_GUIDE.md) - Guía para desarrolladores
- [ARCHITECTURE.md](./docs/architecture/ARCHITECTURE.md) - Arquitectura del sistema
- [API REST](./docs/api/appointments-api.rest) - Ejemplos de API

---

## ✨ Próximos Pasos

1. **Configura email real** para notificaciones
2. **Verifica BD** con script de inicialización
3. **Prueba endpoints** con Swagger UI o curl
4. **Conecta con frontend** para flujo completo
5. **Configura para producción** con variables de entorno

---

## 💡 Tips y Buenas Prácticas

### Desarrollo Rápido
```bash
# Si solo cambias código, no necesitas recompilar
mvn spring-boot:run

# Los cambios en application.yml requieren reinicio
# Presiona Ctrl+C y ejecuta nuevamente
```

### Testing
```bash
# Ejecuta tests
mvn test

# Tests de una clase específica
mvn test -Dtest=AuthenticationServiceTest

# Sin ejecutar tests (evita en CI/CD)
mvn install -DskipTests
```

### Debugging
```bash
# Con logs detallados
SPRING_PROFILES_ACTIVE=debug mvn spring-boot:run

# Conexión a base de datos
# Usa DBeaver o pgAdmin para ver datos en tiempo real
```

---

¡Backend listo para desarrollar! 🎉
