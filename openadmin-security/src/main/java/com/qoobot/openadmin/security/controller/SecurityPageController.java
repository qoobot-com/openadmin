package com.qoobot.openadmin.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 安全配置页面控制器
 */
@Controller
@RequestMapping("/security")
public class SecurityPageController {

    /**
     * 安全配置主页
     */
    @GetMapping
    public String securityHome() {
        return "security/index";
    }

    /**
     * 用户管理页面
     */
    @GetMapping("/users")
    public String userList() {
        return "security/users";
    }

    /**
     * 角色管理页面
     */
    @GetMapping("/roles")
    public String roleList() {
        return "security/roles";
    }

    /**
     * 权限管理页面
     */
    @GetMapping("/permissions")
    public String permissionList() {
        return "security/permissions";
    }

    /**
     * 安全日志页面
     */
    @GetMapping("/audit")
    public String auditLog() {
        return "security/audit";
    }

    /**
     * 认证配置页面
     */
    @GetMapping("/auth-config")
    public String authConfig() {
        return "security/auth-config";
    }
}