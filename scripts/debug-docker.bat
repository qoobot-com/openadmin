@echo off
echo ========================================
echo Docker Compose Debug Script
echo ========================================

REM 检查Docker是否安装
echo Checking Docker installation...
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Docker is not installed or not in PATH
    echo Please install Docker Desktop from: https://www.docker.com/products/docker-desktop/
    goto :end
)

REM 检查Docker是否运行
echo Checking Docker daemon...
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Docker daemon is not running
    echo Please start Docker Desktop
    goto :end
)

REM 检查docker-compose
echo Checking docker-compose...
docker-compose --version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: docker-compose not found
    echo Using docker compose plugin instead...
    set COMPOSE_CMD=docker compose
) else (
    set COMPOSE_CMD=docker-compose
)

REM 检查项目构建状态
echo Checking if project is built...
if not exist "openadmin-samples\target\openadmin-samples-10.3.0-SNAPSHOT.jar" (
    echo Building project...
    mvn clean package -DskipTests
    if %errorlevel% neq 0 (
        echo Error: Build failed
        goto :end
    )
)

if not exist "openadmin-gateway\target\openadmin-gateway-10.3.0-SNAPSHOT.jar" (
    echo Building gateway...
    cd openadmin-gateway
    mvn clean package -DskipTests
    cd ..
    if %errorlevel% neq 0 (
        echo Error: Gateway build failed
        goto :end
    )
)

REM 尝试启动简化版docker-compose
echo Starting simple services...
%COMPOSE_CMD% -f docker-compose-simple.yml up -d

if %errorlevel% neq 0 (
    echo Error: Failed to start services
    echo Showing logs...
    %COMPOSE_CMD% -f docker-compose-simple.yml logs
    goto :end
)

echo Services started successfully!
echo MySQL: localhost:3306
echo Redis: localhost:6379

:end
echo.
echo Press any key to continue...
pause >nul