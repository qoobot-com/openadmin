@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo ========================================
echo OpenAdmin Simple Startup
echo ========================================

REM 检查JAR文件
set "JAR_PATH=openadmin-samples\target\openadmin-samples-10.3.0-SNAPSHOT.jar"
if not exist "%JAR_PATH%" (
    echo Building project first...
    mvn clean package -DskipTests
    if !errorlevel! neq 0 (
        echo Build failed!
        pause
        exit /b 1
    )
)

echo Starting simple application...
echo Access URL: http://localhost:8080
echo Health check: http://localhost:8080/actuator/health
echo H2 Console: http://localhost:8080/h2-console

java -jar "%JAR_PATH%" --spring.profiles.active=simple

pause