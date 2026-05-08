# 🚀 Nutrisoft Backend - Getting Started

## 📋 Prerequisitos

Antes de comenzar, asegúrate de tener instalado:

✅ **Java 17+**
```bash
java -version
```

✅ **Maven 3.8+**
```bash
mvn -version
```

✅ **Docker & Docker Compose**
```bash
docker --version
docker-compose --version
```

---

## ⚡ Inicio en 3 Pasos

### Paso 1️⃣: Clonar el Repositorio
```bash
cd C:\Git-Projects\Liccit\Nutrisoft-Backend
```

### Paso 2️⃣: Iniciar Contenedores Docker
```bash
# En Windows:
docker-compose up -d

# O en Linux/Mac:
docker-compose up -d
```

**Verifica que todo esté corriendo:**
```bash
docker-compose ps
```

Deberías ver:
- ✅ `nutrisoft-postgres` - Up
- ✅ `nutrisoft-mailpit` - Up

### Paso 3️⃣: Ejecutar la Aplicación
```bash
mvn spring-boot:run
```

✨ **¡La aplicación está lista!**

---

## 🌐 Acceso a Servicios

| Servicio | URL | Descripción |
|----------|-----|-------------|
| **API Backend** | http://localhost:8080/api | REST API principal |
| **Swagger UI** | http://localhost:8080/api/swagger-ui.html | Documentación interactiva |
| **Mailpit Web** | http://localhost:8025 | Captura de correos 📧 |
| **PostgreSQL** | localhost:5432 | Base de datos |

### Credenciales PostgreSQL
```
Host: localhost
Puerto: 5432
Usuario: nutrisoft_user
Contraseña: nutrisoft_password
Base de datos: nutrisoft_db
```

---

## 📧 Prueba el Flujo Completo

### 1. Registrar un Paciente

```bash
curl -X POST http://localhost:8080/api/v1/patients \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "juan@ejemplo.com",
    "phone": "5551234567",
    "dateOfBirth": "1990-01-15"
  }'
```

### 2. Ver el Correo Enviado

1. Abre http://localhost:8025 en tu navegador
2. Verás el correo de bienvenida capturado por Mailpit
3. Puedes ver:
   - ✉️ Remitente
   - 📍 Destinatario
   - 📄 Asunto y cuerpo del mensaje
   - 📎 Archivos adjuntos (si los hay)

### 3. Verifica en la Base de Datos

```bash
# Desde PowerShell/CMD
docker-compose exec postgres psql -U nutrisoft_user -d nutrisoft_db

# Dentro de psql:
SELECT * FROM patients;
SELECT * FROM credentials;
```

---

## 🛑 Detener la Aplicación

### Opción 1: Usar Script (Recomendado)
```bash
# Windows:
.\stop-dev.bat

# Linux/Mac:
./stop-dev.sh
```

### Opción 2: Comando Manual
```bash
# Detener sin eliminar datos:
docker-compose stop

# Detener y limpiar (elimina contenedores pero no datos):
docker-compose down

# Limpiar TODO incluida la base de datos:
docker-compose down -v
```

---

## 🔧 Configuración de Mailpit

Mailpit está preconfigurado en `application.yml` para desarrollo:

```yaml
mail:
  host: localhost
  port: 1025
  username:          # Vacío para Mailpit
  password:          # Vacío para Mailpit
  from: noreply@nutrisoft.com
  from-name: Nutrisoft
  properties:
    mail:
      smtp:
        auth: false
        starttls:
          enable: false
```

**Para cambiar a Gmail o SendGrid en producción:**
- Consulta [`docs/MAILPIT_SETUP.md`](./docs/MAILPIT_SETUP.md)

---

## 📚 Documentación Adicional

| Documento | Descripción |
|-----------|-------------|
| 📧 [`MAILPIT_SETUP.md`](./docs/MAILPIT_SETUP.md) | Guía completa de Mailpit |
| 🏗️ [`ARCHITECTURE.md`](./docs/architecture/ARCHITECTURE.md) | Arquitectura del sistema |
| 👨‍💻 [`DEVELOPER_GUIDE.md`](./docs/DEVELOPER_GUIDE.md) | Guía para desarrolladores |
| ⚡ [`QUICK_REFERENCE.md`](./docs/QUICK_REFERENCE.md) | Comandos útiles y trucos |

---

## 🐛 Solución de Problemas

### ❌ "Puerto 1025 ya está en uso"
```bash
# Ver qué proceso usa el puerto
netstat -tulpn | grep 1025

# O reiniciar Docker
docker-compose restart mailpit
```

### ❌ PostgreSQL no responde
```bash
# Ver logs de PostgreSQL
docker-compose logs postgres

# Reiniciar PostgreSQL
docker-compose restart postgres

# Esperar 10 segundos
```

### ❌ La aplicación no levanta
```bash
# Verificar que PostgreSQL esté listo
docker-compose ps

# Esperar a que PostgreSQL esté "Up"
# Luego ejecutar nuevamente:
mvn spring-boot:run
```

### ❌ No veo correos en Mailpit
```bash
# Verificar configuración en application.yml:
# - mail.host: localhost
# - mail.port: 1025

# Reiniciar Mailpit
docker-compose restart mailpit

# Verificar logs
docker-compose logs mailpit
```

---

## 💡 Tips Útiles

### 1️⃣ Ejecutar con Script (Todo en Uno)
```bash
# Windows:
.\start-dev.bat

# Linux/Mac:
./start-dev.sh
```

### 2️⃣ Ver Logs en Tiempo Real
```bash
docker-compose logs -f mailpit
docker-compose logs -f postgres
```

### 3️⃣ Limpiar Base de Datos (Mantener Servidor)
```bash
# Conectarse a psql
docker-compose exec postgres psql -U nutrisoft_user -d nutrisoft_db

# Dentro de psql:
TRUNCATE TABLE credentials;
TRUNCATE TABLE patients;
\q  # Salir
```

### 4️⃣ Ejecutar Tests
```bash
# Todos los tests
mvn test

# Test específico
mvn test -Dtest=PatientRepositoryTest

# Ver cobertura
mvn test jacoco:report
```

---

## 🎯 Próximos Pasos

1. 📖 Lee [`DEVELOPER_GUIDE.md`](./docs/DEVELOPER_GUIDE.md) para entender la arquitectura
2. 🏗️ Revisa [`ARCHITECTURE.md`](./docs/architecture/ARCHITECTURE.md) para el diseño del sistema
3. 🔌 Consulta la API en Swagger: http://localhost:8080/api/swagger-ui.html
4. 📧 Prueba el flujo completo registrando un paciente

---

## 🆘 ¿Necesitas Ayuda?

- 📖 Consulta la [documentación completa](./docs/)
- 🔍 Revisa los logs: `docker-compose logs`
- 💬 Abre un issue en el repositorio

---

**¡Listo para desarrollar! 🚀**

Last Updated: May 3, 2026

