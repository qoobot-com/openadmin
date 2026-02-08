package com.qoobot.openadmin.starter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * OpenAdmin 配置属性类
 * 支持通过 application.yml 配置框架各项功能
 */
@Data
@ConfigurationProperties(prefix = "openadmin")
public class OpenAdminProperties {

    /**
     * 是否启用 OpenAdmin 框架
     */
    private boolean enabled = true;

    /**
     * 安全配置
     */
    @NestedConfigurationProperty
    private Security security = new Security();

    /**
     * 监控配置
     */
    @NestedConfigurationProperty
    private Monitoring monitoring = new Monitoring();

    /**
     * 网关配置
     */
    @NestedConfigurationProperty
    private Gateway gateway = new Gateway();

    /**
     * 配置中心配置
     */
    @NestedConfigurationProperty
    private Config config = new Config();

    /**
     * 管理后台配置
     */
    @NestedConfigurationProperty
    private Admin admin = new Admin();

    /**
     * 安全配置属性
     */
    @Data
    public static class Security {
        /**
         * 是否启用安全功能
         */
        private boolean enabled = true;

        /**
         * JWT 配置
         */
        @NestedConfigurationProperty
        private Jwt jwt = new Jwt();

        /**
         * OAuth2 配置
         */
        @NestedConfigurationProperty
        private OAuth2 oauth2 = new OAuth2();

        /**
         * 认证方式：jwt, oauth2, form
         */
        private String authType = "jwt";

        /**
         * Token 过期时间（秒）
         */
        private Long tokenExpireTime = 3600L;

        /**
         * 刷新 Token 过期时间（秒）
         */
        private Long refreshTokenExpireTime = 2592000L; // 30天

        /**
         * 是否启用 CSRF 保护
         */
        private boolean csrfEnabled = true;

        /**
         * 是否启用 XSS 防护
         */
        private boolean xssEnabled = true;

        /**
         * 密码强度要求
         */
        private String passwordPolicy = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,}$";

        @Data
        public static class Jwt {
            /**
             * JWT 密钥
             */
            private String secret = "openadmin-default-secret-key-change-in-production";

            /**
             * Token 签发者
             */
            private String issuer = "openadmin";

            /**
             * Token 受众
             */
            private String audience = "openadmin-users";

            /**
             * 是否启用 JWT
             */
            private boolean enabled = true;
        }

        @Data
        public static class OAuth2 {
            /**
             * 是否启用 OAuth2
             */
            private boolean enabled = false;

            /**
             * 授权服务器地址
             */
            private String authorizationServerUrl;

            /**
             * 客户端 ID
             */
            private String clientId;

            /**
             * 客户端密钥
             */
            private String clientSecret;

            /**
             * 重定向 URI
             */
            private String redirectUri;
        }
    }

    /**
     * 监控配置属性
     */
    @Data
    public static class Monitoring {
        /**
         * 是否启用监控功能
         */
        private boolean enabled = true;

        /**
         * 日志级别
         */
        private String logLevel = "INFO";

        /**
         * 是否启用指标收集
         */
        private boolean metricsEnabled = true;

        /**
         * 是否启用健康检查
         */
        private boolean healthEnabled = true;

        /**
         * 是否启用审计日志
         */
        private boolean auditEnabled = true;

        /**
         * 审计日志保留天数
         */
        private Integer auditRetentionDays = 30;

        /**
         * 性能监控采样率 (0.0-1.0)
         */
        private Double samplingRate = 0.1;

        /**
         * 慢查询阈值（毫秒）
         */
        private Long slowQueryThreshold = 1000L;
    }

    /**
     * 网关配置属性
     */
    @Data
    public static class Gateway {
        /**
         * 是否启用网关功能
         */
        private boolean enabled = false;

        /**
         * 网关端口
         */
        private Integer port = 9090;

        /**
         * 默认路由
         */
        private String defaultRoute = "lb://default-service";

        /**
         * 是否启用限流
         */
        private boolean rateLimitEnabled = true;

        /**
         * 全局限流请求数
         */
        private Integer globalRateLimit = 1000;

        /**
         * IP 限流请求数
         */
        private Integer ipRateLimit = 100;

        /**
         * 用户限流请求数
         */
        private Integer userRateLimit = 200;

        /**
         * 是否启用熔断器
         */
        private boolean circuitBreakerEnabled = true;

        /**
         * 熔断器失败率阈值
         */
        private Integer failureRateThreshold = 50;

        /**
         * 熔断器等待时间（毫秒）
         */
        private Long waitDurationInOpenState = 30000L;
    }

    /**
     * 配置中心配置属性
     */
    @Data
    public static class Config {
        /**
         * 是否启用配置中心
         */
        private boolean enabled = true;

        /**
         * 配置服务器地址
         */
        private String serverAddr = "localhost:8848";

        /**
         * 命名空间
         */
        private String namespace = "openadmin";

        /**
         * 配置组
         */
        private String group = "DEFAULT_GROUP";

        /**
         * 配置文件扩展名
         */
        private String fileExtension = "yaml";

        /**
         * 是否启用本地缓存
         */
        private boolean localCacheEnabled = true;

        /**
         * 本地缓存路径
         */
        private String localCachePath = "./config-cache";
    }

    /**
     * 管理后台配置属性
     */
    @Data
    public static class Admin {
        /**
         * 是否启用管理后台
         */
        private boolean enabled = true;

        /**
         * 管理后台端口
         */
        private Integer port = 8080;

        /**
         * 管理后台上下文路径
         */
        private String contextPath = "/admin";

        /**
         * 是否启用 Thymeleaf 模板
         */
        private boolean thymeleafEnabled = true;

        /**
         * 是否启用 Swagger UI
         */
        private boolean swaggerEnabled = true;

        /**
         * 管理员默认用户名
         */
        private String defaultUsername = "admin";

        /**
         * 管理员默认密码
         */
        private String defaultPassword = "admin123";
    }
}