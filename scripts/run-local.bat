@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo ========================================
echo OpenAdmin Local Development Startup
echo ========================================

REM Check if Maven is available
set "MAVEN_CMD=mvn"
call :find_maven

echo Using Maven: !MAVEN_CMD!

"!MAVEN_CMD!" --version >nul 2>&1
if !errorlevel! neq 0 (
    echo Error: Maven not found. Please install Maven first.
    pause
    exit /b 1
)

REM Build project
echo Building project...
"!MAVEN_CMD!" clean package -DskipTests
if !errorlevel! neq 0 (
    echo Error: Build failed.
    pause
    exit /b 1
)

echo.
echo ========================================
echo Starting OpenAdmin Applications
echo ========================================
echo Press Ctrl+C to stop all applications
echo.

REM Start Enterprise Management System (Port 8081)
start "Enterprise App" cmd /k "java -jar openadmin-samples\target\openadmin-samples-10.3.0-SNAPSHOT.jar --spring.profiles.active=enterprise"

timeout /t 5 /nobreak >nul

REM Start Config Management Application (Port 8082)
start "Config App" cmd /k "java -jar openadmin-samples\target\openadmin-samples-10.3.0-SNAPSHOT.jar --spring.profiles.active=config"

timeout /t 5 /nobreak >nul

REM Start Monitor Application (Port 8083)
start "Monitor App" cmd /k "java -jar openadmin-samples\target\openadmin-samples-10.3.0-SNAPSHOT.jar --spring.profiles.active=monitor"

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
exit /b 0

:find_maven
REM Try to find Maven in common locations
if exist "D:\ProgramFiles\JetBrains\IntelliJ IDEA 2025.3.2\plugins\maven\lib\maven3\bin\mvn.cmd" (
    set "MAVEN_CMD=D:\ProgramFiles\JetBrains\IntelliJ IDEA 2025.3.2\plugins\maven\lib\maven3\bin\mvn.cmd"
    goto :eof
)

if exist "%MAVEN_HOME%\bin\mvn.cmd" (
    set "MAVEN_CMD=%MAVEN_HOME%\bin\mvn.cmd"
    goto :eof
)

if exist "%M2_HOME%\bin\mvn.cmd" (
    set "MAVEN_CMD=%M2_HOME%\bin\mvn.cmd"
    goto :eof
)

goto :eof