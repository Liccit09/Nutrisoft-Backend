#!/bin/bash
# Script de verificación - Asegura que todo está listo para usar Mailpit

echo "🔍 Verificando configuración de Mailpit..."
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Función para verificar archivo
check_file() {
    if [ -f "$1" ]; then
        echo -e "${GREEN}✅${NC} Archivo existe: $1"
        return 0
    else
        echo -e "${RED}❌${NC} Archivo NO existe: $1"
        return 1
    fi
}

echo "📁 Verificando archivos de configuración..."
echo ""

# Archivos de configuración
check_file "docker-compose.yml"
check_file "src/main/resources/application.yml"
check_file ".env.example"

echo ""
echo "📚 Verificando documentación..."
echo ""

# Documentación
check_file "GETTING_STARTED.md"
check_file "docs/MAILPIT_SETUP.md"
check_file "docs/QUICK_REFERENCE.md"
check_file "docs/MAILPIT_CONFIGURATION_SUMMARY.md"

echo ""
echo "🔧 Verificando scripts..."
echo ""

# Scripts
check_file "start-dev.bat"
check_file "start-dev.sh"
check_file "stop-dev.bat"
check_file "stop-dev.sh"

echo ""
echo "🐳 Verificando Docker..."
echo ""

if command -v docker &> /dev/null; then
    echo -e "${GREEN}✅${NC} Docker instalado"
else
    echo -e "${RED}❌${NC} Docker NO instalado"
fi

if command -v docker-compose &> /dev/null; then
    echo -e "${GREEN}✅${NC} Docker Compose instalado"
else
    echo -e "${RED}❌${NC} Docker Compose NO instalado"
fi

echo ""
echo "☕ Verificando Java..."
echo ""

if command -v java &> /dev/null; then
    java_version=$(java -version 2>&1 | head -n 1)
    echo -e "${GREEN}✅${NC} Java instalado: $java_version"
else
    echo -e "${RED}❌${NC} Java NO instalado"
fi

echo ""
echo "📦 Verificando Maven..."
echo ""

if command -v mvn &> /dev/null; then
    mvn_version=$(mvn -v 2>&1 | head -n 1)
    echo -e "${GREEN}✅${NC} Maven instalado: $mvn_version"
else
    echo -e "${RED}❌${NC} Maven NO instalado"
fi

echo ""
echo "🎯 Verificación completada"
echo ""
echo "Para comenzar, ejecuta:"
echo -e "  ${YELLOW}./start-dev.sh${NC}"
echo ""
echo "Luego abre en tu navegador:"
echo -e "  ${YELLOW}http://localhost:8025${NC}"
echo ""

