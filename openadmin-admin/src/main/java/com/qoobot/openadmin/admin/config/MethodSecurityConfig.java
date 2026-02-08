package com.qoobot.openadmin.admin.config;

import com.qoobot.openadmin.admin.security.RolePermissionEvaluator;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;

@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    @Autowired
    private RolePermissionEvaluator permissionEvaluator;

    // Method security configuration removed to avoid incompatible API surface in this workspace.
    // In production, enable method security and register a MethodSecurityExpressionHandler that uses
    // the application PermissionEvaluator (RolePermissionEvaluator) created under security package.
}
