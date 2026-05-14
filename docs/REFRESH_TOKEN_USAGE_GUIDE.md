# 🔐 Guía de Uso: Sistema de Autenticación con Refresh Tokens

## 📚 Introducción

Este documento describe cómo usar el sistema de autenticación mejorado con refresh tokens en Nutrisoft Backend.

---

## 🔄 Flujo de Autenticación

### 1. **LOGIN** - Obtener tokens
```
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePassword123!"
}
```

**Respuesta (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "aggregateId": "550e8400-e29b-41d4-a716-446655440000",
  "email": "user@example.com",
  "role": "PROFESSIONAL"
}
```

**Headers de respuesta:**
```
Set-Cookie: refreshToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...; HttpOnly; Secure; SameSite=Strict; Path=/; Max-Age=604800
```

### 2. **USAR ACCESS TOKEN** - En cada request autenticado
```
GET /api/v1/appointments
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

El access token dura **24 horas**.

### 3. **REFRESH TOKEN** - Cuando el access token está por expirar
```
POST /api/v1/auth/refresh-token
Cookie: refreshToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Respuesta (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...(NUEVO)",
  "type": "Bearer",
  "aggregateId": "550e8400-e29b-41d4-a716-446655440000",
  "email": "user@example.com",
  "role": "PROFESSIONAL"
}
```

**Headers de respuesta:**
```
Set-Cookie: refreshToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...(NUEVO); ...
```

⚠️ **Importante:** El refresh token es rotado automáticamente. Siempre usa el nuevo token de la cookie.

### 4. **LOGOUT** - Cerrar sesión
```
POST /api/v1/auth/logout
Cookie: refreshToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Respuesta (200 OK):**
```
(vacío, pero con Set-Cookie header para expirar la cookie)
```

---

## 🛠️ Ejemplo en Frontend (TypeScript/React)

### Setup de Axios Interceptor

```typescript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  withCredentials: true, // ⚠️ IMPORTANTE: envía cookies automáticamente
});

// Interceptor para renovar token si falla
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        // Intentar refrescar el token
        const response = await api.post('/v1/auth/refresh-token');
        
        // Actualizar el access token en local storage (opcional)
        localStorage.setItem('accessToken', response.data.token);
        
        // Reintentar el request original con el nuevo token
        originalRequest.headers.Authorization = `Bearer ${response.data.token}`;
        return api(originalRequest);
      } catch (refreshError) {
        // Si el refresh falla, redirigir a login
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default api;
```

### Usar en un componente

```typescript
// Login
const handleLogin = async (email: string, password: string) => {
  const response = await api.post('/v1/auth/login', { email, password });
  // La cookie se guarda automáticamente (withCredentials: true)
  localStorage.setItem('accessToken', response.data.token);
  localStorage.setItem('aggregateId', response.data.aggregateId);
};

// Hacer requests autenticados
const handleGetAppointments = async () => {
  const token = localStorage.getItem('accessToken');
  const response = await api.get('/v1/appointments', {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.data;
};

// Logout
const handleLogout = async () => {
  await api.post('/v1/auth/logout');
  localStorage.removeItem('accessToken');
  localStorage.removeItem('aggregateId');
};
```

---

## 🧪 Pruebas con cURL

### 1. Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "SecurePassword123!"
  }' \
  -c cookies.txt
```

### 2. Refresh Token
```bash
curl -X POST http://localhost:8080/api/v1/auth/refresh-token \
  -b cookies.txt \
  -c cookies.txt
```

### 3. Logout
```bash
curl -X POST http://localhost:8080/api/v1/auth/logout \
  -b cookies.txt \
  -c cookies.txt
```

### 4. Usar Access Token
```bash
curl -H "Authorization: Bearer <ACCESS_TOKEN>" \
  http://localhost:8080/api/v1/appointments
```

---

## ⏱️ Tiempos de Expiración

| Token | Duración | Renovable | Persistencia |
|---|---|---|---|
| **Access Token** | 24 horas | No | En memoria/localStorage |
| **Refresh Token** | 7 días | Sí (token rotation) | HttpOnly Cookie + BD |

---

## 🔒 Características de Seguridad

### Cookie Flags
```
HttpOnly  ✅ No accesible desde JavaScript
Secure   ✅ Solo HTTPS
SameSite=Strict ✅ Protección contra CSRF
Path=/   ✅ Válida para toda la aplicación
Max-Age=604800  ✅ Expira en 7 días
```

### Token Claims
```json
{
  "sub": "user@example.com",
  "email": "user@example.com",
  "role": "PROFESSIONAL",
  "token_type": "access",  // ✅ Diferencia tipos
  "iat": 1652534400,
  "exp": 1652620800
}
```

### Auditoría en BD
```sql
SELECT * FROM refresh_tokens WHERE credential_id = '...';
-- Contiene: user_agent, ip_address, created_at, used_at, revoked_at
```

---

## ❌ Manejo de Errores

### Error: Token expirado
```
401 Unauthorized
{
  "error": "Unauthorized",
  "message": "Refresh token is invalid or has expired"
}
```

**Acción:** Redirigir a login

### Error: Token revocado
```
401 Unauthorized
{
  "error": "Unauthorized",
  "message": "Refresh token is invalid or has expired"
}
```

**Acción:** Redirigir a login

### Error: Token type incorrecto
```
401 Unauthorized
{
  "error": "Unauthorized",
  "message": "Token type is not a refresh token"
}
```

**Acción:** No debería ocurrir, pero indica un bug en el cliente

### Error: Credenciales inválidas
```
401 Unauthorized
{
  "error": "Unauthorized",
  "message": "Invalid email or password"
}
```

**Acción:** Mostrar formulario de login nuevamente

### Error: Cuenta deshabilitada
```
403 Forbidden
{
  "error": "Forbidden",
  "message": "Account is disabled"
}
```

**Acción:** Mostrar mensaje de error y contactar soporte

---

## 🔧 Variables de Configuración

En `application.yml`:

```yaml
app:
  jwtSecret: ${JWT_SECRET:nutrisoft-secret-key-...}
  jwtExpirationMs: 86400000              # 24 horas
  jwtRefreshExpirationMs: 604800000      # 7 días
  resetTokenExpirationHours: 24
  frontend:
    baseUrl: ${FRONTEND_BASE_URL:http://localhost:3000}
```

---

## 📝 Próximas Características

- [ ] Logout endpoint implementado ✅
- [ ] Múltiples sesiones activas por usuario
- [ ] Listado de sesiones (dispositivo, IP, última actividad)
- [ ] Cierre remoto de sesión
- [ ] Revocación en cascada (cambio de contraseña)
- [ ] Scheduler de limpieza de tokens expirados
- [ ] Rate limiting en login
- [ ] 2FA (Two-Factor Authentication)

---

## 📞 Soporte

Para más información sobre la arquitectura de autenticación, ver:
- `/docs/AUTENTICACION_BACKEND.md`
- `/docs/DEVELOPER_GUIDE.md`

