package com.qoobot.openadmin.gateway.filter;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.*;

/**
 * 认证网关过滤器
 * 实现JWT Token验证、权限控制、黑名单检查等功能
 */
@Slf4j
@Component
public class AuthenticationGatewayFilter implements GlobalFilter, Ordered {

    @Value("${gateway.jwt.secret:openadmin-secret-key-for-jwt-token-generation}")
    private String jwtSecret;

    @Value("${gateway.jwt.expiration:3600}")
    private Long jwtExpiration;

    @Value("${gateway.auth.enabled:true}")
    private Boolean authEnabled;

    // 白名单路径，不需要认证
    private static final Set<String> WHITE_LIST_PATHS = Set.of(
            "/auth/login",
            "/auth/register",
            "/health",
            "/actuator/**",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    );

    // 黑名单Token集合（可以从Redis或数据库加载）
    private static final Set<String> BLACKLIST_TOKENS = Collections.synchronizedSet(new HashSet<>());

    private SecretKey secretKey;

    public AuthenticationGatewayFilter() {
        // 初始化JWT密钥
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 如果认证功能未启用，直接放行
        if (!authEnabled) {
            log.debug("Authentication disabled, allowing request: {}", request.getURI());
            return chain.filter(exchange);
        }

        String path = request.getURI().getPath();

        // 检查白名单路径
        if (isWhitelisted(path)) {
            log.debug("Whitelisted path, allowing request: {}", path);
            return chain.filter(exchange);
        }

        // 获取Authorization头
        String authorizationHeader = request.getHeaders().getFirst("Authorization");
        
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for path: {}", path);
            return unauthorizedResponse(response, "Missing or invalid Authorization header");
        }

        String token = authorizationHeader.substring(7);

        // 检查Token是否在黑名单中
        if (BLACKLIST_TOKENS.contains(token)) {
            log.warn("Token is blacklisted: {}", maskToken(token));
            return unauthorizedResponse(response, "Token has been revoked");
        }

        try {
            // 验证JWT Token
            Claims claims = validateToken(token);
            
            // 将用户信息添加到请求头中供下游服务使用
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-ID", claims.getSubject())
                    .header("X-User-Roles", String.join(",", getRolesFromClaims(claims)))
                    .header("X-Authenticated-Token", token)
                    .build();

            ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
            
            log.debug("Authentication successful for user: {}, path: {}", claims.getSubject(), path);
            return chain.filter(mutatedExchange);

        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token for path: {}, error: {}", path, e.getMessage());
            return unauthorizedResponse(response, "Invalid or expired token");
        }
    }

    /**
     * 验证JWT Token
     */
    private Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从Claims中提取角色信息
     */
    @SuppressWarnings("unchecked")
    private List<String> getRolesFromClaims(Claims claims) {
        Object rolesObj = claims.get("roles");
        if (rolesObj instanceof List) {
            return (List<String>) rolesObj;
        }
        return Collections.emptyList();
    }

    /**
     * 检查路径是否在白名单中
     */
    private boolean isWhitelisted(String path) {
        return WHITE_LIST_PATHS.stream().anyMatch(pattern -> 
            path.matches(pattern.replace("**", ".*").replace("*", "[^/]*"))
        );
    }

    /**
     * 返回未授权响应
     */
    private Mono<Void> unauthorizedResponse(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        
        String body = String.format("{\"error\":\"Unauthorized\",\"message\":\"%s\"}", message);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    /**
     * 掩盖Token用于日志输出
     */
    private String maskToken(String token) {
        if (token == null || token.length() <= 10) {
            return "***";
        }
        return token.substring(0, 5) + "..." + token.substring(token.length() - 5);
    }

    /**
     * 将Token加入黑名单
     */
    public void addToBlacklist(String token) {
        BLACKLIST_TOKENS.add(token);
        log.info("Token added to blacklist: {}", maskToken(token));
    }

    /**
     * 从黑名单中移除Token
     */
    public void removeFromBlacklist(String token) {
        BLACKLIST_TOKENS.remove(token);
        log.info("Token removed from blacklist: {}", maskToken(token));
    }

    /**
     * 清空黑名单
     */
    public void clearBlacklist() {
        BLACKLIST_TOKENS.clear();
        log.info("Blacklist cleared");
    }

    /**
     * 获取黑名单大小
     */
    public int getBlacklistSize() {
        return BLACKLIST_TOKENS.size();
    }

    @Override
    public int getOrder() {
        return -100; // 高优先级，确保在其他过滤器之前执行
    }
}