# 📋 Nutrisoft Backend - REST Client Test Files

## Descripción General

Este directorio contiene archivos `.rest` para probar todos los endpoints del Nutrisoft Backend API usando el plugin REST Client de VSCode.

**Plugin recomendado:** [REST Client](https://marketplace.visualstudio.com/items?itemName=humao.rest-client) por Huachao Mao

---

## 📁 Archivos Disponibles

### 1. `auth-api.rest`
**API:** Authentication API  
**Especificación:** `/src/main/resources/openapi/auth-api-v1.yaml`  
**Controller:** `AuthController` en `/api/v1/auth/*`

**Endpoints disponibles:**
- `POST /v1/auth/login` - Login universal para cualquier usuario

**Características:**
- ✅ Login de pacientes
- ✅ Login de profesionales
- ✅ Login de administradores
- ✅ Retorna JWT Bearer token

**Notas:**
- No requiere autenticación
- Retorna token para usar en otros endpoints

---

### 2. `patient-api.rest`
**API:** Patient API  
**Especificación:** `/src/main/resources/openapi/patient-api-v1.yaml`  
**Controller:** `PatientController` en `/api/v1/patients/*`

**Endpoints disponibles:**
- `POST /v1/patients/register` - Registrar nuevo paciente

**Características:**
- ✅ Registro con email, password, firstName, lastName
- ✅ Validaciones automáticas
- ✅ Retorna JWT Bearer token
- ✅ Ejemplos de errores (400, 409, 500)

**Ejemplos incluidos:**
- Registro exitoso
- Email duplicado (409)
- Email inválido (400)
- Password muy corto (400)
- Campo vacío (400)

**Notas:**
- No requiere autenticación (endpoint público)
- El email debe ser único
- Password mínimo 8 caracteres

---

### 3. `professional-api.rest`
**API:** Professional API  
**Especificación:** `/src/main/resources/openapi/professional-api-v1.yaml`  
**Controller:** `ProfessionalController` en `/api/v1/professionals/*`

**Endpoints disponibles:**
- `POST /v1/professionals/register` - Registrar nuevo profesional

**Características:**
- ✅ Registro con email, password, firstName, lastName, licenseNumber, specialization
- ✅ Validaciones automáticas de campos requeridos
- ✅ Retorna JWT Bearer token
- ✅ Ejemplos de errores (400, 409, 500)

**Ejemplos incluidos:**
- Registro exitoso
- Email duplicado (409)
- Email inválido (400)
- licenseNumber vacío (400)
- specialization vacía (400)
- Password muy corto (400)

**Notas:**
- No requiere autenticación (endpoint público)
- Todos los campos son requeridos
- licenseNumber debe ser único
- specialization es obligatorio

---

### 4. `appointments-api.rest`
**API:** Appointments API  
**Especificación:** `/src/main/resources/openapi/appointments-api-v1.yaml`  
**Controller:** `AppointmentController` en `/api/v1/appointments/*`

**Endpoints disponibles:**
- `GET /v1/appointments` - Listar todos los appointments
- `GET /v1/appointments/{appointmentId}` - Obtener appointment por ID
- `GET /v1/appointments/patient/{patientId}` - Listar appointments de un paciente
- `GET /v1/appointments/professional/{professionalId}` - Listar appointments de un profesional
- `POST /v1/appointments` - Crear appointment
- `POST /v1/appointments/{appointmentId}/confirm` - Confirmar appointment
- `POST /v1/appointments/{appointmentId}/complete` - Completar appointment
- `POST /v1/appointments/{appointmentId}/cancel` - Cancelar appointment
- `POST /v1/appointments/{appointmentId}/no-show` - Marcar como no-show
- `PUT /v1/appointments/{appointmentId}/reschedule` - Reagendar appointment
- `PUT /v1/appointments/{appointmentId}/virtual-link` - Registrar enlace virtual

**Características:**
- ✅ CRUD completo de appointments
- ✅ Cambios de estado
- ✅ Soporte para reuniones virtuales
- ✅ Requiere autenticación (Bearer token)

**Notas:**
- Todos los endpoints requieren Bearer token
- Primero registra usuarios en patient-api o professional-api
- Luego obtén token en auth-api
- Usa el token en Authorization header

---

## 🚀 Cómo Usar

### Opción 1: VSCode REST Client Plugin

1. **Instalar extensión:**
   - Abre VSCode
   - Ve a Extensions (Ctrl+Shift+X)
   - Busca "REST Client"
   - Instala la extensión por Huachao Mao

2. **Ejecutar peticiones:**
   - Abre un archivo `.rest`
   - Haz click en "Send Request" (encima de cada petición)
   - O presiona Ctrl+Alt+R

3. **Ver respuestas:**
   - La respuesta aparece en el panel derecho
   - Puedes ver headers, body, y status code

### Opción 2: cURL

```bash
curl -X POST http://localhost:8080/api/v1/patients/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "patient@example.com",
    "password": "SecurePassword123!",
    "firstName": "Juan",
    "lastName": "García"
  }'
```

### Opción 3: Postman

1. Importa los archivos `.rest` o crea colecciones manualmente
2. Configura variables de entorno
3. Ejecuta las peticiones desde Postman

### Opción 4: Swagger UI

```
http://localhost:8080/swagger-ui.html
```

---

## 📊 Flujo de Prueba Recomendado

### 1. Registrar Paciente
Usa `patient-api.rest`:
```
POST /v1/patients/register
```
Obtén el token de la respuesta.

### 2. Registrar Profesional
Usa `professional-api.rest`:
```
POST /v1/professionals/register
```
Obtén el token de la respuesta.

### 3. Login
Usa `auth-api.rest`:
```
POST /v1/auth/login
```
Para verificar que puedes autenticarte.

### 4. Crear Appointment
Usa `appointments-api.rest`:
```
POST /v1/appointments
```
Usa los IDs de paciente y profesional registrados.

---

## 🔑 Variables Importantes

```
@baseUrl = http://localhost:8080/api
@patientId = <UUID del paciente registrado>
@professionalId = <UUID del profesional registrado>
@appointmentId = <UUID del appointment>
@token = <JWT Bearer token obtenido en login>
```

---

## ✅ Checklist de Pruebas

- [ ] Registrar paciente exitosamente
- [ ] Registrar profesional exitosamente
- [ ] Intentar registrar con email duplicado (debe fallar con 409)
- [ ] Intentar registrar con email inválido (debe fallar con 400)
- [ ] Intentar registrar con password corto (debe fallar con 400)
- [ ] Login como paciente
- [ ] Login como profesional
- [ ] Crear appointment autenticado
- [ ] Listar appointments del paciente
- [ ] Listar appointments del profesional
- [ ] Confirmar appointment
- [ ] Reagendar appointment
- [ ] Cancelar appointment

---

## 🔐 Seguridad

⚠️ **Notas importantes:**
- Los tokens JWT incluidos son ejemplos (reemplaza con tokens válidos)
- No guardes tokens reales en archivos versionados
- Usa variables de entorno para datos sensibles
- Los endpoints de registro NO requieren autenticación
- Los endpoints de appointments SÍ requieren autenticación

---

## 📖 Referencias

- **OpenAPI Specs:** `/src/main/resources/openapi/`
- **Controladores:** `/src/main/java/com/nutrisoft/userinterface/api/rest/`
- **Documentación:** `/docs/`
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`

---

## 🆘 Troubleshooting

### "Connection refused"
- Verifica que el servidor esté corriendo: `./mvnw spring-boot:run`
- Verifica el puerto: por defecto es 8080

### "401 Unauthorized"
- El token puede estar expirado
- Obtén un nuevo token en auth-api.rest
- Reemplaza la variable @token

### "400 Bad Request"
- Verifica los datos enviados
- Revisa las validaciones en los archivos `.rest`
- Revisa los esquemas OpenAPI en `/src/main/resources/openapi/`

### "409 Conflict"
- El email ya existe
- Usa un email diferente para registrar

---

**¡Listo para comenzar a testear!** 🎉

