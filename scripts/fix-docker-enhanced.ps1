# Docker 环境修复脚本 (增强版 v2.0)
# 用于解决常见的 Docker 部署问题

param(
    [switch]$AutoFix = $false,
    [switch]$ShowConfig = $false
)

Write-Host "🔧 Docker 环境诊断和修复工具 (增强版 v2.0)" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green

# 显示当前配置
if ($ShowConfig) {
    Write-Host "`n📋 当前 Dockerfile 配置:" -ForegroundColor Yellow
    Get-ChildItem -Path "." -Filter "Dockerfile.*" | ForEach-Object {
        $content = Get-Content $_.FullName -First 3
        Write-Host "  $($_.Name):" -ForegroundColor Cyan
        $content | ForEach-Object { Write-Host "    $_" -ForegroundColor White }
    }
    exit 0
}

# 检查 Docker 是否安装
Write-Host "`n🔍 检查 Docker 安装状态..." -ForegroundColor Cyan
try {
    $dockerVersion = docker --version
    Write-Host "✅ Docker 已安装: $dockerVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker 未安装或未添加到 PATH" -ForegroundColor Red
    Write-Host "请安装 Docker Desktop 并确保已启动" -ForegroundColor Yellow
    exit 1
}

# 检查 Docker Desktop 是否运行
Write-Host "🔍 检查 Docker Desktop 运行状态..." -ForegroundColor Cyan
try {
    $dockerInfo = docker info 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Docker Desktop 正在运行" -ForegroundColor Green
    } else {
        Write-Host "❌ Docker Desktop 未运行" -ForegroundColor Red
        if ($AutoFix) {
            Write-Host "🔄 尝试启动 Docker Desktop..." -ForegroundColor Yellow
            Start-Process "Docker Desktop"
            Start-Sleep -Seconds 10
        } else {
            Write-Host "请手动启动 Docker Desktop 应用程序" -ForegroundColor Yellow
            exit 1
        }
    }
} catch {
    Write-Host "❌ 无法连接到 Docker daemon" -ForegroundColor Red
    exit 1
}

# 检查镜像拉取权限
Write-Host "🔍 测试镜像拉取能力..." -ForegroundColor Cyan
$testImages = @(
    @{Name="Alpine Linux"; Image="alpine:latest"},
    @{Name="Hello World"; Image="hello-world:latest"}
)

$pullSuccess = $true
foreach ($test in $testImages) {
    Write-Host "  测试 $($test.Name): $($test.Image)" -ForegroundColor Gray
    try {
        $result = docker pull $test.Image --quiet 2>$null
        if ($LASTEXITCODE -eq 0) {
            Write-Host "  ✅ $($test.Name) 拉取成功" -ForegroundColor Green
            # 清理测试镜像
            docker rmi $test.Image -f 2>$null | Out-Null
        } else {
            Write-Host "  ❌ $($test.Name) 拉取失败" -ForegroundColor Red
            $pullSuccess = $false
        }
    } catch {
        Write-Host "  ❌ $($test.Name) 测试异常" -ForegroundColor Red
        $pullSuccess = $false
    }
}

# 检查认证配置
Write-Host "🔍 检查 Docker 认证配置..." -ForegroundColor Cyan
$credHelpers = Get-Command docker-credential-desktop -ErrorAction SilentlyContinue
if ($credHelpers) {
    try {
        $credVersion = docker-credential-desktop version 2>$null
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ Docker credential helper 正常工作" -ForegroundColor Green
        } else {
            Write-Host "⚠️  Docker credential helper 有版本问题" -ForegroundColor Yellow
        }
    } catch {
        Write-Host "⚠️  Docker credential helper 无法正常调用" -ForegroundColor Yellow
    }
} else {
    Write-Host "⚠️  Docker credential helper 未找到" -ForegroundColor Yellow
    if ($AutoFix) {
        Write-Host "💡 建议: 重新安装 Docker Desktop" -ForegroundColor Yellow
    }
}

# 自动修复选项
if ($AutoFix -and -not $pullSuccess) {
    Write-Host "`n🔄 尝试自动修复镜像拉取问题..." -ForegroundColor Yellow
    
    # 尝试清理 Docker 系统
    Write-Host "  清理 Docker 系统缓存..." -ForegroundColor Gray
    docker system prune -f 2>$null | Out-Null
    
    # 尝试重启 Docker daemon
    Write-Host "  重启 Docker 服务..." -ForegroundColor Gray
    Restart-Service com.docker.service -Force -ErrorAction SilentlyContinue
    Start-Sleep -Seconds 5
    
    # 重新测试
    Write-Host "  重新测试镜像拉取..." -ForegroundColor Gray
    try {
        docker pull alpine:latest --quiet 2>$null
        if ($LASTEXITCODE -eq 0) {
            Write-Host "  ✅ 镜像拉取问题已修复" -ForegroundColor Green
            $pullSuccess = $true
        }
    } catch {}
}

# 提供解决方案
Write-Host "`n📋 诊断结果和建议:" -ForegroundColor Yellow
if ($pullSuccess) {
    Write-Host "✅ Docker 环境基本正常" -ForegroundColor Green
    Write-Host "建议尝试: docker compose up" -ForegroundColor White
} else {
    Write-Host "❌ 存在镜像拉取问题" -ForegroundColor Red
    Write-Host "`n推荐解决方案:" -ForegroundColor Yellow
    Write-Host "1. 使用本地 JAR 包运行 (最稳定): .\start-local.ps1" -ForegroundColor White
    Write-Host "2. 重新安装 Docker Desktop" -ForegroundColor White
    Write-Host "3. 检查网络代理设置" -ForegroundColor White
    Write-Host "4. 使用 .proxy 后缀的 Dockerfile" -ForegroundColor White
}

Write-Host "`n🚀 快速操作命令:" -ForegroundColor Green
Write-Host "  检查环境: .\fix-docker.ps1" -ForegroundColor White
Write-Host "  自动修复: .\fix-docker.ps1 -AutoFix" -ForegroundColor White
Write-Host "  显示配置: .\fix-docker.ps1 -ShowConfig" -ForegroundColor White
Write-Host "  本地运行: .\start-local.ps1" -ForegroundColor White

if (-not $pullSuccess -and -not $AutoFix) {
    $choice = Read-Host "是否尝试自动修复? (y/N)"
    if ($choice -eq 'y' -or $choice -eq 'Y') {
        & "$PSCommandPath" -AutoFix
    }
}