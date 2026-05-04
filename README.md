# 🥗 Nutrisoft - Diseño del Sistema

## 📘 Introducción
Nutrisoft es un sistema de gestión para consultorios de nutrición diseñado para optimizar el proceso de agendamiento de citas, administración de pacientes y seguimiento nutricional. El sistema permite a los usuarios agendar citas de manera virtual, gestionar su información personal y recibir confirmaciones y recordatorios automáticos. Asimismo, facilita al profesional de nutrición la organización de su agenda, el control de sus pacientes y la administración de sus servicios.

Este repositorio contiene la implementación del backend basada en arquitectura hexagonal y domain-driven design (DDD).

---

## 🚀 Inicio Rápido

### Requisitos Previos
- **Java 17+** instalado
- **Maven 3.8+** instalado
- **Docker y Docker Compose** instalados
- **PostgreSQL 16** (incluido en Docker)
- **Mailpit** para pruebas de correos (incluido en Docker)

### Pasos para Iniciar

#### 1. En Windows (PowerShell):
```powershell
# Iniciar contenedores y aplicación
.\start-dev.bat

# O en PowerShell específicamente:
docker-compose up -d
mvn spring-boot:run
```

#### 2. En Linux/Mac:
```bash
# Dar permisos de ejecución
chmod +x start-dev.sh stop-dev.sh

# Iniciar contenedores y aplicación
./start-dev.sh
```

#### 3. Acceso a la Aplicación
- **API Backend**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **Mailpit (Correos de Prueba)**: http://localhost:8025
- **Base de Datos**: localhost:5432

### Detener la Aplicación
```bash
# Windows
.\stop-dev.bat

# Linux/Mac
./stop-dev.sh

# O manualmente:
docker-compose down
```

---

## 🎯 Objetivo
Centralizar los artefactos de diseño del sistema Nutrisoft, con el fin de documentar la arquitectura, funcionalidades y estructura del proyecto antes de su implementación, facilitando su desarrollo, mantenimiento y pruebas.

---

## 🧩 Alcance del Sistema
El sistema Nutrisoft permitirá:
- Agendamiento de citas virtuales
- Gestión de pacientes
- Confirmación automática de citas
- Recordatorios por correo y mensajería
- Administración de servicios nutricionales
- Consulta de historial de citas
- Portal del paciente

---

## 📂 Contenido del Repositorio
Este repositorio incluye:
- Diagramas de casos de uso
- Diagramas de clases
- Diagramas de secuencia
- Requerimientos funcionales
- Requerimientos no funcionales
- Arquitectura del sistema
- Prototipos o wireframes

---

## 🗂️ Estructura del Proyecto

nutrisoft-design/
│
├── docs/
│ ├── requerimientos/
│ ├── casos-de-uso/
│ ├── diagramas-clases/
│ ├── diagramas-secuencia/
│ └── arquitectura/
│
├── imagenes/
│ ├── casos-uso/
│ ├── clases/
│ └── arquitectura/
│
└── prototipos/


---

## 🛠️ Stack Tecnológico

### Backend
- **Java 17+**: Lenguaje de programación
- **Spring Boot 3.x**: Framework web
- **Spring Data JPA**: ORM para acceso a base de datos
- **Spring Event System**: Publicación/suscripción de eventos
- **PostgreSQL 16**: Base de datos relacional
- **MapStruct**: Mapeo de objetos (DTO ↔ Entity)
- **Lombok**: Reducción de boilerplate
- **SpringDoc OpenAPI**: Documentación automática con Swagger

### DevOps & Testing
- **Docker**: Containerización
- **Docker Compose**: Orquestación de contenedores
- **Mailpit**: Captura de correos para desarrollo y testing
- **Maven 3.8+**: Gestor de dependencias y builds

### Arquitectura
- **Hexagonal Architecture (Ports & Adapters)**: Separación de capas
- **Domain-Driven Design (DDD)**: Modelado del dominio
- **Event-Driven Architecture**: Flujos asíncronos con eventos de dominio
- **Repository Pattern**: Abstracción de acceso a datos

---

## 📚 Documentación
- [Setup de Mailpit](docs/MAILPIT_SETUP.md) - Configuración para pruebas de correo
- [Guía del Desarrollador](docs/DEVELOPER_GUIDE.md) - Arquitectura y convenciones
- [Cambios en la API](docs/API_CHANGES_SUMMARY.md) - Historial de cambios

## 👩‍💻 Autora
**Liccit Moncada**

---

## 📌 Estado del Proyecto
En fase de diseño y documentación.
