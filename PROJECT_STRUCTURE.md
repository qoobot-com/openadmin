# OpenAdmin 项目目录结构

本文档详细说明了 OpenAdmin 项目的目录组织结构。

## 📁 根目录结构

```
openadmin/
├── pom.xml                          # Maven 父项目配置文件
├── README.md                        # 项目说明文档
├── LICENSE                          # 许可证文件
├── .gitignore                       # Git 忽略配置
│
├── openadmin-admin/                 # 管理后台模块
├── openadmin-core/                  # 核心功能模块
├── openadmin-gateway/               # API 网关模块
├── openadmin-security/              # 安全认证模块
├── openadmin-config/                # 配置管理模块
├── openadmin-monitor/               # 监控模块
├── openadmin-starter/               # 自动配置模块
├── openadmin-samples/               # 综合示例应用
│
├── examples/                        # 独立示例项目
│   └── simple-demo/                # 简单演示项目（Spring Boot 3.2.0）
│
├── scripts/                         # 运行脚本目录
│   ├── debug-docker.bat            # Docker 调试脚本
│   ├── fix-docker.ps1              # Docker 修复脚本
│   ├── fix-docker-enhanced.ps1     # Docker 增强修复脚本
│   ├── run-local.bat               # 本地运行脚本
│   ├── run-local.ps1               # 本地运行 PowerShell 脚本
│   ├── start-apps.bat              # 启动所有应用
│   ├── start-local.ps1             # 本地启动 PowerShell 脚本
│   └── start-simple.bat            # 启动简单应用
│
├── deployments/                     # 部署相关配置
│   ├── docker-compose.yml          # 完整 Docker Compose 配置
│   ├── docker-compose-simple.yml   # 简化 Docker Compose 配置
│   │
│   ├── dockerfiles/                # Docker 镜像定义文件
│   │   ├── Dockerfile              # 基础 Dockerfile
│   │   ├── Dockerfile.config*      # 配置服务相关
│   │   ├── Dockerfile.enterprise*  # 企业服务相关
│   │   ├── Dockerfile.gateway*     # 网关服务相关
│   │   └── Dockerfile.monitor*     # 监控服务相关
│   │
│   ├── docker/                     # Docker 辅助配置
│   │   ├── grafana/               # Grafana 配置
│   │   ├── mysql/                 # MySQL 初始化脚本
│   │   └── prometheus/            # Prometheus 配置
│   │
│   └── configs/                    # 项目配置文件
│       ├── checkstyle.xml         # Checkstyle 配置
│       ├── owasp-suppressions.xml # OWASP 依赖检查抑制规则
│       ├── sonar-project.properties # SonarQube 配置
│       ├── settings.xml           # Maven 设置
│       └── dependency_tree*.txt   # 依赖树文件
│
├── .github/                        # GitHub 配置
│   └── workflows/                 # GitHub Actions 工作流
│       └── ci-cd.yml             # CI/CD 配置
│
└── .idea/                          # IDE 配置（忽略）
```

## 📦 模块说明

### 核心模块

| 模块 | 说明 | 端口 |
|------|------|------|
| openadmin-admin | 管理后台模块，提供用户、角色、权限等管理功能 | 8084 |
| openadmin-core | 核心功能模块，提供基础服务和工具类 | - |
| openadmin-gateway | API 网关模块，提供统一入口和路由转发 | 8080 |
| openadmin-security | 安全认证模块，提供 JWT/OAuth2 认证 | - |
| openadmin-config | 配置管理模块，支持动态配置和热更新 | 8082 |
| openadmin-monitor | 监控模块，提供性能监控和告警功能 | 8083 |
| openadmin-starter | 自动配置模块，简化框架集成 | - |

### 示例应用

| 应用 | 说明 | 端口 |
|------|------|------|
| openadmin-samples | 综合示例应用，包含企业管理、配置管理、监控等 | 8081 |
| examples/simple-demo | 简单演示项目，独立于主项目版本 | 8080 |

## 🔧 部署相关

### Docker 镜像文件

- `Dockerfile` - 基础镜像
- `Dockerfile.{config|enterprise|gateway|monitor}` - 各服务专用镜像
- `Dockerfile.*.cn` - 中国网络优化版本
- `Dockerfile.*.proxy` - 代理配置版本
- `Dockerfile.*.temurin` - Temurin JDK 版本

### Docker Compose 配置

- `docker-compose.yml` - 完整配置，包含所有服务
- `docker-compose-simple.yml` - 简化配置，仅包含核心服务

### 辅助配置

- `docker/grafana/` - Grafana 仪表板和数据源配置
- `docker/mysql/` - MySQL 数据库初始化脚本
- `docker/prometheus/` - Prometheus 监控配置

## 📝 配置文件

| 文件 | 说明 |
|------|------|
| checkstyle.xml | Checkstyle 代码风格检查配置 |
| owasp-suppressions.xml | OWASP 依赖检查漏洞抑制规则 |
| sonar-project.properties | SonarQube 代码质量分析配置 |
| settings.xml | Maven 仓库和镜像配置 |
| dependency_tree*.txt | 项目依赖关系树 |

## 🚀 脚本说明

### Windows 脚本

| 脚本 | 说明 |
|------|------|
| run-local.bat | 本地开发环境快速启动 |
| start-apps.bat | 启动所有应用服务 |
| start-simple.bat | 启动简单演示 |
| debug-docker.bat | Docker 环境调试 |

### PowerShell 脚本

| 脚本 | 说明 |
|------|------|
| run-local.ps1 | 本地开发环境快速启动（PowerShell 版本） |
| start-local.ps1 | 启动所有应用服务（PowerShell 版本） |
| fix-docker.ps1 | Docker 问题修复脚本 |
| fix-docker-enhanced.ps1 | Docker 增强修复脚本 |

## 📋 快速导航

- **项目概述**: [README.md](../README.md)
- **模块开发**: 各模块目录下的 README.md
- **部署指南**: deployments/ 目录
- **脚本使用**: scripts/ 目录

## 🔄 版本说明

- **主项目版本**: Spring Boot 4.0.2 + JDK 25
- **simple-demo**: Spring Boot 3.2.0 + JDK 17（独立示例）

---

更新时间: 2026-02-08
