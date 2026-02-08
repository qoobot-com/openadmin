package com.qoobot.openadmin.admin.security;

import com.qoobot.openadmin.admin.entity.UserRole;
import com.qoobot.openadmin.admin.entity.User;
import com.qoobot.openadmin.admin.repository.UserRepository;
import com.qoobot.openadmin.admin.repository.UserRoleRepository;
import com.qoobot.openadmin.admin.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

/**
 * PermissionEvaluator that checks if an authentication has a permission code by resolving
 * the authenticated user -> roles -> aggregated permissions (including inheritance) via RoleService.
 */
@Component
public class RolePermissionEvaluator implements PermissionEvaluator {

    @Autowired(required = false)
    private RoleService roleService;

    @Autowired(required = false)
    private UserRepository userRepository;

    @Autowired(required = false)
    private UserRoleRepository userRoleRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || permission == null) return false;
        String username = authentication.getName();
        if (username == null) return false;
        if (userRepository == null || userRoleRepository == null || roleService == null) {
            // If services not available, fail-safe deny
            return false;
        }
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return false;
        List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
        if (userRoles == null || userRoles.isEmpty()) return false;
        String perm = permission.toString();
        for (UserRole ur : userRoles) {
            List<String> perms = roleService.getPermissionsAggregated(ur.getRoleId());
            if (perms != null && perms.contains(perm)) return true;
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return hasPermission(authentication, null, permission);
    }
}
