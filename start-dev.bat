@echo off
REM Script para iniciar Docker Compose y la aplicación Nutrisoft Backend
REM Este script levanta PostgreSQL y Mailpit, luego inicia la aplicación Spring Boot

echo.
echo ============================================
echo  Nutrisoft Backend - Startup Script
echo ============================================
echo.

REM Verificar si Docker está disponible
where docker >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Docker no está instalado o no está en el PATH
    echo         Por favor, instala Docker Desktop
    echo         https://www.docker.com/products/docker-desktop
    exit /b 1
)

REM Verificar si Docker Compose está disponible
where docker-compose >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Docker Compose no está disponible
    exit /b 1
)

echo [1/3] Iniciando contenedores Docker (PostgreSQL + Mailpit)...
docker-compose up -d

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Error al iniciar Docker Compose
    exit /b 1
)

echo [OK] Contenedores iniciados exitosamente
echo.
echo [2/3] Esperando a que los servicios estén listos...
timeout /t 5 /nobreak
echo.
echo [3/3] Iniciando aplicación Spring Boot...
echo.
echo ============================================
echo  Nutrisoft Backend - Información de Acceso
echo ============================================
echo.
echo PostgreSQL:
echo   - Host: localhost
echo   - Puerto: 5432
echo   - Usuario: nutrisoft_user
echo   - Password: nutrisoft_password
echo   - Base de datos: nutrisoft_db
echo.
echo Mailpit (Captura de Correos):
echo   - Web UI: http://localhost:8025
echo   - SMTP: localhost:1025
echo.
echo Aplicación Spring Boot:
echo   - URL: http://localhost:8080/api
echo   - Swagger UI: http://localhost:8080/api/swagger-ui.html
echo.
echo ============================================
echo.

REM Inicia la aplicación Spring Boot
call mvn spring-boot:run


