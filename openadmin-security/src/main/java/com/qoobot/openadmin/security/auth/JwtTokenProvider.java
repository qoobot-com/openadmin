package com.qoobot.openadmin.security.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JWT工具类
 * 提供JWT Token的生成、验证、解析等功能
 */
@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret:mySecretKeyForJWTTokenGenerationWhichShouldBe32Characters}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:86400000}") // 24小时
    private long jwtExpiration;

    @Value("${app.jwt.refresh-expiration:604800000}") // 7天
    private long refreshExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * 生成访问令牌
     */
    public String generateAccessToken(Authentication authentication) {
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("authorities", authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        claims.put("type", "access");

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(String username) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("type", "refresh");

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpiration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 验证JWT Token
     */
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            
            // 检查token是否过期
            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从Token中提取用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (JwtException e) {
            log.error("Failed to extract username from token: {}", e.getMessage());
            throw new BadCredentialsException("Invalid token");
        }
    }

    /**
     * 从Token中提取权限
     */
    @SuppressWarnings("unchecked")
    public List<String> getAuthoritiesFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            Object authoritiesObj = claims.get("authorities");
            if (authoritiesObj instanceof List) {
                return (List<String>) authoritiesObj;
            }
            return new ArrayList<>();
        } catch (JwtException e) {
            log.error("Failed to extract authorities from token: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 检查Token类型
     */
    public String getTokenType(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return (String) claims.get("type");
        } catch (JwtException e) {
            return null;
        }
    }

    /**
     * 刷新访问令牌
     */
    public String refreshAccessToken(String refreshToken) {
        if (!validateToken(refreshToken)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        String username = getUsernameFromToken(refreshToken);
        String tokenType = getTokenType(refreshToken);
        
        if (!"refresh".equals(tokenType)) {
            throw new BadCredentialsException("Token is not a refresh token");
        }

        // 这里应该从用户服务获取用户的最新权限信息
        // 简化实现，返回新的访问令牌
        return generateAccessToken(createSimpleAuthentication(username));
    }

    /**
     * 创建简单的认证对象（用于刷新令牌场景）
     */
    private Authentication createSimpleAuthentication(String username) {
        return new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                username, null, new ArrayList<>());
    }
}