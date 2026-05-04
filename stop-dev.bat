@echo off
REM Script para detener los contenedores Docker de Nutrisoft Backend

echo.
echo Deteniendo contenedores Docker...
docker-compose down

echo [OK] Contenedores detenidos exitosamente
echo.
echo Para eliminar también los volúmenes (base de datos), ejecuta:
echo   docker-compose down -v
echo.


