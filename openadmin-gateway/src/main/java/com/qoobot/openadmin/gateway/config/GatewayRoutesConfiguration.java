package com.qoobot.openadmin.gateway.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * 网关路由配置类
 * 支持动态路由配置、负载均衡、权限控制等功能
 */
@Slf4j
@Configuration
public class GatewayRoutesConfiguration {

    @Autowired
    private Environment environment;

    /**
     * 自定义路由定位器
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // 管理后台路由
                .route("admin-service", r -> r.path("/admin/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .retry(3)
                        )
                        .uri("lb://admin-service"))
                
                // 安全服务路由
                .route("security-service", r -> r.path("/security/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .retry(2)
                        )
                        .uri("lb://security-service"))
                
                // 配置服务路由
                .route("config-service", r -> r.path("/config/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://config-service"))
                
                // 监控服务路由
                .route("monitor-service", r -> r.path("/monitor/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://monitor-service"))
                
                // 网关管理路由
                .route("gateway-management", r -> r.path("/gateway/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://gateway-service"))
                
                // 默认路由 - 转发到默认服务
                .route("default-route", r -> r.path("/**")
                        .uri("lb://default-service"))
                
                .build();
    }

    /**
     * 路由配置实体类
     */
    @Data
    public static class RouteConfig {
        private String id;
        private String path;
        private String uri;
        private List<String> filters;
        private Integer retryCount = 3;
        private Boolean enabled = true;
        private String description;
    }

    /**
     * 负载均衡配置
     */
    @Data
    public static class LoadBalancerConfig {
        private String algorithm = "ROUND_ROBIN"; // ROUND_ROBIN, RANDOM, WEIGHTED_RESPONSE_TIME
        private Integer maxRetries = 3;
        private Long retryTimeoutMs = 3000L;
        private Boolean enableCircuitBreaker = true;
    }

    /**
     * 权限控制配置
     */
    @Data
    public static class AuthorizationConfig {
        private Boolean enabled = true;
        private List<String> whiteListPaths;
        private String jwtSecret;
        private Long tokenExpireTime = 3600L; // 秒
    }
}