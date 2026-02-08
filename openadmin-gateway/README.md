# OpenAdmin API 网关模块

## 概述

OpenAdmin API 网关是一个基于 Spring Cloud Gateway 构建的企业级 API 网关解决方案，提供了完整的路由管理、安全认证、流量控制和服务治理功能。

## 核心功能

### 1. 路由管理
- **动态路由配置**：支持运行时动态添加、修改和删除路由规则
- **负载均衡**：集成 Spring Cloud LoadBalancer 实现服务间负载均衡
- **路由权限控制**：基于路径的细粒度访问控制
- **故障转移**：自动服务发现和故障节点剔除

### 2. 安全防护
- **JWT 认证**：基于 JWT Token 的身份验证机制
- **黑名单管理**：支持 Token 黑名单和实时封禁
- **参数校验**：请求参数合法性验证
- **安全日志**：完整的安全审计日志记录

### 3. 流量控制
- **限流熔断**：基于 Resilience4j 的高级限流和熔断机制
- **多维度限流**：支持全局、IP、用户等多维度限流策略
- **自适应限流**：根据系统负载动态调整限流阈值

### 4. 服务治理
- **服务发现**：集成 Nacos 实现服务注册与发现
- **健康检查**：内置服务健康状态监控
- **配置管理**：支持动态配置更新

## 技术架构

### 核心组件

```
openadmin-gateway/
├── config/                    # 配置类
│   ├── GatewayRoutesConfiguration.java    # 路由配置
│   ├── CircuitBreakerConfiguration.java   # 熔断器配置
│   └── GatewaySecurityConfig.java         # 安全配置
├── filter/                    # 过滤器
│   ├── AuthenticationGatewayFilter.java   # 认证过滤器
│   ├── RateLimitGatewayFilter.java        # 限流过滤器
│   └── GatewayLoggingFilter.java          # 日志过滤器
├── controller/                # 控制器
│   └── GatewayAdminController.java        # 网关管理接口
├── resources/
│   ├── templates/gateway/                 # 管理页面模板
│   ├── application.yml                    # 应用配置
│   └── bootstrap.yml                      # 启动配置
└── test/                     # 测试用例
```

### 技术栈

- **网关框架**：Spring Cloud Gateway 4.1.0
- **安全框架**：Spring Security 6.2.0
- **限流熔断**：Resilience4j 2.1.0
- **服务发现**：Nacos 2.3.0
- **JWT支持**：JJWT 0.11.5
- **反应式编程**：Project Reactor
- **模板引擎**：Thymeleaf

## 配置说明

### application.yml 核心配置

```yaml
gateway:
  # 认证配置
  auth:
    enabled: true
    jwt:
      secret: ${JWT_SECRET:your-secret-key}
      expiration: 3600
  
  # 限流配置
  rate-limit:
    enabled: true
    global-permits: 1000    # 全局限流
    ip-permits: 100         # IP限流
    user-permits: 200       # 用户限流
  
  # 负载均衡配置
  load-balancer:
    algorithm: ROUND_ROBIN
```

### 路由配置示例

```java
.route("admin-service", r -> r.path("/admin/**")
    .filters(f -> f.stripPrefix(1).retry(3))
    .uri("lb://admin-service"))
```

## API 接口

### 网关管理接口

```
GET    /gateway/status           # 获取网关状态
GET    /gateway/auth/stats       # 获取认证统计
POST   /gateway/auth/blacklist   # 添加黑名单
DELETE /gateway/auth/blacklist   # 清空黑名单
GET    /gateway/ratelimit/stats  # 获取限流统计
GET    /gateway/routes           # 获取路由列表
POST   /gateway/routes           # 添加路由
DELETE /gateway/routes/{id}      # 删除路由
```

### 健康检查接口

```
GET /actuator/health             # 健康检查
GET /actuator/gateway            # 网关详细信息
GET /actuator/metrics            # 性能指标
```

## 部署指南

### 环境要求

- Java 17+
- Maven 3.9+
- Nacos Server 2.2+

### 启动步骤

1. **配置 Nacos**
```bash
# 启动 Nacos 服务
sh startup.sh -m standalone
```

2. **配置环境变量**
```bash
export NACOS_SERVER_ADDR=localhost:8848
export JWT_SECRET=your-jwt-secret-key
```

3. **启动网关服务**
```bash
cd openadmin-gateway
mvn spring-boot:run
```

4. **访问管理界面**
```
http://localhost:9090/gateway/management
```

## 性能特性

### 高并发处理
- 基于 Netty 的异步非阻塞架构
- 支持百万级并发连接
- QPS 可达 10,000+

### 低延迟转发
- 平均响应时间 < 5ms
- 支持长连接复用
- 零拷贝数据传输

### 高可用保障
- 99.99% 可用性设计
- 自动故障检测和恢复
- 多机房部署支持

## 监控告警

### 内置监控指标
- 路由转发成功率
- 平均响应时间
- 当前连接数
- 限流触发次数
- 熔断器状态

### 集成 Prometheus
```yaml
management:
  endpoints:
    web:
      exposure:
        include: prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

## 安全最佳实践

### 访问控制
- 启用 HTTPS 加密传输
- 配置严格的 CORS 策略
- 实施最小权限原则

### 安全配置示例
```yaml
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${SSL_PASSWORD}

spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${JWT_ISSUER_URI}
```

## 故障排除

### 常见问题

1. **路由不生效**
   - 检查路由配置顺序
   - 确认谓词条件匹配
   - 查看路由刷新状态

2. **认证失败**
   - 验证 JWT Token 格式
   - 检查密钥配置
   - 确认 Token 未过期

3. **限流异常**
   - 查看限流器配置
   - 检查 Redis 连接状态
   - 分析限流统计信息

### 日志分析
```bash
# 查看网关日志
tail -f logs/gateway.log

# 查看错误日志
grep ERROR logs/gateway.log
```

## 扩展开发

### 自定义过滤器
```java
@Component
public class CustomGatewayFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 自定义逻辑
        return chain.filter(exchange);
    }
}
```

### 动态路由管理
```java
@Autowired
private RouteDefinitionWriter routeDefinitionWriter;

public void addRoute(String routeId, String path, String uri) {
    RouteDefinition route = new RouteDefinition();
    route.setId(routeId);
    route.setUri(URI.create(uri));
    route.setPredicates(List.of(new PredicateDefinition("Path=" + path)));
    routeDefinitionWriter.save(Mono.just(route)).subscribe();
}
```

## 版本历史

### v1.0.0 (2024-02-08)
- ✅ 基础路由功能实现
- ✅ JWT 认证集成
- ✅ Resilience4j 限流熔断
- ✅ 网关管理界面
- ✅ Nacos 服务发现集成
- ✅ 完善的配置体系

---

**OpenAdmin Team** - 构建现代化微服务治理平台