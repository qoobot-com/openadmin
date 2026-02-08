# OpenAdmin Local Development Startup Script
# 设置UTF-8编码
$OutputEncoding = New-Object -TypeName System.Text.UTF8Encoding
[Console]::InputEncoding = [System.Text.Encoding]::UTF8
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host "========================================" -ForegroundColor Green
Write-Host "OpenAdmin Local Development Startup" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green

# 查找Maven路径
function Find-Maven {
    $possiblePaths = @(
        "D:\ProgramFiles\JetBrains\IntelliJ IDEA 2025.3.2\plugins\maven\lib\maven3\bin\mvn.cmd",
        "$env:MAVEN_HOME\bin\mvn.cmd",
        "$env:M2_HOME\bin\mvn.cmd"
    )
    
    foreach ($path in $possiblePaths) {
        if (Test-Path $path) {
            return $path
        }
    }
    
    # 如果都没找到，返回默认mvn命令
    return "mvn"
}

# 获取Maven命令
$mvnCmd = Find-Maven
Write-Host "Using Maven: $mvnCmd" -ForegroundColor Yellow

# 检查Maven是否可用
try {
    & $mvnCmd --version | Out-Null
    Write-Host "Maven is available" -ForegroundColor Green
} catch {
    Write-Error "Error: Maven not found. Please install Maven first."
    Read-Host "Press Enter to exit"
    exit 1
}

# 编译项目
Write-Host "Building project..." -ForegroundColor Yellow
try {
    & $mvnCmd clean package -DskipTests
    if ($LASTEXITCODE -ne 0) {
        throw "Build failed"
    }
    Write-Host "Build completed successfully" -ForegroundColor Green
} catch {
    Write-Error "Error: Build failed."
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "Starting OpenAdmin Applications" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host "Press Ctrl+C to stop all applications" -ForegroundColor Yellow
Write-Host ""

# 启动应用程序的函数
function Start-Application {
    param(
        [string]$Name,
        [string]$Profile,
        [int]$Port,
        [int]$Delay = 5
    )
    
    Write-Host "Starting $Name on port $Port..." -ForegroundColor Yellow
    
    $jarPath = "openadmin-samples\target\openadmin-samples-10.3.0-SNAPSHOT.jar"
    $command = "java -jar $jarPath --spring.profiles.active=$Profile"
    
    Start-Process powershell -ArgumentList "-Command", "& { $command; Read-Host 'Press Enter to close' }" -WindowStyle Normal -PassThru | Out-Null
    
    Start-Sleep -Seconds $Delay
}

# 启动各个应用
Start-Application -Name "Enterprise App" -Profile "enterprise" -Port 8081
Start-Application -Name "Config App" -Profile "config" -Port 8082
Start-Application -Name "Monitor App" -Profile "monitor" -Port 8083

Write-Host ""
Write-Host "All applications started successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "Access URLs:" -ForegroundColor Cyan
Write-Host "Enterprise App: http://localhost:8081"
Write-Host "Config App: http://localhost:8082"
Write-Host "Monitor App: http://localhost:8083"
Write-Host ""
Write-Host "Health checks:" -ForegroundColor Cyan
Write-Host "http://localhost:8081/actuator/health"
Write-Host "http://localhost:8082/actuator/health"
Write-Host "http://localhost:8083/actuator/health"
Write-Host ""

Read-Host "Press Enter to exit"