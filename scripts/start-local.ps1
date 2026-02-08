# OpenAdmin 本地启动脚本 (增强版)
# 用于在没有 Docker 环境下直接运行应用

param(
    [switch]$CheckOnly = $false,
    [switch]$KillExisting = $false
)

Write-Host "🚀 OpenAdmin 本地开发环境启动器" -ForegroundColor Green
Write-Host "===================================" -ForegroundColor Green

# 检查并终止现有进程
if ($KillExisting) {
    Write-Host "🔄 终止现有的 OpenAdmin 进程..." -ForegroundColor Yellow
    Get-Process -Name "java" -ErrorAction SilentlyContinue | Where-Object { 
        $_.CommandLine -match "openadmin" 
    } | Stop-Process -Force
    Start-Sleep -Seconds 2
}

# 检查必要的文件是否存在
$requiredFiles = @(
    @{Path="openadmin-samples/target/openadmin-samples-10.3.0-SNAPSHOT.jar"; Name="Samples应用"},
    @{Path="openadmin-gateway/target/openadmin-gateway-10.3.0-SNAPSHOT.jar"; Name="网关应用"}
)

$missingFiles = @()
foreach ($fileInfo in $requiredFiles) {
    if (-not (Test-Path $fileInfo.Path)) {
        $missingFiles += $fileInfo
        Write-Host "❌ 缺少文件: $($fileInfo.Name) ($($fileInfo.Path))" -ForegroundColor Red
    } else {
        Write-Host "✅ 找到文件: $($fileInfo.Name)" -ForegroundColor Green
    }
}

if ($missingFiles.Count -gt 0) {
    Write-Host "\n🔧 解决方案:" -ForegroundColor Yellow
    Write-Host "请先运行 Maven 构建:" -ForegroundColor Cyan
    Write-Host "mvn clean package -DskipTests" -ForegroundColor White
    Write-Host "或者使用 IDE 的 Maven 工具进行构建" -ForegroundColor White
    
    if ($CheckOnly) {
        exit 1
    }
    
    $continue = Read-Host "是否继续尝试启动? (y/N)"
    if ($continue -ne 'y' -and $continue -ne 'Y') {
        exit 1
    }
}

if ($CheckOnly) {
    Write-Host "✅ 环境检查完成，所有文件就绪!" -ForegroundColor Green
    exit 0
}

# 设置环境变量
$env:SPRING_PROFILES_ACTIVE = "docker"
$env:SPRING_DATASOURCE_URL = "jdbc:mysql://localhost:3306/openadmin?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai"
$env:SPRING_DATASOURCE_USERNAME = "openadmin"
$env:SPRING_DATASOURCE_PASSWORD = "openadmin123"
$env:SPRING_REDIS_HOST = "localhost"
$env:SPRING_REDIS_PORT = "6379"
$env:NACOS_SERVER_ADDR = "localhost:8848"

Write-Host "\n🔧 环境变量已设置:" -ForegroundColor Cyan
Write-Host "  数据库: $($env:SPRING_DATASOURCE_URL)" -ForegroundColor White
Write-Host "  Redis: $($env:SPRING_REDIS_HOST):$($env:SPRING_REDIS_PORT)" -ForegroundColor White
Write-Host "  Nacos: $($env:NACOS_SERVER_ADDR)" -ForegroundColor White

# 启动各个服务
$processes = @()

Write-Host "\n🔄 启动企业管理系统应用 (端口 8081)..." -ForegroundColor Blue
$proc1 = Start-Process -FilePath "java" -ArgumentList "-jar", "openadmin-samples/target/openadmin-samples-10.3.0-SNAPSHOT.jar", "--server.port=8081", "--spring.profiles.active=docker" -WindowStyle Minimized -PassThru
$processes += $proc1
Start-Sleep -Seconds 3

Write-Host "🔄 启动配置管理应用 (端口 8082)..." -ForegroundColor Blue
$proc2 = Start-Process -FilePath "java" -ArgumentList "-jar", "openadmin-samples/target/openadmin-samples-10.3.0-SNAPSHOT.jar", "--server.port=8082", "--spring.profiles.active=docker" -WindowStyle Minimized -PassThru
$processes += $proc2
Start-Sleep -Seconds 3

Write-Host "🔄 启动监控应用 (端口 8083)..." -ForegroundColor Blue
$proc3 = Start-Process -FilePath "java" -ArgumentList "-jar", "openadmin-samples/target/openadmin-samples-10.3.0-SNAPSHOT.jar", "--server.port=8083", "--spring.profiles.active=docker" -WindowStyle Minimized -PassThru
$processes += $proc3
Start-Sleep -Seconds 3

Write-Host "🔄 启动API网关 (端口 8080)..." -ForegroundColor Blue
$proc4 = Start-Process -FilePath "java" -ArgumentList "-jar", "openadmin-gateway/target/openadmin-gateway-10.3.0-SNAPSHOT.jar", "--server.port=8080", "--spring.profiles.active=docker" -WindowStyle Minimized -PassThru
$processes += $proc4

Write-Host "\n✅ 所有服务启动命令已发送!" -ForegroundColor Green
Write-Host "🌐 访问地址:" -ForegroundColor Yellow
Write-Host "  API网关: http://localhost:8080" -ForegroundColor White
Write-Host "  企业管理系统: http://localhost:8081" -ForegroundColor White
Write-Host "  配置管理: http://localhost:8082" -ForegroundColor White
Write-Host "  监控系统: http://localhost:8083" -ForegroundColor White
Write-Host "\n📋 进程信息:" -ForegroundColor Yellow
foreach ($proc in $processes) {
    Write-Host "  PID: $($proc.Id) - 状态: $($proc.ProcessName)" -ForegroundColor White
}

Write-Host "\n💡 使用说明:" -ForegroundColor Magenta
Write-Host "  - 查看日志: 在任务管理器中找到对应的 java 进程" -ForegroundColor White
Write-Host "  - 停止服务: 关闭对应的 java 进程或使用任务管理器" -ForegroundColor White
Write-Host "  - 重启服务: 先停止再重新运行此脚本" -ForegroundColor White
Write-Host "  - 检查环境: .\start-local.ps1 -CheckOnly" -ForegroundColor White
Write-Host "  - 强制重启: .\start-local.ps1 -KillExisting" -ForegroundColor White

# 保持脚本运行并监控进程
Write-Host "\n监听页面进程... 按 Ctrl+C 退出监控" -ForegroundColor Cyan

try {
    while ($true) {
        $runningCount = ($processes | Where-Object { -not $_.HasExited }).Count
        if ($runningCount -eq 0) {
            Write-Host "⚠️  所有进程已退出" -ForegroundColor Red
            break
        }
        
        Write-Host "[$(Get-Date -Format 'HH:mm:ss')] 运行中的进程: $runningCount/$($processes.Count)" -ForegroundColor Gray
        Start-Sleep -Seconds 30
    }
} catch {
    Write-Host "\n👋 监控已停止" -ForegroundColor Yellow
}