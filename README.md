# OpenAdmin Framework 示例应用

OpenAdmin 是一个基于 Spring Boot 4.0.2 和 JDK 25 的现代化企业级应用框架，提供了完整的微服务解决方案，包括API网关、安全认证、配置管理、系统监控等功能。

## 📋 项目概述

本项目包含了三个完整的示例应用，展示了 OpenAdmin 框架的核心功能：

### 🏢 sample-enterprise-app (企业管理系统)
- 用户管理：增删改查、角色分配
- 部门管理：组织架构、权限分配  
- 菜单管理：动态菜单、权限控制

### ⚙️ sample-config-app (配置管理应用)
- 系统参数配置
- 配置热更新
- 配置审计功能

### 📊 sample-monitor-app (监控应用)
- 应用性能监控
- 业务指标监控
- 监控告警系统

## 🚀 快速开始

### 系统要求
- JDK 25+
- Maven 3.8+
- Docker & Docker Compose (可选)

### 方式一：直接运行 (推荐开发)

1. **克隆项目**
```bash
git clone <repository-url>
cd openadmin
```

2. **编译项目**
```bash
mvn clean install -DskipTests
```

3. **启动应用**

分别启动各个应用：

```bash
# 启动企业管理系统 (端口: 8081)
java -jar openadmin-samples/target/openadmin-samples-10.3.0-SNAPSHOT.jar --spring.profiles.active=enterprise

# 启动配置管理应用 (端口: 8082)  
java -jar openadmin-samples/target/openadmin-samples-10.3.0-SNAPSHOT.jar --spring.profiles.active=config

# 启动监控应用 (端口: 8083)
java -jar openadmin-samples/target/openadmin-samples-10.3.0-SNAPSHOT.jar --spring.profiles.active=monitor

# 启动API网关 (端口: 8080)
java -jar openadmin-gateway/target/openadmin-gateway-10.3.0-SNAPSHOT.jar
```

或者使用提供的便捷脚本：
```bash
# Windows
scripts\run-local.bat
scripts\start-apps.bat

# PowerShell
scripts\run-local.ps1
scripts\start-local.ps1
```

### 方式二：Docker Compose 运行 (推荐生产/演示)

```bash
# 构建并启动所有服务
cd deployments
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 停止所有服务
docker-compose down
```

## 🔧 服务端口说明

| 服务 | 端口 | 描述 |
|------|------|------|
| API网关 | 8080 | 统一入口，路由转发 |
| 企业管理系统 | 8081 | 用户、部门、菜单管理 |
| 配置管理应用 | 8082 | 系统配置管理 |
| 监控应用 | 8083 | 性能监控、告警 |
| Nacos | 8848 | 注册配置中心 |
| Prometheus | 9090 | 监控数据收集 |
| Grafana | 3000 | 监控可视化 |
| MySQL | 3306 | 数据库 |
| Redis | 6379 | 缓存 |

## 🎯 功能演示

### 1. 企业管理系统

**API端点：**
```
GET    /api/users              # 获取用户列表
POST   /api/users              # 创建用户
GET    /api/users/{id}         # 获取用户详情
PUT    /api/users/{id}         # 更新用户
DELETE /api/users/{id}         # 删除用户

GET    /api/departments        # 获取部门列表
POST   /api/departments        # 创建部门
GET    /api/menus              # 获取菜单列表
```

**测试示例：**
```bash
# 创建用户
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "fullName": "测试用户",
    "departmentId": 1
  }'

# 获取用户列表
curl http://localhost:8081/api/users
```

### 2. 配置管理应用

**API端点：**
```
GET    /api/configs                    # 获取所有配置
POST   /api/configs                    # 创建配置
GET    /api/configs/{key}              # 获取配置详情
PUT    /api/configs/{key}              # 更新配置
DELETE /api/configs/{key}              # 删除配置
POST   /api/configs/{key}/refresh      # 刷新配置
```

**测试示例：**
```bash
# 创建配置项
curl -X POST http://localhost:8082/api/configs \
  -H "Content-Type: application/json" \
  -d '{
    "configKey": "app.feature.enabled",
    "configValue": "true",
    "description": "功能开关配置"
  }'

# 刷新配置
curl -X POST http://localhost:8082/api/configs/app.feature.enabled/refresh
```

### 3. 监控应用

