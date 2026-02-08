package com.qoobot.openadmin.starter;

import com.qoobot.openadmin.starter.properties.OpenAdminProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAdmin 自动配置类
 * 根据类路径和配置属性条件自动装配相关功能
 */
@Configuration
@EnableConfigurationProperties(OpenAdminProperties.class)
@ConditionalOnProperty(prefix = "openadmin", name = "enabled", havingValue = "true", matchIfMissing = true)
public class OpenAdminAutoConfiguration {

    /**
     * 基础标记 Bean，确保自动配置已加载
     */
    @Bean
    @ConditionalOnMissingBean
    public AutoConfigurationMarker autoConfigurationMarker() {
        return new AutoConfigurationMarker();
    }

    /**
     * 安全自动配置
     */
    @Configuration
    @ConditionalOnClass(name = "com.qoobot.openadmin.security.SecurityApplication")
    @ConditionalOnProperty(prefix = "openadmin.security", name = "enabled", havingValue = "true", matchIfMissing = true)
    static class SecurityAutoConfiguration {
        
        @Bean
        @ConditionalOnMissingBean
        public SecurityMarker securityMarker() {
            return new SecurityMarker();
        }
    }

    /**
     * 监控自动配置
     */
    @Configuration
    @ConditionalOnClass(name = "com.qoobot.openadmin.monitor.MonitorApplication")
    @ConditionalOnProperty(prefix = "openadmin.monitoring", name = "enabled", havingValue = "true", matchIfMissing = true)
    static class MonitoringAutoConfiguration {
        
        @Bean
        @ConditionalOnMissingBean
        public MonitorMarker monitorMarker() {
            return new MonitorMarker();
        }
    }

    /**
     * 网关自动配置
     */
    @Configuration
    @ConditionalOnClass(name = "com.qoobot.openadmin.gateway.GatewayApplication")
    @ConditionalOnProperty(prefix = "openadmin.gateway", name = "enabled", havingValue = "true")
    static class GatewayAutoConfiguration {
        
        @Bean
        @ConditionalOnMissingBean
        public GatewayMarker gatewayMarker() {
            return new GatewayMarker();
        }
    }

    /**
     * 配置中心自动配置
     */
    @Configuration
    @ConditionalOnClass(name = "com.qoobot.openadmin.config.ConfigService")
    @ConditionalOnProperty(prefix = "openadmin.config", name = "enabled", havingValue = "true", matchIfMissing = true)
    static class ConfigAutoConfiguration {
        
        @Bean
        @ConditionalOnMissingBean
        public ConfigMarker configMarker() {
            return new ConfigMarker();
        }
    }

    /**
     * 管理后台自动配置
     */
    @Configuration
    @ConditionalOnClass(name = "com.qoobot.openadmin.admin.AdminApplication")
    @ConditionalOnProperty(prefix = "openadmin.admin", name = "enabled", havingValue = "true", matchIfMissing = true)
    static class AdminAutoConfiguration {
        
        @Bean
        @ConditionalOnMissingBean
        public AdminMarker adminMarker() {
            return new AdminMarker();
        }
    }

    /**
     * 核心服务自动配置
     */
    @Configuration
    @ConditionalOnClass(name = "com.qoobot.openadmin.core.CoreMarker")
    static class CoreAutoConfiguration {
        
        @Bean
        @ConditionalOnMissingBean
        public CoreMarker coreMarker() {
            return new CoreMarker();
        }
    }
}
