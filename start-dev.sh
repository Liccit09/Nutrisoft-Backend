#!/bin/bash

# Script para iniciar Docker Compose y la aplicación Nutrisoft Backend
# Este script levanta PostgreSQL y Mailpit, luego inicia la aplicación Spring Boot

echo ""
echo "============================================"
echo "  Nutrisoft Backend - Startup Script"
echo "============================================"
echo ""

# Verificar si Docker está disponible
if ! command -v docker &> /dev/null; then
    echo "[ERROR] Docker no está instalado"
    echo "        Por favor, instala Docker Desktop"
    echo "        https://www.docker.com/products/docker-desktop"
    exit 1
fi

# Verificar si Docker Compose está disponible
if ! command -v docker-compose &> /dev/null; then
    echo "[ERROR] Docker Compose no está disponible"
    exit 1
fi

echo "[1/3] Iniciando contenedores Docker (PostgreSQL + Mailpit)..."
docker-compose up -d

if [ $? -ne 0 ]; then
    echo "[ERROR] Error al iniciar Docker Compose"
    exit 1
fi

echo "[OK] Contenedores iniciados exitosamente"
echo ""
echo "[2/3] Esperando a que los servicios estén listos..."
sleep 5
echo ""
echo "[3/3] Iniciando aplicación Spring Boot..."
echo ""
echo "============================================"
echo "  Nutrisoft Backend - Información de Acceso"
echo "============================================"
echo ""
echo "PostgreSQL:"
echo "  - Host: localhost"
echo "  - Puerto: 5432"
echo "  - Usuario: nutrisoft_user"
echo "  - Password: nutrisoft_password"
echo "  - Base de datos: nutrisoft_db"
echo ""
echo "Mailpit (Captura de Correos):"
echo "  - Web UI: http://localhost:8025"
echo "  - SMTP: localhost:1025"
echo ""
echo "Aplicación Spring Boot:"
echo "  - URL: http://localhost:8080/api"
echo "  - Swagger UI: http://localhost:8080/api/swagger-ui.html"
echo ""
echo "============================================"
echo ""

# Inicia la aplicación Spring Boot
mvn spring-boot:run


