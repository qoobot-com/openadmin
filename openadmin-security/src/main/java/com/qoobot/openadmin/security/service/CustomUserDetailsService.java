package com.qoobot.openadmin.security.service;

import com.qoobot.openadmin.security.model.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户详情服务实现
 * 从数据库或其他来源加载用户信息
 */
@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            // 模拟从数据库加载用户信息
            // 实际项目中应该从数据库查询用户信息
            
            if ("admin".equals(username)) {
                return createUserDetails(1L, username, "admin123", 
                    Arrays.asList("ROLE_ADMIN", "ROLE_USER"), 
                    "admin@example.com", "13800138000");
            } else if ("user".equals(username)) {
                return createUserDetails(2L, username, "user123", 
                    Arrays.asList("ROLE_USER"), 
                    "user@example.com", "13800138001");
            } else {
                throw new UsernameNotFoundException("User not found: " + username);
            }
        } catch (Exception e) {
            log.error("Error loading user: {}", username, e);
            throw new UsernameNotFoundException("Error loading user: " + username);
        }
    }

    /**
     * 创建用户详情对象
     */
    private UserDetails createUserDetails(Long id, String username, String password, 
                                         List<String> roles, String email, String phone) {
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                id,
                username,
                "{noop}" + password, // 使用明文密码前缀，实际项目中应使用BCrypt加密
                authorities,
                email,
                phone,
                true,  // accountNonExpired
                true,  // accountNonLocked
                true,  // credentialsNonExpired
                true   // enabled
        );
    }
}