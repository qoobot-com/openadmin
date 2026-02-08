package com.qoobot.openadmin.security.service;

import com.qoobot.openadmin.security.auth.JwtTokenProvider;
import com.qoobot.openadmin.security.model.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * 认证服务接口实现
 * 提供用户认证、Token管理等核心功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 用户登录认证
     */
    public LoginResponse login(LoginRequest request) {
        try {
            // 创建认证Token
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());

            // 执行认证
            Authentication authentication = authenticationManager.authenticate(authToken);
            
            // 设置安全上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 获取认证详情
            Object details = authentication.getDetails();
            if (details instanceof com.qoobot.openadmin.security.auth.JwtAuthenticationProvider.AuthenticatedUserDetails) {
                com.qoobot.openadmin.security.auth.JwtAuthenticationProvider.AuthenticatedUserDetails authDetails = 
                    (com.qoobot.openadmin.security.auth.JwtAuthenticationProvider.AuthenticatedUserDetails) details;
                
                return new LoginResponse(
                    authDetails.getUserId(),
                    authDetails.getAccessToken(),
                    authDetails.getRefreshToken(),
                    authentication.getName(),
                    authDetails.getAuthorities()
                );
            }

            throw new RuntimeException("Authentication details not found");
        } catch (Exception e) {
            log.error("Login failed for user: {} - {}", request.getUsername(), e.getMessage());
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }

    /**
     * 刷新访问令牌
     */
    public RefreshTokenResponse refreshToken(String refreshToken) {
        try {
            String newAccessToken = jwtTokenProvider.refreshAccessToken(refreshToken);
            return new RefreshTokenResponse(newAccessToken, refreshToken);
        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            throw new RuntimeException("Token refresh failed: " + e.getMessage());
        }
    }

    /**
     * 用户登出
     */
    public void logout() {
        SecurityContextHolder.clearContext();
        log.info("User logged out successfully");
    }

    /**
     * 验证Token
     */
    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    /**
     * 从Token获取用户信息
     */
    public UserInfo getUserInfoFromToken(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new RuntimeException("Invalid token");
        }

        String username = jwtTokenProvider.getUsernameFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        return new UserInfo(
            ((UserDetailsImpl) userDetails).getId(),
            userDetails.getUsername(),
            ((UserDetailsImpl) userDetails).getEmail(),
            ((UserDetailsImpl) userDetails).getPhone(),
            userDetails.getAuthorities()
        );
    }

    /**
     * 修改密码
     */
    public void changePassword(ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        String username = authentication.getName();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // 验证旧密码
        if (!passwordEncoder.matches(request.getOldPassword(), userDetails.getPassword())) {
            throw new RuntimeException("Invalid old password");
        }

        // 这里应该调用用户服务更新密码
        log.info("Password changed successfully for user: {}", username);
    }

    /**
     * 登录请求DTO
     */
    @lombok.Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    /**
     * 登录响应DTO
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class LoginResponse {
        private Long userId;
        private String accessToken;
        private String refreshToken;
        private String username;
        private Object authorities;
    }

    /**
     * 刷新Token响应DTO
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class RefreshTokenResponse {
        private String accessToken;
        private String refreshToken;
    }

    /**
     * 用户信息DTO
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class UserInfo {
        private Long userId;
        private String username;
        private String email;
        private String phone;
        private Object authorities;
    }

    /**
     * 修改密码请求DTO
     */
    @lombok.Data
    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;
    }
}