package com.qoobot.openadmin.security.config;

import com.qoobot.openadmin.security.auth.JwtAuthenticationProvider;
import com.qoobot.openadmin.security.auth.JwtTokenAuthenticationProvider;
import com.qoobot.openadmin.security.filter.JwtAuthenticationFilter;
import com.qoobot.openadmin.security.filter.SecurityAuditFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;

/**
 * Spring Security核心配置
 * 配置认证、授权、过滤器链等安全设置
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final JwtTokenAuthenticationProvider jwtTokenAuthenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final SecurityAuditFilter securityAuditFilter;

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Arrays.asList(
            jwtAuthenticationProvider,
            jwtTokenAuthenticationProvider
        ));
    }

    /**
     * 安全过滤器链配置
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF（JWT场景下通常不需要）
            .csrf(AbstractHttpConfigurer::disable)
            
            // 禁用Session
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // 配置认证请求
            .authorizeHttpRequests(authz -> authz
                // 公开接口
                .requestMatchers("/api/auth/login", "/api/auth/refresh", "/api/public/**").permitAll()
                // Swagger UI
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // 静态资源
                .requestMatchers("/webjars/**", "/favicon.ico").permitAll()
                // 健康检查
                .requestMatchers("/actuator/health").permitAll()
                // 其他所有请求都需要认证
                .anyRequest().authenticated()
            )
            
            // 添加自定义过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(securityAuditFilter, UsernamePasswordAuthenticationFilter.class)
            
            // 禁用默认登录页面
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}