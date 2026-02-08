package com.qoobot.openadmin.security.controller;

import com.qoobot.openadmin.security.dto.ApiResponse;
import com.qoobot.openadmin.security.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 提供用户认证相关的REST API接口
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<AuthenticationService.LoginResponse> login(
            @RequestBody AuthenticationService.LoginRequest request) {
        try {
            AuthenticationService.LoginResponse response = authenticationService.login(request);
            return ApiResponse.success(response, "Login successful");
        } catch (Exception e) {
            log.error("Login failed for user: {}", request.getUsername(), e);
            return ApiResponse.error("Login failed: " + e.getMessage());
        }
    }

    /**
     * 刷新访问令牌
     */
    @PostMapping("/refresh")
    public ApiResponse<AuthenticationService.RefreshTokenResponse> refreshToken(
            @RequestHeader("Authorization") String refreshToken) {
        try {
            if (refreshToken.startsWith("Bearer ")) {
                refreshToken = refreshToken.substring(7);
            }
            AuthenticationService.RefreshTokenResponse response = authenticationService.refreshToken(refreshToken);
            return ApiResponse.success(response, "Token refreshed successfully");
        } catch (Exception e) {
            log.error("Token refresh failed", e);
            return ApiResponse.error("Token refresh failed: " + e.getMessage());
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        try {
            authenticationService.logout();
            return ApiResponse.success(null, "Logout successful");
        } catch (Exception e) {
            log.error("Logout failed", e);
            return ApiResponse.error("Logout failed: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ApiResponse<AuthenticationService.UserInfo> getCurrentUser(
            @RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            AuthenticationService.UserInfo userInfo = authenticationService.getUserInfoFromToken(token);
            return ApiResponse.success(userInfo, "User info retrieved successfully");
        } catch (Exception e) {
            log.error("Failed to get user info", e);
            return ApiResponse.error("Failed to get user info: " + e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(
            @RequestBody AuthenticationService.ChangePasswordRequest request) {
        try {
            authenticationService.changePassword(request);
            return ApiResponse.success(null, "Password changed successfully");
        } catch (Exception e) {
            log.error("Password change failed", e);
            return ApiResponse.error("Password change failed: " + e.getMessage());
        }
    }

    /**
     * 验证Token
     */
    @GetMapping("/validate")
    public ApiResponse<Boolean> validateToken(
            @RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            boolean isValid = authenticationService.validateToken(token);
            return ApiResponse.success(isValid, "Token validation completed");
        } catch (Exception e) {
            log.error("Token validation failed", e);
            return ApiResponse.error("Token validation failed: " + e.getMessage());
        }
    }
}