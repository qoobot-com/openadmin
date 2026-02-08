package com.qoobot.openadmin.security.authorization;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 自定义权限评估器
 * 实现ABAC（基于属性的访问控制）权限评估
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || targetDomainObject == null || !(permission instanceof String)) {
            return false;
        }

        String targetType = targetDomainObject.getClass().getSimpleName().toLowerCase();
        String perm = (String) permission;

        log.debug("Checking permission '{}' for target '{}' for user '{}'", 
                 perm, targetType, authentication.getName());

        // 基于角色的权限检查
        if (hasRoleBasedPermission(authentication, targetType, perm)) {
            return true;
        }

        // 基于数据所有权的权限检查
        if (hasOwnershipPermission(authentication, targetDomainObject, perm)) {
            return true;
        }

        // 基于属性的权限检查
        if (hasAttributeBasedPermission(authentication, targetDomainObject, perm)) {
            return true;
        }

        log.debug("Permission '{}' denied for target '{}' for user '{}'", 
                 perm, targetType, authentication.getName());
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (authentication == null || targetId == null || targetType == null || !(permission instanceof String)) {
            return false;
        }

        String perm = (String) permission;
        
        log.debug("Checking permission '{}' for target type '{}' with id '{}' for user '{}'", 
                 perm, targetType, targetId, authentication.getName());

        // 基于角色的权限检查
        if (hasRoleBasedPermission(authentication, targetType.toLowerCase(), perm)) {
            return true;
        }

        // 这里可以实现更复杂的基于ID的权限检查逻辑
        // 例如检查用户是否有权访问特定ID的资源

        return false;
    }

    /**
     * 基于角色的权限检查
     */
    private boolean hasRoleBasedPermission(Authentication authentication, String targetType, String permission) {
        String requiredRole = "ROLE_" + permission.toUpperCase();
        
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(requiredRole));
    }

    /**
     * 基于所有权的权限检查
     */
    private boolean hasOwnershipPermission(Authentication authentication, Object targetDomainObject, String permission) {
        // 这里应该检查目标对象的所有者是否为当前用户
        // 简化实现：管理员可以操作所有资源
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

    /**
     * 基于属性的权限检查
     */
    private boolean hasAttributeBasedPermission(Authentication authentication, Object targetDomainObject, String permission) {
        // 这里可以实现基于对象属性的复杂权限检查
        // 例如检查用户部门、地理位置等属性
        
        // 示例：只有管理员可以执行删除操作
        if ("delete".equals(permission)) {
            return authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        }
        
        return true;
    }
}