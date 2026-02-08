package com.qoobot.openadmin.security.auth;

import com.qoobot.openadmin.security.model.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * JWT Token认证提供者
 * 验证JWT Token并构建认证对象
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenAuthenticationProvider implements AuthenticationProvider {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getCredentials();

        if (token == null || token.isEmpty()) {
            throw new org.springframework.security.authentication.BadCredentialsException("Token is required");
        }

        // 验证Token
        if (!jwtTokenProvider.validateToken(token)) {
            throw new org.springframework.security.authentication.BadCredentialsException("Invalid or expired token");
        }

        // 提取用户名
        String username = jwtTokenProvider.getUsernameFromToken(token);
        
        // 加载用户详情
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // 提取权限
        List<String> authorities = jwtTokenProvider.getAuthoritiesFromToken(token);

        // 创建认证对象
        org.springframework.security.authentication.UsernamePasswordAuthenticationToken authToken =
            new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                userDetails,
                token,
                userDetails.getAuthorities()
            );

        // 设置用户详细信息
        authToken.setDetails(new UserDetailsImpl(
            ((UserDetailsImpl) userDetails).getId(),
            userDetails.getUsername(),
            userDetails.getPassword(),
            userDetails.getAuthorities(),
            ((UserDetailsImpl) userDetails).getEmail(),
            ((UserDetailsImpl) userDetails).getPhone(),
            ((UserDetailsImpl) userDetails).isAccountNonExpired(),
            ((UserDetailsImpl) userDetails).isAccountNonLocked(),
            ((UserDetailsImpl) userDetails).isCredentialsNonExpired(),
            ((UserDetailsImpl) userDetails).isEnabled()
        ));

        log.debug("JWT token authentication successful for user: {}", username);
        return authToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return org.springframework.security.authentication.UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}