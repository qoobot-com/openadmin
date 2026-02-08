package com.qoobot.openadmin.gateway.filter;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 限流网关过滤器
 * 使用Resilience4j实现基于令牌桶算法的限流功能
 */
@Slf4j
@Component
public class RateLimitGatewayFilter implements GlobalFilter, Ordered {

    @Value("${gateway.rate-limit.enabled:true}")
    private Boolean rateLimitEnabled;

    @Value("${gateway.rate-limit.global-permits:1000}")
    private Integer globalPermits;

    @Value("${gateway.rate-limit.global-period:1}")
    private Integer globalPeriodSeconds;

    @Value("${gateway.rate-limit.ip-permits:100}")
    private Integer ipPermits;

    @Value("${gateway.rate-limit.ip-period:60}")
    private Integer ipPeriodSeconds;

    @Value("${gateway.rate-limit.user-permits:200}")
    private Integer userPermits;

    @Value("${gateway.rate-limit.user-period:60}")
    private Integer userPeriodSeconds;

    // 全局限流器
    private RateLimiter globalRateLimiter;

    // IP限流器缓存
    private final Map<String, RateLimiter> ipRateLimiters = new ConcurrentHashMap<>();

    // 用户限流器缓存
    private final Map<String, RateLimiter> userRateLimiters = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // 初始化全局限流器
        RateLimiterConfig globalConfig = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofSeconds(globalPeriodSeconds))
                .limitForPeriod(globalPermits)
                .timeoutDuration(Duration.ofMillis(100))
                .build();
        
        globalRateLimiter = RateLimiter.of("global-rate-limiter", globalConfig);
        log.info("Global rate limiter initialized: {} permits per {} seconds", globalPermits, globalPeriodSeconds);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!rateLimitEnabled) {
            return chain.filter(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String clientIp = getClientIpAddress(request);
        String userId = getUserId(request);

        try {
            // 应用全局限流
            Mono<Void> globalLimitedChain = chain.filter(exchange)
                    .transform(RateLimiterOperator.of(globalRateLimiter));

            // 应用IP限流
            RateLimiter ipRateLimiter = getOrCreateIpRateLimiter(clientIp);
            Mono<Void> ipLimitedChain = globalLimitedChain
                    .transform(RateLimiterOperator.of(ipRateLimiter));

            // 应用用户限流（如果有用户信息）
            if (userId != null && !userId.isEmpty()) {
                RateLimiter userRateLimiter = getOrCreateUserRateLimiter(userId);
                return ipLimitedChain
                        .transform(RateLimiterOperator.of(userRateLimiter))
                        .onErrorResume(RequestNotPermitted.class, 
                            e -> handleRateLimitExceeded(response, "User rate limit exceeded"));
            }

            return ipLimitedChain
                    .onErrorResume(RequestNotPermitted.class, 
                        e -> handleRateLimitExceeded(response, "Rate limit exceeded"));

        } catch (RequestNotPermitted e) {
            return handleRateLimitExceeded(response, "Rate limit exceeded");
        }
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddress() != null ? 
               request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
    }

    /**
     * 从请求中获取用户ID
     */
    private String getUserId(ServerHttpRequest request) {
        return request.getHeaders().getFirst("X-User-ID");
    }

    /**
     * 获取或创建IP限流器
     */
    private RateLimiter getOrCreateIpRateLimiter(String clientIp) {
        return ipRateLimiters.computeIfAbsent(clientIp, ip -> {
            RateLimiterConfig config = RateLimiterConfig.custom()
                    .limitRefreshPeriod(Duration.ofSeconds(ipPeriodSeconds))
                    .limitForPeriod(ipPermits)
                    .timeoutDuration(Duration.ofMillis(50))
                    .build();
            
            RateLimiter rateLimiter = RateLimiter.of("ip-" + ip, config);
            log.debug("Created rate limiter for IP: {}", ip);
            return rateLimiter;
        });
    }

    /**
     * 获取或创建用户限流器
     */
    private RateLimiter getOrCreateUserRateLimiter(String userId) {
        return userRateLimiters.computeIfAbsent(userId, user -> {
            RateLimiterConfig config = RateLimiterConfig.custom()
                    .limitRefreshPeriod(Duration.ofSeconds(userPeriodSeconds))
                    .limitForPeriod(userPermits)
                    .timeoutDuration(Duration.ofMillis(100))
                    .build();
            
            RateLimiter rateLimiter = RateLimiter.of("user-" + user, config);
            log.debug("Created rate limiter for user: {}", user);
            return rateLimiter;
        });
    }

    /**
     * 处理限流超限情况
     */
    private Mono<Void> handleRateLimitExceeded(ServerHttpResponse response, String message) {
        log.warn("Rate limit exceeded: {}", message);
        
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        response.getHeaders().add("X-Rate-Limit-Exceeded", "true");
        
        String body = String.format("""
                {
                  "error": "Rate Limit Exceeded",
                  "message": "%s",
                  "timestamp": "%s"
                }
                """, message, System.currentTimeMillis());
        
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    /**
     * 获取指定IP的限流统计信息
     */
    public Map<String, Object> getIpRateLimitStats(String ip) {
        RateLimiter rateLimiter = ipRateLimiters.get(ip);
        if (rateLimiter == null) {
            return Map.of("ip", ip, "exists", false);
        }
        
        RateLimiter.Metrics metrics = rateLimiter.getMetrics();
        return Map.of(
            "ip", ip,
            "availablePermissions", metrics.getAvailablePermissions(),
            "numberOfWaitingThreads", metrics.getNumberOfWaitingThreads(),
            "exists", true
        );
    }

    /**
     * 获取指定用户的限流统计信息
     */
    public Map<String, Object> getUserRateLimitStats(String userId) {
        RateLimiter rateLimiter = userRateLimiters.get(userId);
        if (rateLimiter == null) {
            return Map.of("userId", userId, "exists", false);
        }
        
        RateLimiter.Metrics metrics = rateLimiter.getMetrics();
        return Map.of(
            "userId", userId,
            "availablePermissions", metrics.getAvailablePermissions(),
            "numberOfWaitingThreads", metrics.getNumberOfWaitingThreads(),
            "exists", true
        );
    }

    /**
     * 获取全局限流统计信息
     */
    public Map<String, Object> getGlobalRateLimitStats() {
        RateLimiter.Metrics metrics = globalRateLimiter.getMetrics();
        return Map.of(
            "availablePermissions", metrics.getAvailablePermissions(),
            "numberOfWaitingThreads", metrics.getNumberOfWaitingThreads()
        );
    }

    /**
     * 清除IP限流器缓存
     */
    public void clearIpRateLimiters() {
        int size = ipRateLimiters.size();
        ipRateLimiters.clear();
        log.info("Cleared {} IP rate limiters", size);
    }

    /**
     * 清除用户限流器缓存
     */
    public void clearUserRateLimiters() {
        int size = userRateLimiters.size();
        userRateLimiters.clear();
        log.info("Cleared {} user rate limiters", size);
    }

    @Override
    public int getOrder() {
        return -50; // 在认证过滤器之后，其他业务过滤器之前
    }
}