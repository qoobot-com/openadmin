# OpenAdmin Spring Boot Starter

## 概述

OpenAdmin Spring Boot Starter 是一个符合 Spring Boot Starter 最佳实践的自动配置模块，为 OpenAdmin 微服务治理框架提供一键式的自动化配置能力。

## 核心特性

### 1. 自动装配
- ✅ 检测类路径依赖，自动配置相应功能
- ✅ 支持 `@EnableOpenAdmin` 注解启用框架
- ✅ 配置属性自动绑定 (`openadmin.*`)

### 2. 条件装配
- ✅ `@ConditionalOnClass`：根据类路径存在性装配
- ✅ `@ConditionalOnProperty`：根据配置属性装配
- ✅ `@ConditionalOnMissingBean`：防止重复装配

### 3. 模块化配置
- ✅ 安全模块自动配置
- ✅ 监控模块自动配置
- ✅ 网关模块自动配置
- ✅ 配置中心自动配置
- ✅ 管理后台自动配置

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.qoobot</groupId>
    <artifactId>openadmin-spring-boot-starter</artifactId>
    <version>10.3.0-SNAPSHOT</version>
</dependency>
```

### 2. 启用自动配置

#### 方式一：通过配置文件启用（推荐）

```yaml
openadmin:
  enabled: true
  security:
    enabled: true
    auth-type: jwt
  monitoring:
    enabled: true
    log-level: INFO
  gateway:
    enabled: false
  config:
    enabled: true
  admin:
    enabled: true
```

#### 方式二：通过注解启用

```java
@SpringBootApplication
@EnableOpenAdmin(
    security = true,
    monitoring = true,
    gateway = false,
    config = true,
    admin = true
)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

## 配置属性详解

### 基础配置

```yaml
openadmin:
  enabled: true  # 是否启用 OpenAdmin 框架
```

### 安全配置

```yaml
openadmin:
  security:
    enabled: true
    auth-type: jwt  # 认证方式：jwt, oauth2, form
    token-expire-time: 3600  # Token 过期时间（秒）
    refresh-token-expire-time: 2592000  # 刷新 Token 过期时间（秒）
    csrf-enabled: true  # 是否启用 CSRF 保护
    xss-enabled: true   # 是否启用 XSS 防护
    password-policy: "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,}$"  # 密码强度要求
    
    jwt:
      secret: "your-jwt-secret"  # JWT 密钥
      issuer: "openadmin"        # Token 签发者
      audience: "users"          # Token 受众
      enabled: true              # 是否启用 JWT
    
    oauth2:
      enabled: false             # 是否启用 OAuth2
      authorization-server-url: "http://localhost:8080/auth"
      client-id: "client"
      client-secret: "secret"
      redirect-uri: "http://localhost:8080/callback"
```

### 监控配置

```yaml
openadmin:
  monitoring:
    enabled: true
    log-level: INFO
    metrics-enabled: true      # 是否启用指标收集
    health-enabled: true       # 是否启用健康检查
    audit-enabled: true        # 是否启用审计日志
    audit-retention-days: 30   # 审计日志保留天数
    sampling-rate: 0.1         # 性能监控采样率
    slow-query-threshold: 1000 # 慢查询阈值（毫秒）
```

### 网关配置

```yaml
openadmin:
  gateway:
    enabled: false
    port: 9090
    default-route: "lb://default-service"
    rate-limit-enabled: true
    global-rate-limit: 1000    # 全局限流请求数
    ip-rate-limit: 100         # IP 限流请求数
    user-rate-limit: 200       # 用户限流请求数
    circuit-breaker-enabled: true
    failure-rate-threshold: 50 # 熔断器失败率阈值
    wait-duration-in-open-state: 30000  # 熔断器等待时间（毫秒）
```

### 配置中心配置

```yaml
openadmin:
  config:
    enabled: true
    server-addr: "localhost:8848"  # 配置服务器地址
    namespace: "openadmin"         # 命名空间
    group: "DEFAULT_GROUP"         # 配置组
    file-extension: "yaml"         # 配置文件扩展名
    local-cache-enabled: true      # 是否启用本地缓存
    local-cache-path: "./config-cache"  # 本地缓存路径
```

### 管理后台配置

```yaml
openadmin:
  admin:
    enabled: true
    port: 8080
    context-path: "/admin"
    thymeleaf-enabled: true
    swagger-enabled: true
    default-username: "admin"
    default-password: "admin123"
```

## 条件装配说明

### 类路径条件

```java
// 只有当安全模块在类路径中时才装配
@ConditionalOnClass(name = "com.qoobot.openadmin.security.SecurityApplication")
```

### 属性条件

```java
// 只有当配置属性为 true 时才装配
@ConditionalOnProperty(prefix = "openadmin.security", name = "enabled", havingValue = "true")
```

### Bean 条件

```java
// 只有当容器中不存在该 Bean 时才创建
@ConditionalOnMissingBean
```

## 测试验证

运行单元测试验证自动配置功能：

```bash
mvn test -pl openadmin-starter
```

测试覆盖场景：
- ✅ 默认自动配置
- ✅ 禁用自动配置
- ✅ 各模块配置属性绑定
- ✅ 条件装配逻辑
- ✅ 注解驱动配置
- ✅ 属性默认值回退

## 最佳实践

### 1. 生产环境配置

```yaml
openadmin:
  enabled: true
  security:
    jwt:
      secret: "${JWT_SECRET:change-me-in-production}"  # 使用环境变量
    password-policy: "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{12,}$"
  monitoring:
    log-level: WARN
    audit-retention-days: 90
  config:
    server-addr: "${CONFIG_SERVER_ADDR:nacos-server:8848}"
```

### 2. 开发环境配置

```yaml
openadmin:
  enabled: true
  security:
    jwt:
      secret: "dev-secret-key"  # 开发环境使用简单密钥
  monitoring:
    log-level: DEBUG
    sampling-rate: 1.0  # 100% 采样
  gateway:
    enabled: true
    port: 9090
```

### 3. 模块选择性启用

```java
// 只启用安全和监控模块
@EnableOpenAdmin(
    security = true,
    monitoring = true,
    gateway = false,
    config = false,
    admin = false
)
```

## 故障排除

### 常见问题

1. **自动配置未生效**
   - 检查 `openadmin.enabled` 是否设置为 `true`
   - 确认 starter 依赖已正确添加
   - 验证 `@EnableAutoConfiguration` 或 `@SpringBootApplication` 是否存在

2. **模块配置不生效**
   - 检查对应模块是否在类路径中
   - 确认模块的 `enabled` 属性设置为 `true`
   - 查看日志确认条件装配是否满足

3. **属性绑定失败**
   - 验证配置文件格式是否正确
   - 检查属性名称是否匹配
   - 确认属性类型是否正确

### 日志调试

```yaml
logging:
  level:
    com.qoobot.openadmin.starter: DEBUG
    org.springframework.boot.autoconfigure: DEBUG
```

## 版本兼容性

- Spring Boot: 3.2+
- Java: 17+
- OpenAdmin Modules: 10.3.0-SNAPSHOT

## 贡献指南

欢迎提交 Issue 和 Pull Request 来改进这个 Starter 模块！

## 许可证

Apache License 2.0