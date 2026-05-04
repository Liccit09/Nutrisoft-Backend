@echo off
REM Script de verificación - Asegura que todo está listo para usar Mailpit

echo.
echo 🔍 Verificando configuración de Mailpit...
echo.

REM Función para verificar archivo
setlocal enabledelayedexpansion

echo 📁 Verificando archivos de configuración...
echo.

if exist "docker-compose.yml" (
    echo ✅ Archivo existe: docker-compose.yml
) else (
    echo ❌ Archivo NO existe: docker-compose.yml
)

if exist "src\main\resources\application.yml" (
    echo ✅ Archivo existe: src\main\resources\application.yml
) else (
    echo ❌ Archivo NO existe: src\main\resources\application.yml
)

if exist ".env.example" (
    echo ✅ Archivo existe: .env.example
) else (
    echo ❌ Archivo NO existe: .env.example
)

echo.
echo 📚 Verificando documentación...
echo.

if exist "GETTING_STARTED.md" (
    echo ✅ Archivo existe: GETTING_STARTED.md
) else (
    echo ❌ Archivo NO existe: GETTING_STARTED.md
)

if exist "docs\MAILPIT_SETUP.md" (
    echo ✅ Archivo existe: docs\MAILPIT_SETUP.md
) else (
    echo ❌ Archivo NO existe: docs\MAILPIT_SETUP.md
)

if exist "docs\QUICK_REFERENCE.md" (
    echo ✅ Archivo existe: docs\QUICK_REFERENCE.md
) else (
    echo ❌ Archivo NO existe: docs\QUICK_REFERENCE.md
)

echo.
echo 🔧 Verificando scripts...
echo.

if exist "start-dev.bat" (
    echo ✅ Script existe: start-dev.bat
) else (
    echo ❌ Script NO existe: start-dev.bat
)

if exist "stop-dev.bat" (
    echo ✅ Script existe: stop-dev.bat
) else (
    echo ❌ Script NO existe: stop-dev.bat
)

echo.
echo 🐳 Verificando Docker...
echo.

where docker >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo ✅ Docker instalado
) else (
    echo ❌ Docker NO instalado
)

where docker-compose >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo ✅ Docker Compose instalado
) else (
    echo ❌ Docker Compose NO instalado
)

echo.
echo ☕ Verificando Java...
echo.

where java >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo ✅ Java instalado
    java -version 2>&1 | findstr /R "version"
) else (
    echo ❌ Java NO instalado
)

echo.
echo 📦 Verificando Maven...
echo.

where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo ✅ Maven instalado
) else (
    echo ❌ Maven NO instalado
)

echo.
echo 🎯 Verificación completada
echo.
echo Para comenzar, ejecuta:
echo   .\start-dev.bat
echo.
echo Luego abre en tu navegador:
echo   http://localhost:8025
echo.

endlocal

