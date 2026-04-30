# Sistema de Autenticación - Backend Nutrisoft

## Descripción

Sistema de autenticación JWT completo implementado con Spring Boot 3.3, Spring Security 6 y PostgreSQL.

## Características

✅ **Registro de usuarios** - Con validaciones completas  
✅ **Inicio de sesión** - Generación de JWT tokens  
✅ **Recuperación de contraseña** - Con tokens con expiración  
✅ **Seguridad** - BCrypt, CORS configurado, validación de entrada  
✅ **Email** - Envío de instrucciones de recuperación  

## Estructura del Código

```
com.nutrisoft
├── core
│   ├── domain
│   │   └── User.java                 # Entidad de usuario
│   └── service
│       ├── AuthenticationService.java # Lógica de autenticación
│       └── EmailService.java         # Envío de emails
├── infrastructure
│   ├── config
│   │   └── SecurityConfig.java       # Configuración de seguridad
│   ├── persistence
│   │   └── UserRepository.java       # Repositorio de usuarios
│   └── security
│       ├── JwtTokenProvider.java     # Generación y validación JWT
│       ├── UserPrincipal.java        # UserDetails personalizado
│       ├── CustomUserDetailsService.java
│       └── JwtAuthenticationFilter.java
└── userinterface
    └── api
        └── rest
            └── auth
                ├── AuthController.java
                └── dto
                    ├── LoginRequest.java
                    ├── RegisterRequest.java
                    ├── AuthResponse.java
                    ├── ForgotPasswordRequest.java
                    └── ResetPasswordRequest.java
```

## Endpoints

### POST /api/v1/auth/register
Registra un nuevo usuario

**Request:**
```json
{
  "email": "usuario@ejemplo.com",
  "firstName": "Juan",
  "lastName": "Pérez",
  "password": "contraseña123",
  "phoneNumber": "+57 300 123 4567"
}
```

**Response:** 201 Created
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "userId": 1,
  "email": "usuario@ejemplo.com",
  "firstName": "Juan",
  "lastName": "Pérez",
  "fullName": "Juan Pérez"
}
```

### POST /api/v1/auth/login
Inicia sesión

**Request:**
```json
{
  "email": "usuario@ejemplo.com",
  "password": "contraseña123"
}
```

**Response:** 200 OK
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "userId": 1,
  "email": "usuario@ejemplo.com",
  "firstName": "Juan",
  "lastName": "Pérez",
  "fullName": "Juan Pérez"
}
```

### POST /api/v1/auth/forgot-password
Solicita recuperación de contraseña

**Request:**
```json
{
  "email": "usuario@ejemplo.com"
}
```

**Response:** 200 OK

### GET /api/v1/auth/validate-reset-token
Valida un token de recuperación

**Query Parameter:** `token=TOKEN_AQUI`

**Response:** 200 OK / 400 Bad Request

### POST /api/v1/auth/reset-password
Restablece la contraseña

**Request:**
```json
{
  "token": "TOKEN_AQUI",
  "newPassword": "nuevacontraseña123"
}
```

**Response:** 200 OK

## Configuración

### application.yml

```yaml
app:
  jwtSecret: tu-clave-secreta-muy-larga-aqui
  jwtExpirationMs: 86400000  # 24 horas
  resetTokenExpirationHours: 24
  frontend:
    baseUrl: http://localhost:3000

spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: tu-email@gmail.com
    password: tu-app-password
    from: noreply@nutrisoft.com
    from-name: Nutrisoft
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

## Flujo de Seguridad

1. **Usuario registra/login** → POST /auth/register o /auth/login
2. **Backend valida credenciales** → Contra BCrypt hash
3. **Backend genera JWT** → JwtTokenProvider.generateToken()
4. **Frontend recibe token** → Lo almacena en localStorage
5. **Frontend envía token** → En header Authorization: Bearer TOKEN
6. **Backend valida token** → JwtAuthenticationFilter valida en cada request
7. **Spring Security establece contexto** → Usuario autenticado

## Clases Clave

### User Entity
```java
@Entity
@Table(name = "users")
public class User {
    private Long id;
    private String email;           // Único
    private String password;        // Hasheado
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin;
    private String passwordResetToken;
    private LocalDateTime passwordResetTokenExpiry;
    private Boolean emailVerified;
}
```

### JwtTokenProvider
```java
// Generación
String token = jwtTokenProvider.generateTokenFromUserId(userId);

// Validación
boolean isValid = jwtTokenProvider.validateToken(token);

// Extracción de datos
Long userId = jwtTokenProvider.getUserIdFromToken(token);
```

### SecurityConfig
- **Password Encoder:** BCryptPasswordEncoder
- **Session Policy:** STATELESS (sin sesiones en servidor)
- **CORS:** Configurado para desarrollo
- **Endpoints públicos:** /api/v1/auth/**
- **Endpoints protegidos:** Todos los demás

### JwtAuthenticationFilter
- Intercepta cada request
- Extrae token del header Authorization
- Valida el token
- Carga el usuario
- Establece el contexto de seguridad

## Seguridad

✅ **Contraseñas:** Hasheadas con BCrypt  
✅ **Tokens:** Firmados con HMAC-SHA256  
✅ **Expiración:** 24 horas  
✅ **Reset tokens:** Con expiración corta  
✅ **CORS:** Configurado específicamente  
✅ **CSRF:** Deshabilitado (stateless)  
✅ **Validación:** DTOs con @Valid  

## Testing

### Registro manual
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@ejemplo.com",
    "firstName": "Test",
    "lastName": "User",
    "password": "password123"
  }'
```

### Login manual
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@ejemplo.com",
    "password": "password123"
  }'
```

### Usar token protegido
```bash
curl -X GET http://localhost:8080/api/v1/appointments \
  -H "Authorization: Bearer TU_TOKEN_AQUI"
```

## Errores Comunes

### 401 Unauthorized
- Token expirado o inválido
- Token no incluido en el header
- Formato incorrecto: debe ser `Bearer TOKEN`

### 400 Bad Request
- Datos de entrada inválidos
- Email ya existe
- Contraseña no cumple requisitos

### 500 Internal Server Error
- Error en envío de email
- Error en base de datos
- Verificar logs de Spring Boot

## Próximas Mejoras

- [ ] Refresh tokens
- [ ] Cookies HttpOnly en lugar de localStorage
- [ ] Rate limiting para login
- [ ] Verificación de email obligatoria
- [ ] 2FA (Two-Factor Authentication)
- [ ] OAuth2/OpenID Connect
- [ ] Audit logging
- [ ] IP whitelisting

## Referencias

- [Spring Security Docs](https://spring.io/projects/spring-security)
- [JWT.io](https://jwt.io)
- [OWASP Auth Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html)
