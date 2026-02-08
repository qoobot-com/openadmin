package com.qoobot.openadmin.security.authorization;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * 自定义访问决策管理器
 * 实现RBAC权限模型的访问控制决策
 */
@Slf4j
@Component
public class CustomAccessDecisionManager {

    /**
     * 简化的权限决策方法
     */
    public boolean decideAccess(Authentication authentication, Collection<String> requiredRoles) {
        if (requiredRoles == null || requiredRoles.isEmpty()) {
            return true;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        for (String requiredRole : requiredRoles) {
            for (GrantedAuthority authority : authorities) {
                if (requiredRole.equals(authority.getAuthority())) {
                    log.debug("Access granted for user: {} with authority: {}", 
                             authentication.getName(), requiredRole);
                    return true;
                }
            }
        }
        
        log.warn("Access denied for user: {} - missing required roles: {}", 
                authentication.getName(), requiredRoles);
        return false;
    }
}