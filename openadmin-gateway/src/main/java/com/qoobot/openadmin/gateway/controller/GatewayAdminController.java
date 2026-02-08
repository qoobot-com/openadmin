package com.qoobot.openadmin.gateway.controller;

import com.qoobot.openadmin.gateway.config.GatewayRoutesConfiguration;
import com.qoobot.openadmin.gateway.filter.AuthenticationGatewayFilter;
import com.qoobot.openadmin.gateway.filter.RateLimitGatewayFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import reactor.core.publisher.Mono;

/**
 * 网关管理控制器
 * 提供网关配置管理、监控统计、路由管理等功能
 */
@Slf4j
@RestController
@RequestMapping("/gateway")
@RequiredArgsConstructor
public class GatewayAdminController {

    @Autowired
    private final RouteDefinitionLocator routeDefinitionLocator;
    
    @Autowired
    private final RouteDefinitionWriter routeDefinitionWriter;
    
    @Autowired
    private final AuthenticationGatewayFilter authenticationFilter;
    
    @Autowired
    private final RateLimitGatewayFilter rateLimitFilter;

    /**
     * 获取网关状态信息
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getGatewayStatus() {
        Map<String, Object> status = new HashMap<>();
        
        status.put("timestamp", System.currentTimeMillis());
        status.put("authenticationEnabled", true);
        status.put("rateLimitEnabled", true);
        status.put("blacklistSize", authenticationFilter.getBlacklistSize());
        
        // 添加路由信息
        List<RouteDefinition> routes = routeDefinitionLocator.getRouteDefinitions()
                .collectList().block();
        if (routes != null) {
            status.put("routeCount", routes.size());
            status.put("routes", routes.stream()
                    .map(route -> Map.of(
                        "id", route.getId(),
                        "uri", route.getUri().toString(),
                        "predicates", route.getPredicates().stream()
                                .map(p -> p.getName() + ": " + p.getArgs())
                                .collect(Collectors.toList())
                    ))
                    .collect(Collectors.toList()));
        }
        
        return ResponseEntity.ok(status);
    }

    /**
     * 获取认证统计信息
     */
    @GetMapping("/auth/stats")
    public ResponseEntity<Map<String, Object>> getAuthStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("blacklistSize", authenticationFilter.getBlacklistSize());
        stats.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(stats);
    }

    /**
     * 将Token加入黑名单
     */
    @PostMapping("/auth/blacklist")
    public ResponseEntity<Map<String, Object>> addToBlacklist(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Token is required"));
        }
        
        authenticationFilter.addToBlacklist(token);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Token added to blacklist");
        result.put("blacklistSize", authenticationFilter.getBlacklistSize());
        
        return ResponseEntity.ok(result);
    }

    /**
     * 从黑名单中移除Token
     */
    @DeleteMapping("/auth/blacklist/{token}")
    public ResponseEntity<Map<String, Object>> removeFromBlacklist(@PathVariable String token) {
        authenticationFilter.removeFromBlacklist(token);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Token removed from blacklist");
        result.put("blacklistSize", authenticationFilter.getBlacklistSize());
        
        return ResponseEntity.ok(result);
    }

    /**
     * 清空黑名单
     */
    @DeleteMapping("/auth/blacklist")
    public ResponseEntity<Map<String, Object>> clearBlacklist() {
        authenticationFilter.clearBlacklist();
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Blacklist cleared");
        result.put("blacklistSize", 0);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 获取限流统计信息
     */
    @GetMapping("/ratelimit/stats")
    public ResponseEntity<Map<String, Object>> getRateLimitStats(
            @RequestParam(required = false) String ip,
            @RequestParam(required = false) String userId) {
        
        Map<String, Object> stats = new HashMap<>();
        
        if (ip != null) {
            stats.put("ipStats", rateLimitFilter.getIpRateLimitStats(ip));
        }
        
        if (userId != null) {
            stats.put("userStats", rateLimitFilter.getUserRateLimitStats(userId));
        }
        
        stats.put("globalStats", rateLimitFilter.getGlobalRateLimitStats());
        stats.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(stats);
    }

    /**
     * 清除限流器缓存
     */
    @DeleteMapping("/ratelimit/cache")
    public ResponseEntity<Map<String, Object>> clearRateLimitCache(
            @RequestParam(defaultValue = "false") boolean clearIp,
            @RequestParam(defaultValue = "false") boolean clearUser) {
        
        Map<String, Object> result = new HashMap<>();
        
        if (clearIp) {
            rateLimitFilter.clearIpRateLimiters();
            result.put("ipCacheCleared", true);
        }
        
        if (clearUser) {
            rateLimitFilter.clearUserRateLimiters();
            result.put("userCacheCleared", true);
        }
        
        result.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(result);
    }

    /**
     * 动态添加路由
     */
    @PostMapping("/routes")
    public ResponseEntity<Map<String, Object>> addRoute(@RequestBody GatewayRoutesConfiguration.RouteConfig routeConfig) {
        try {
            RouteDefinition routeDefinition = new RouteDefinition();
            routeDefinition.setId(routeConfig.getId());
            routeDefinition.setUri(java.net.URI.create(routeConfig.getUri()));
            
            // 设置谓词
            routeDefinition.setPredicates(List.of(
                new org.springframework.cloud.gateway.handler.predicate.PredicateDefinition("Path=" + routeConfig.getPath())
            ));
            
            routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Route added successfully");
            result.put("routeId", routeConfig.getId());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Failed to add route: {}", routeConfig.getId(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    /**
     * 删除路由
     */
    @DeleteMapping("/routes/{routeId}")
    public ResponseEntity<Map<String, Object>> deleteRoute(@PathVariable String routeId) {
        try {
            routeDefinitionWriter.delete(Mono.just(routeId)).subscribe();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Route deleted successfully");
            result.put("routeId", routeId);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Failed to delete route: {}", routeId, e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    /**
     * 获取所有路由配置
     */
    @GetMapping("/routes")
    public ResponseEntity<List<Map<String, Object>>> getAllRoutes() {
        List<RouteDefinition> routes = routeDefinitionLocator.getRouteDefinitions()
                .collectList().block();
        
        if (routes == null) {
            return ResponseEntity.ok(List.of());
        }
        
        List<Map<String, Object>> routeList = routes.stream()
                .map(route -> {
                    Map<String, Object> routeInfo = new HashMap<>();
                    routeInfo.put("id", route.getId());
                    routeInfo.put("uri", route.getUri().toString());
                    routeInfo.put("predicates", route.getPredicates());
                    routeInfo.put("filters", route.getFilters());
                    return routeInfo;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(routeList);
    }

    /**
     * 健康检查端点
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("component", "gateway");
        return ResponseEntity.ok(health);
    }

    /**
     * 网关监控指标
     */
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("timestamp", System.currentTimeMillis());
        metrics.put("uptime", System.currentTimeMillis() - getStartupTime());
        
        // 这里可以集成Micrometer或其他监控系统
        metrics.put("memory", getMemoryInfo());
        metrics.put("threads", getThreadInfo());
        
        return ResponseEntity.ok(metrics);
    }

    private long getStartupTime() {
        return System.currentTimeMillis() - 3600000; // 模拟启动时间
    }

    private Map<String, Object> getMemoryInfo() {
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> memory = new HashMap<>();
        memory.put("total", runtime.totalMemory());
        memory.put("free", runtime.freeMemory());
        memory.put("max", runtime.maxMemory());
        memory.put("used", runtime.totalMemory() - runtime.freeMemory());
        return memory;
    }

    private Map<String, Object> getThreadInfo() {
        Map<String, Object> threads = new HashMap<>();
        threads.put("active", Thread.activeCount());
        threads.put("daemon", java.lang.management.ManagementFactory.getThreadMXBean().getDaemonThreadCount());
        return threads;
    }
}