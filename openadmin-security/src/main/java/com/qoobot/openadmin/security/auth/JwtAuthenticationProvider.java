package com.qoobot.openadmin.security.auth;

import com.qoobot.openadmin.security.model.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * JWT认证提供者
 * 处理用户名密码认证并生成JWT Token
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        try {
            // 加载用户详情
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 验证密码
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                log.warn("Authentication failed for user: {} - invalid credentials", username);
                throw new BadCredentialsException("Invalid username or password");
            }

            // 检查账户状态
            if (!userDetails.isAccountNonExpired()) {
                throw new BadCredentialsException("Account expired");
            }
            if (!userDetails.isAccountNonLocked()) {
                throw new BadCredentialsException("Account locked");
            }
            if (!userDetails.isCredentialsNonExpired()) {
                throw new BadCredentialsException("Credentials expired");
            }
            if (!userDetails.isEnabled()) {
                throw new BadCredentialsException("Account disabled");
            }

            // 生成JWT Token
            String accessToken = jwtTokenProvider.generateAccessToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(username);

            // 创建认证成功的Token
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(
                    userDetails, 
                    null, 
                    userDetails.getAuthorities()
                );
            
            // 将Token信息存储在认证对象中
            authToken.setDetails(new AuthenticatedUserDetails(
                ((UserDetailsImpl) userDetails).getId(),
                accessToken,
                refreshToken,
                userDetails.getAuthorities()
            ));

            log.info("Authentication successful for user: {}", username);
            return authToken;

        } catch (Exception e) {
            log.error("Authentication failed for user: {} - {}", username, e.getMessage());
            throw new BadCredentialsException("Authentication failed: " + e.getMessage());
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    /**
     * 认证后的用户详情
     */
    public static class AuthenticatedUserDetails {
        private final Long userId;
        private final String accessToken;
        private final String refreshToken;
        private final Object authorities;

        public AuthenticatedUserDetails(Long userId, String accessToken, String refreshToken, Object authorities) {
            this.userId = userId;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.authorities = authorities;
        }

        public Long getUserId() { return userId; }
        public String getAccessToken() { return accessToken; }
        public String getRefreshToken() { return refreshToken; }
        public Object getAuthorities() { return authorities; }
    }
}