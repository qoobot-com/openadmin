# Docker 环境修复脚本
# 用于解决常见的 Docker 部署问题

Write-Host "🔧 Docker 环境诊断和修复工具" -ForegroundColor Green
Write-Host "============================" -ForegroundColor Green

# 检查 Docker 是否安装
Write-Host "🔍 检查 Docker 安装状态..." -ForegroundColor Cyan
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
        Write-Host "请启动 Docker Desktop 应用程序" -ForegroundColor Yellow
        exit 1
    }
} catch {
    Write-Host "❌ 无法连接到 Docker daemon" -ForegroundColor Red
    exit 1
}

# 检查镜像拉取权限
Write-Host "🔍 测试镜像拉取..." -ForegroundColor Cyan
$testImages = @(
    "hello-world:latest",
    "alpine:latest"
)

foreach ($image in $testImages) {
    Write-Host "  测试镜像: $image" -ForegroundColor Gray
    try {
        docker pull $image --quiet 2>$null
        if ($LASTEXITCODE -eq 0) {
            Write-Host "  ✅ $image 拉取成功" -ForegroundColor Green
            # 清理测试镜像
            docker rmi $image -f 2>$null | Out-Null
        } else {
            Write-Host "  ❌ $image 拉取失败" -ForegroundColor Red
        }
    } catch {
        Write-Host "  ❌ $image 测试异常" -ForegroundColor Red
    }
}

# 检查认证配置
Write-Host "🔍 检查 Docker 认证配置..." -ForegroundColor Cyan
$credHelpers = docker-credential-desktop version 2>$null
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Docker credential helper 正常工作" -ForegroundColor Green
} else {
    Write-Host "⚠️  Docker credential helper 有问题" -ForegroundColor Yellow
    Write-Host "建议重新安装 Docker Desktop" -ForegroundColor Yellow
}

# 提供解决方案
Write-Host "\\n📋 可能的解决方案:" -ForegroundColor Yellow
Write-Host "1. 使用国内镜像源 (已配置)" -ForegroundColor White
Write-Host "2. 降级 JDK 版本 (已配置)" -ForegroundColor White
Write-Host "3. 使用替代的 Temurin 镜像 (已提供)" -ForegroundColor White
Write-Host "4. 直接运行 JAR 包 (推荐)" -ForegroundColor White

Write-Host "\\n🚀 推荐操作:" -ForegroundColor Green
Write-Host "运行本地启动脚本: .\\start-local.ps1" -ForegroundColor White
Write-Host "或者检查具体错误后选择对应 Dockerfile" -ForegroundColor White

Write-Host "\\n💡 快速命令:" -ForegroundColor Magenta
Write-Host "  检查环境: .\\start-local.ps1 -CheckOnly" -ForegroundColor White
Write-Host "  强制重启: .\\start-local.ps1 -KillExisting" -ForegroundColor White
Write-Host "  使用 Temurin 镜像: 将 *.temurin 文件重命名为对应名称" -ForegroundColor White