**API端点：**
```
GET    /api/monitor/performance/overview    # 性能概览
POST   /api/monitor/performance/collect     # 收集性能指标
GET    /api/monitor/business/overview       # 业务指标概览
GET    /api/monitor/alerts/active           # 活跃告警列表
```

**测试示例：**
```bash
# 获取性能概览
curl http://localhost:8083/api/monitor/performance/overview

# 记录自定义指标
curl -X POST "http://localhost:8083/api/monitor/performance/metrics/custom?name=request_count&value=100"

# 获取活跃告警
curl http://localhost:8083/api/monitor/alerts/active
```

## 🔐 安全认证

系统集成了JWT认证，默认用户：

```
用户名: admin
密码: admin123
```

**获取Token：**
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**使用Token访问受保护资源：**
```bash
curl -H "Authorization: Bearer <your-token>" \
  http://localhost:8080/api/users
```

## 📊 监控面板

### Grafana 监控
访问地址：http://localhost:3000
默认账号：admin/admin123

预置仪表板包括：
- 应用性能监控
- JVM指标监控
- HTTP请求统计
- 系统健康状态

### Prometheus 查询
访问地址：http://localhost:9090

常用查询示例：
```
# JVM内存使用
jvm_memory_used_bytes{area="heap"}

# HTTP请求数
rate(http_server_requests_seconds_count[1m])

# 系统在线状态
up
```

## 🛠️ 开发指南

### 项目结构
```
openadmin/
├── openadmin-admin/          # 管理后台模块
├── openadmin-core/           # 核心功能模块
├── openadmin-gateway/        # API网关模块
├── openadmin-security/       # 安全认证模块
├── openadmin-config/         # 配置管理模块
├── openadmin-monitor/        # 监控模块
├── openadmin-starter/        # 自动配置模块
├── openadmin-samples/        # 综合示例应用
├── examples/                 # 独立示例项目
│   └── simple-demo/         # 简单演示项目
├── scripts/                  # 运行脚本
│   ├── run-local.bat        # 本地运行脚本
│   ├── start-apps.bat       # 启动所有应用
│   └── ...
├── deployments/              # 部署相关配置
│   ├── dockerfiles/         # Docker镜像定义
│   ├── docker/              # Docker辅助配置
│   ├── configs/             # 项目配置文件
│   ├── docker-compose.yml   # 完整编排文件
│   └── docker-compose-simple.yml # 简化编排文件
└── pom.xml                  # Maven主配置
```

### 自定义配置

**应用配置文件位置：**
- `openadmin-samples/src/main/resources/application-*.yml`

**修改端口：**
```yaml
server:
  port: 8081  # 修改为所需端口
```

**数据库配置：**
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_database
    username: your_username
    password: your_password
```

### 扩展开发

1. **添加新的监控指标：**
```java
@Autowired
private PerformanceMonitoringService performanceService;

// 记录自定义指标
Map<String, String> tags = new HashMap<>();
tags.put("service", "order");
performanceService.collectCustomMetric("order_count", 1.0, tags);
```

2. **添加新的业务指标：**
```java
@Autowired
private BusinessMonitoringService businessService;

// 记录业务操作
businessService.recordBusinessOperation("order_created", 1.0, "count");
```

## 🧪 测试验证

### 单元测试
```bash
# 运行所有测试
mvn test

# 运行特定模块测试
mvn test -pl openadmin-gateway
```

### 集成测试
```bash
# 运行集成测试
mvn verify -Pintegration-test
```

### 健康检查
```bash
# 检查各服务健康状态
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
```

## 📈 性能优化

### 虚拟线程配置
项目已启用虚拟线程优化：
```yaml
spring:
  threads:
    virtual:
      enabled: true
```

### 连接池优化
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
```

## 🐛 故障排除

### 常见问题

1. **端口冲突**
   ```
   错误：Address already in use
   解决：修改 application.yml 中的 server.port 配置
   ```

2. **数据库连接失败**
   ```
   错误：Cannot connect to database
   解决：检查数据库服务是否启动，连接配置是否正确
   ```

3. **JWT认证失败**
   ```
   错误：401 Unauthorized
   解决：确保使用正确的token，检查token是否过期
   ```

### 日志查看
```bash
# 查看应用日志
tail -f logs/application.log

# Docker环境下查看日志
docker-compose logs -f enterprise-app
```

## 📄 许可证

MIT License

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📞 支持

如有问题，请联系：support@openadmin.com