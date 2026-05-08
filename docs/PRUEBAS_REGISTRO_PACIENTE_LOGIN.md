# Guía de Pruebas: Registro de Paciente y Login

## Objetivo

Describir el flujo completo de pruebas para el registro de un paciente y su posterior inicio de sesión. Incluye:
- Consumo de APIs a través de Swagger
- Revisión de correo electrónico de notificación
- Verificación de datos en la base de datos

## Requisitos previos

1. El backend está ejecutándose localmente.
   - Por defecto el servidor usa `http://localhost:8080`
2. Swagger UI está disponible.
   - URL probable: `http://localhost:8080/swagger-ui.html`
3. La base de datos PostgreSQL está accesible.
4. Si se desea revisar correo electrónico, el SMTP debe estar configurado correctamente.

## Endpoints clave

### Registrar paciente
- Método: `POST`
- URL: `http://localhost:8080/api/v1/patients/register`
- Request body ejemplo:
```json
{
  "firstName": "Juan",
  "lastName": "García",
  "email": "juan.garcia@example.com",
  "phoneNumber": "+57 300 123 4567"
}
```

### Login
- Método: `POST`
- URL: `http://localhost:8080/api/v1/auth/login`
- Request body ejemplo:
```json
{
  "email": "juan.garcia@example.com",
  "password": "Contraseña123!"
}
```

> Nota: El flujo del proyecto sugiere que el registro de paciente crea la entidad del paciente y, de forma asíncrona, genera las credenciales y envía una notificación de bienvenida.

## Flujo de pruebas

### 1. Iniciar la aplicación

Ejecuta el backend con el script correspondiente:
- Windows: `./start-dev.bat`
- Linux/macOS: `./start-dev.sh`

Verifica que el servidor arranque sin errores y escuche en el puerto 8080.

### 2. Abrir Swagger UI

1. Abre tu navegador.
2. Navega a: `http://localhost:8080/swagger-ui.html`
3. Busca los endpoints:
   - `POST /api/v1/patients/register`
   - `POST /api/v1/auth/login`

### 3. Probar registro de paciente en Swagger

1. Abre el endpoint `POST /api/v1/patients/register`.
2. Haz click en "Try it out".
3. Pega el body de ejemplo.
4. Ejecuta la petición.

#### Resultados esperados
- Código HTTP: `201 Created`
- Response body contiene al menos:
  - `aggregateId` o `patientId`

### 4. Verificar la respuesta del registro

1. Confirma que el body devuelto tiene un ID de paciente.
2. Si el endpoint retorna errores, revisa:
   - `400 Bad Request` para datos inválidos
   - `409 Conflict` si el email ya existe

### 5. Revisar correo electrónico (Mailpit)

El backend usa Mailpit en el entorno de desarrollo cuando está levantado con `docker-compose`.

#### Cómo validar el correo mediante Mailpit

1. Abre Mailpit en el navegador:
   - `http://localhost:8025`
2. Busca el correo enviado al email usado en el registro.
3. Verifica que el mensaje tenga:
   - Asunto de bienvenida o registro
   - Email del paciente
   - Posible contraseña temporal o indicación de credenciales
4. Si no aparece el correo, revisa los logs del backend y si Mailpit está corriendo correctamente.

> Si no recibes correo, revisa los logs del backend para confirmar si el envío falló. El adaptador de email registra errores cuando no puede enviar el mensaje.

### 6. Comprobar en la base de datos

#### Tablas relevantes

- `patients`
- `credentials`

#### Qué verificar

1. En la tabla `patients` debe existir una fila con:
   - `email = 'juan.garcia@example.com'`
   - `first_name = 'Juan'`
   - `last_name = 'García'`
   - `phone_number = '+57 300 123 4567'`
2. En la tabla `credentials` debe existir una fila con:
   - `email = 'juan.garcia@example.com'`
   - `role = 'PATIENT'`
   - `aggregate_id` igual al ID del paciente registrado
   - `active = true`

#### Ejemplo de consulta SQL
```sql
SELECT id, first_name, last_name, email, phone_number, created_at
FROM patients
WHERE email = 'juan.garcia@example.com';

SELECT id, email, password_hash, role, aggregate_id, active, created_at
FROM credentials
WHERE email = 'juan.garcia@example.com';
```

> Nota: La contraseña en `credentials.password_hash` estará hasheada con BCrypt y no es legible. Para probar el login, utiliza la contraseña real enviada o generada por el flujo.

### 7. Realizar login por Swagger

1. Abre el endpoint `POST /api/v1/auth/login`.
2. Haz click en "Try it out".
3. Pega el body de login.
4. Ejecuta la petición.

#### Resultados esperados
- Código HTTP: `200 OK`
- Response body incluye un token JWT válido:
  - `token`
  - `type` = `Bearer`
  - `userId`
  - `email`

### 8. Validar el token JWT

1. Copia el valor de `token` de la respuesta.
2. Puedes usar jwt.io o una herramienta similar para inspeccionarlo.
3. Verifica que el `sub` o `userId` coincida con el paciente registrado.
4. El token debe tener fechas de expiración válidas.

### 9. Probar acceso autenticado a otro endpoint

Para asegurarte de que el login funcionó correctamente, utiliza el token en un endpoint protegido.

Ejemplo de prueba:
- `GET /api/v1/appointments/patient/{patientId}`
- Header: `Authorization: Bearer <token>`

Si la petición retorna `200 OK`, el login y la autorización funcionan.

## Casos de prueba recomendados

### Registro válido
- Registrar paciente con datos completos y válidos.
- Debe devolver `201 Created`.
- Debe crear filas en `patients` y `credentials`.

### Registro con email duplicado
- Usar un email ya registrado.
- Debe devolver `409 Conflict`.

### Registro con datos inválidos
- Email inválido.
- Teléfono faltante.
- Nombre o apellido vacíos.
- Debe devolver `400 Bad Request`.

### Login válido
- Iniciar sesión con email y contraseña correctos.
- Debe devolver `200 OK` y token JWT.

### Login inválido
- Contraseña incorrecta.
- Email no registrado.
- Debe devolver `401 Unauthorized` o `400 Bad Request` según la implementación.

## Flujo de trabajo completo sugerido

1. Iniciar backend.
2. Abrir Swagger UI en `http://localhost:8080/swagger-ui.html`.
3. Ejecutar `POST /api/v1/patients/register`.
4. Verificar respuesta `201 Created`.
5. Revisar correo electrónico de bienvenida.
6. Ejecutar consultas SQL en `patients` y `credentials`.
7. Ejecutar `POST /api/v1/auth/login`.
8. Revisar token JWT devuelto.
9. Probar un endpoint protegido usando `Authorization: Bearer <token>`.

## Verificación final

Al finalizar, confirma que:
- El paciente existe en `patients`.
- La credencial existe en `credentials`.
- El login retorna un JWT válido.
- El token permite acceder a rutas protegidas.
- Si se configuró email, el mensaje de bienvenida llegó al buzón.

## Notas adicionales

- Si el correo no llega, es normal si el SMTP no está configurado en el entorno local. En ese caso, la verificación principal debe centrarse en la base de datos y en el login.
- Mantén valores de prueba diferentes para evitar errores por email duplicado.
- Usa herramientas como Postman, REST Client o Swagger UI según prefieras.
