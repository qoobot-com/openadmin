package com.qoobot.openadmin.security.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 安全审计过滤器
 * 记录安全相关的访问日志和审计信息
 */
@Slf4j
@Component
public class SecurityAuditFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String requestId = UUID.randomUUID().toString();
        String clientIp = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        String method = request.getMethod();
        String uri = request.getRequestURI();
        
        long startTime = System.currentTimeMillis();
        
        try {
            // 记录请求开始
            log.info("SECURITY_AUDIT - Request Start | ID: {} | IP: {} | Method: {} | URI: {} | User-Agent: {}", 
                    requestId, clientIp, method, uri, userAgent);
            
            filterChain.doFilter(request, response);
            
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();
            
            // 记录请求结束
            log.info("SECURITY_AUDIT - Request End | ID: {} | Status: {} | Duration: {}ms | IP: {}", 
                    requestId, status, duration, clientIp);
            
            // 记录安全相关的特殊事件
            auditSpecialEvents(request, response, requestId);
        }
    }

    /**
     * 审计特殊安全事件
     */
    private void auditSpecialEvents(HttpServletRequest request, HttpServletResponse response, String requestId) {
        String uri = request.getRequestURI();
        int status = response.getStatus();
        
        // 记录登录尝试
        if ("/api/auth/login".equals(uri)) {
            if (status == 200) {
                log.info("SECURITY_AUDIT - LOGIN_SUCCESS | ID: {} | User: {}", 
                        requestId, request.getParameter("username"));
            } else {
                log.warn("SECURITY_AUDIT - LOGIN_FAILED | ID: {} | User: {} | Status: {}", 
                        requestId, request.getParameter("username"), status);
            }
        }
        
        // 记录未授权访问尝试
        if (status == 401 || status == 403) {
            log.warn("SECURITY_AUDIT - UNAUTHORIZED_ACCESS | ID: {} | IP: {} | URI: {} | Status: {}", 
                    requestId, getClientIpAddress(request), uri, status);
        }
        
        // 记录可疑活动
        if (isSuspiciousActivity(request)) {
            log.warn("SECURITY_AUDIT - SUSPICIOUS_ACTIVITY | ID: {} | IP: {} | URI: {} | Details: {}", 
                    requestId, getClientIpAddress(request), uri, getSuspiciousDetails(request));
        }
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    /**
     * 判断是否为可疑活动
     */
    private boolean isSuspiciousActivity(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        
        // 敏感端点的非GET请求
        if (("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method)) &&
            (uri.contains("/api/admin/") || uri.contains("/api/user/") || uri.contains("/api/config/"))) {
            return true;
        }
        
        // 频繁的认证失败
        if ("/api/auth/login".equals(uri) && "POST".equals(method)) {
            // 这里应该结合Redis等存储来统计失败次数
            return false; // 简化实现
        }
        
        return false;
    }

    /**
     * 获取可疑活动详情
     */
    private String getSuspiciousDetails(HttpServletRequest request) {
        StringBuilder details = new StringBuilder();
        details.append("Method: ").append(request.getMethod());
        details.append(", Headers: ");
        
        request.getHeaderNames().asIterator().forEachRemaining(header -> {
            if (header.toLowerCase().contains("auth") || header.toLowerCase().contains("token")) {
                details.append(header).append(": ***PROTECTED***, ");
            } else {
                details.append(header).append(": ").append(request.getHeader(header)).append(", ");
            }
        });
        
        return details.toString();
    }
}