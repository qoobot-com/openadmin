@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo ========================================
echo OpenAdmin Application Launcher
echo ========================================

REM 检查JAR文件是否存在
set "JAR_PATH=openadmin-samples\target\openadmin-samples-10.3.0-SNAPSHOT.jar"
if not exist "%JAR_PATH%" (
    echo Error: JAR file not found at %JAR_PATH%
    echo Please build the project first using: mvn clean package -DskipTests
    pause
    exit /b 1
)

echo Starting OpenAdmin Applications
echo Press Ctrl+C to stop all applications
echo.

REM 启动企业管理系统 (端口 8081)
echo Starting Enterprise App on port 8081...
start "Enterprise App" cmd /k "java -jar \"%JAR_PATH%\" --spring.profiles.active=enterprise"

timeout /t 5 /nobreak >nul

REM 启动配置管理应用 (端口 8082)
echo Starting Config App on port 8082...
start "Config App" cmd /k "java -jar \"%JAR_PATH%\" --spring.profiles.active=config"

timeout /t 5 /nobreak >nul

REM 启动监控应用 (端口 8083)
echo Starting Monitor App on port 8083...
start "Monitor App" cmd /k "java -jar \"%JAR_PATH%\" --spring.profiles.active=monitor"

echo.
echo All applications started successfully!
echo.
echo Access URLs:
echo Enterprise App: http://localhost:8081
echo Config App: http://localhost:8082
echo Monitor App: http://localhost:8083
echo.
echo Health checks:
echo http://localhost:8081/actuator/health
echo http://localhost:8082/actuator/health
echo http://localhost:8083/actuator/health
echo.

pause