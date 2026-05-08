# Configuración de Mailpit para Pruebas de Correo Electrónico

## Resumen

Mailpit es un servidor SMTP simple y fácil de usar para desarrollo y pruebas. Captura todos los correos electrónicos enviados y proporciona una interfaz web intuitiva para visualizarlos sin necesidad de configurar cuentas de correo reales.

## Requisitos

- Docker y Docker Compose instalados
- Puerto 1025 disponible (SMTP)
- Puerto 8025 disponible (Web UI)

## Instalación y Uso

### 1. Inicia los contenedores de Docker

```bash
docker-compose up -d
```

Esto iniciará:
- **PostgreSQL** en puerto `5432` (base de datos)
- **Mailpit** en puerto `1025` (servidor SMTP) y `8025` (interfaz web)

### 2. Verifica que los servicios estén corriendo

```bash
docker-compose ps
```

Deberías ver tres servicios con estado `Up`:
- `nutrisoft-postgres`
- `nutrisoft-mailpit`

### 3. Inicia la aplicación Spring Boot

```bash
mvn spring-boot:run
```

La aplicación usará automáticamente Mailpit para enviar correos (configurado en `application.yml`).

### 4. Accede a la interfaz web de Mailpit

Abre tu navegador y ve a:

```
http://localhost:8025
```

Aquí verás:
- ✉️ Todos los correos capturados
- 📋 Detalles de cada mensaje (remitente, destinatario, asunto, cuerpo)
- 📎 Archivos adjuntos (si los hay)
- 🔍 Búsqueda y filtrado de mensajes

## Flujo de Prueba Completo

### Registrar un Paciente

1. Envía una solicitud POST a: `POST /api/v1/patients`
2. Proporciona los datos del paciente (nombre, email, etc.)
3. La aplicación ejecutará el flujo completo:
   - ✅ Paciente guardado en `patients`
   - ✅ Credencial creada en `credentials`
   - ✅ Correo de bienvenida enviado a través de Mailpit

### Visualizar el Correo de Bienvenida

1. Abre http://localhost:8025
2. Verás el correo de bienvenida en la bandeja de entrada de Mailpit
3. Haz clic para ver el contenido completo

## Configuración Actual

### `application.yml` (Desarrollo - Mailpit)

```yaml
mail:
  host: localhost
  port: 1025
  username:         # Vacío para Mailpit
  password:         # Vacío para Mailpit
  from: noreply@nutrisoft.com
  from-name: Nutrisoft
  properties:
    mail:
      smtp:
        auth: false
        starttls:
          enable: false
          required: false
```

### Variables de Entorno

Si necesitas cambiar los valores predeterminados, usa variables de entorno:

```bash
# Usar Mailpit
export MAIL_HOST=localhost
export MAIL_PORT=1025
export MAIL_USERNAME=
export MAIL_PASSWORD=
export MAIL_FROM=noreply@nutrisoft.com
export MAIL_FROM_NAME=Nutrisoft
export MAIL_SMTP_AUTH=false
export MAIL_STARTTLS_ENABLE=false
export MAIL_STARTTLS_REQUIRED=false

# Luego ejecuta la aplicación
mvn spring-boot:run
```

## Cambiar a un Servidor SMTP Real (Producción)

Cuando estés listo para producción, reemplaza con un servidor SMTP real:

### Ejemplo con Gmail

```bash
export MAIL_HOST=smtp.gmail.com
export MAIL_PORT=587
export MAIL_USERNAME=tu-email@gmail.com
export MAIL_PASSWORD=tu-app-password
export MAIL_FROM=tu-email@gmail.com
export MAIL_FROM_NAME="Tu Nombre"
export MAIL_SMTP_AUTH=true
export MAIL_STARTTLS_ENABLE=true
export MAIL_STARTTLS_REQUIRED=true
```

### Ejemplo con SendGrid

```bash
export MAIL_HOST=smtp.sendgrid.net
export MAIL_PORT=587
export MAIL_USERNAME=apikey
export MAIL_PASSWORD=SG.XXXXXX...
export MAIL_FROM=noreply@tudominio.com
export MAIL_FROM_NAME=Nutrisoft
export MAIL_SMTP_AUTH=true
export MAIL_STARTTLS_ENABLE=true
export MAIL_STARTTLS_REQUIRED=true
```

## Detener los Contenedores

```bash
docker-compose down
```

Para detener y eliminar también los volúmenes (base de datos):

```bash
docker-compose down -v
```

## Solución de Problemas

### "Puerto 1025 ya está en uso"

Si el puerto ya está ocupado:

```bash
# Ver qué proceso usa el puerto
netstat -tulpn | grep 1025

# O cambiar el puerto en docker-compose.yml
# Modifica "1025:1025" a "9025:1025" (usa 9025 en tu aplicación)
```

### Los correos no aparecen en Mailpit

1. Verifica que `MAIL_HOST=localhost` y `MAIL_PORT=1025` en tu configuración
2. Revisa los logs de la aplicación: `mvn spring-boot:run`
3. Verifica que Mailpit esté corriendo: `docker-compose ps`
4. Reinicia los contenedores: `docker-compose restart mailpit`

### Mailpit web UI no carga

1. Verifica que el puerto 8025 sea accesible: `curl http://localhost:8025`
2. Revisa los logs de Mailpit: `docker-compose logs mailpit`
3. Reinicia Mailpit: `docker-compose restart mailpit`

## Características Útiles de Mailpit

- **Búsqueda avanzada**: Busca por remitente, destinatario, asunto, etc.
- **Vista HTML/Texto**: Alterna entre la vista HTML y texto plano
- **Descarga de archivos**: Descarga mensajes en formato `.eml`
- **API REST**: Accede a los correos programáticamente en `http://localhost:8025/api`
- **Limpieza automática**: Los correos se borran cuando reinicia Mailpit

## Referencias

- 📚 [Documentación oficial de Mailpit](https://mailpit.axllent.org/)
- 🐳 [Imagen Docker de Mailpit](https://hub.docker.com/r/axllent/mailpit)

