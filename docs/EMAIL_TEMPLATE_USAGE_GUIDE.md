# Guía Rápida: Sistema de Emails con Thymeleaf

## ¿Cómo Funciona?

El sistema de emails utiliza **Thymeleaf** para renderizar plantillas HTML dinámicas y las envía mediante el puerto de notificaciones.

## Cómo Agregar un Nuevo Email

### Paso 1: Crear la Plantilla Thymeleaf

Crear archivo en: `src/main/resources/templates/emails/tu-email.html`

Ejemplo básico:

```html
<!DOCTYPE html>
<html lang="es" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Mi Email</title>
    <style>
        /* CSS aquí */
    </style>
</head>
<body>
    <h1 th:text="${titulo}"></h1>
    <p th:text="${mensaje}"></p>
    <a th:href="${linkAccion}">Ir</a>
</body>
</html>
```

### Paso 2: Crear Método en EmailTemplateService

Editar: `infrastructure/notification/service/EmailTemplateService.java`

```java
public String renderPasswordResetEmailTemplate(String email, String resetLink) {
    Map<String, Object> variables = new HashMap<>();
    variables.put("email", email);
    variables.put("resetLink", resetLink);
    
    return renderTemplate("password-reset-email", variables);
}
```

### Paso 3: Crear el Caso de Uso

Crear archivo: `core/shared/component/auth/application/notification/SendPasswordResetEmailUseCase.java`

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class SendPasswordResetEmailUseCase {

  private final Notifications notifications;
  private final EmailTemplateService emailTemplateService;

  @Async
  public void execute(String email, String resetLink) {
    log.info("Sending password reset email to: {}", email);
    
    String subject = "Resetea tu contraseña en Nutrisoft";
    String htmlContent = emailTemplateService.renderPasswordResetEmailTemplate(email, resetLink);
    
    notifications.sendHtml(email, subject, htmlContent);
    log.info("Password reset email sent successfully to: {}", email);
  }
}
```

### Paso 4: Usar el Caso de Uso

Inyectar y llamar desde donde sea necesario:

```java
@RequiredArgsConstructor
public class MiComponente {
    
    private final SendPasswordResetEmailUseCase sendPasswordResetEmailUseCase;
    
    public void resetPassword(String email, String token) {
        String resetLink = "https://nutrisoft.com/reset?token=" + token;
        sendPasswordResetEmailUseCase.execute(email, resetLink);
    }
}
```

## Variables Thymeleaf Disponibles

En cualquier plantilla puedes usar variables del contexto:

```html
<!-- Mostrar variable -->
<p th:text="${nombre}"></p>

<!-- Condicionales -->
<div th:if="${mostrar}">
    <p>Se muestra si mostrar es true</p>
</div>

<!-- Bucles -->
<ul>
    <li th:each="item : ${items}" th:text="${item}"></li>
</ul>

<!-- Operadores ternarios -->
<p th:text="${estado == 'activo' ? 'Activo' : 'Inactivo'}"></p>

<!-- URLs dinámicas -->
<a th:href="${urlBase} + '/dashboard'">Dashboard</a>
```

## Ejemplos de Plantillas

### Email Simple con Estilo

```html
<!DOCTYPE html>
<html lang="es" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 600px;
            margin: 0 auto;
        }
        .container {
            background-color: #f5f5f5;
            padding: 20px;
            border-radius: 8px;
        }
        .btn {
            display: inline-block;
            background-color: #667eea;
            color: white;
            padding: 12px 24px;
            text-decoration: none;
            border-radius: 4px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2 th:text="'Hola, ' + ${nombre} + '!'"></h2>
        <p th:text="${mensaje}"></p>
        <a th:href="${enlace}" class="btn">Haz Clic Aquí</a>
    </div>
</body>
</html>
```

### Email con Tabla de Datos

```html
<table style="width: 100%; border-collapse: collapse;">
    <thead>
        <tr style="background-color: #667eea; color: white;">
            <th style="padding: 10px;">Encabezado 1</th>
            <th style="padding: 10px;">Encabezado 2</th>
        </tr>
    </thead>
    <tbody>
        <tr th:each="fila : ${datos}" style="border-bottom: 1px solid #ddd;">
            <td style="padding: 10px;" th:text="${fila.campo1}"></td>
            <td style="padding: 10px;" th:text="${fila.campo2}"></td>
        </tr>
    </tbody>
</table>
```

## Mejores Prácticas

✅ **Usar CSS inline** - Los clientes de email tienen soporte limitado para `<style>`  
✅ **Validar variables** - Usar `th:if` para mostrar contenido solo si existe  
✅ **Responsive design** - Usar media queries para dispositivos móviles  
✅ **Contraste de colores** - Asegurar legibilidad en todos los temas  
✅ **Texto alternativo** - En imágenes usar atributo `alt`  
✅ **Evitar JavaScript** - No se ejecuta en clientes de email  
✅ **Límite de ancho** - Máximo 600px para mejor visualización  

## Envío Asincrónico

Los emails se envían de forma **asincrónica** usando `@Async`:

```java
@Async
public void execute(String email, String subject) {
    // Se ejecuta en un thread separado
    notifications.sendHtml(email, subject, htmlContent);
}
```

Esto evita bloquear la solicitud del usuario.

## Configuración de Email

Para cambiar la configuración, editar variables de entorno:

```bash
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=tu-email@gmail.com
MAIL_PASSWORD=tu-contraseña
MAIL_FROM=noreply@nutrisoft.com
MAIL_FROM_NAME=Nutrisoft
MAIL_SMTP_AUTH=true
MAIL_STARTTLS_ENABLE=true
```

## Testeo Local con Mailpit

Para ver los emails en desarrollo:

1. Iniciar Docker: `docker-compose up` (incluye Mailpit)
2. Abrir: http://localhost:8025
3. Ver todos los emails enviados
4. Inspeccionar HTML y fuente

## Resolución de Problemas

### El email no se envía

- ✓ Verificar logs en `docker-compose logs`
- ✓ Confirmar configuración SMTP en `application.yml`
- ✓ Revisar si el método tiene `@Async`

### La plantilla no renderiza

- ✓ Verificar que la ruta sea: `templates/emails/nombre.html`
- ✓ Revisar errores en logs
- ✓ Confirmar variables están en el Map del contexto

### Caracteres especiales no se muestran

- ✓ Asegurar `charset=UTF-8` en meta tag
- ✓ Verificar que `MimeMessageHelper` use `UTF-8`

## Recursos Útiles

- [Documentación Thymeleaf](https://www.thymeleaf.org/doc/html/Thymeleaf-3.1-html.html)
- [Estándares Email HTML](https://www.campaignmonitor.com/css/)
- [Prueba de Responsive Email](https://www.mailmodo.com/guides/responsive-email/)

