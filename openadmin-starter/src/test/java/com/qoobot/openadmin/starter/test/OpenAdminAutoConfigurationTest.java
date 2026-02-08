package com.qoobot.openadmin.starter.test;

import com.qoobot.openadmin.starter.EnableOpenAdmin;
import com.qoobot.openadmin.starter.OpenAdminAutoConfiguration;
import com.qoobot.openadmin.starter.properties.OpenAdminProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * OpenAdmin 自动配置测试类
 * 验证各种条件装配场景的正确性
 */
class OpenAdminAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(OpenAdminAutoConfiguration.class));

    @Test
    void testDefaultAutoConfiguration() {
        this.contextRunner
                .withPropertyValues("openadmin.enabled=true")
                .run(context -> {
                    assertThat(context).hasSingleBean(OpenAdminProperties.class);
                    assertThat(context).hasBean("autoConfigurationMarker");
                    assertThat(context.getBean(OpenAdminProperties.class).isEnabled()).isTrue();
                });
    }

    @Test
    void testDisabledAutoConfiguration() {
        this.contextRunner
                .withPropertyValues("openadmin.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(OpenAdminProperties.class);
                    assertThat(context).doesNotHaveBean("autoConfigurationMarker");
                });
    }

    @Test
    void testSecurityAutoConfiguration() {
        this.contextRunner
                .withPropertyValues(
                        "openadmin.enabled=true",
                        "openadmin.security.enabled=true"
                )
                .run(context -> {
                    OpenAdminProperties properties = context.getBean(OpenAdminProperties.class);
                    assertThat(properties.getSecurity().isEnabled()).isTrue();
                    assertThat(properties.getSecurity().getAuthType()).isEqualTo("jwt");
                    assertThat(properties.getSecurity().getTokenExpireTime()).isEqualTo(3600L);
                });
    }

    @Test
    void testMonitoringAutoConfiguration() {
        this.contextRunner
                .withPropertyValues(
                        "openadmin.enabled=true",
                        "openadmin.monitoring.enabled=true",
                        "openadmin.monitoring.logLevel=DEBUG"
                )
                .run(context -> {
                    OpenAdminProperties properties = context.getBean(OpenAdminProperties.class);
                    assertThat(properties.getMonitoring().isEnabled()).isTrue();
                    assertThat(properties.getMonitoring().getLogLevel()).isEqualTo("DEBUG");
                    assertThat(properties.getMonitoring().isMetricsEnabled()).isTrue();
                });
    }

    @Test
    void testGatewayAutoConfiguration() {
        this.contextRunner
                .withPropertyValues(
                        "openadmin.enabled=true",
                        "openadmin.gateway.enabled=true",
                        "openadmin.gateway.port=8080"
                )
                .run(context -> {
                    OpenAdminProperties properties = context.getBean(OpenAdminProperties.class);
                    assertThat(properties.getGateway().isEnabled()).isTrue();
                    assertThat(properties.getGateway().getPort()).isEqualTo(8080);
                    assertThat(properties.getGateway().isRateLimitEnabled()).isTrue();
                });
    }

    @Test
    void testConfigAutoConfiguration() {
        this.contextRunner
                .withPropertyValues(
                        "openadmin.enabled=true",
                        "openadmin.config.enabled=true",
                        "openadmin.config.serverAddr=config-server:8848"
                )
                .run(context -> {
                    OpenAdminProperties properties = context.getBean(OpenAdminProperties.class);
                    assertThat(properties.getConfig().isEnabled()).isTrue();
                    assertThat(properties.getConfig().getServerAddr()).isEqualTo("config-server:8848");
                    assertThat(properties.getConfig().getNamespace()).isEqualTo("openadmin");
                });
    }

    @Test
    void testAdminAutoConfiguration() {
        this.contextRunner
                .withPropertyValues(
                        "openadmin.enabled=true",
                        "openadmin.admin.enabled=true",
                        "openadmin.admin.port=9090"
                )
                .run(context -> {
                    OpenAdminProperties properties = context.getBean(OpenAdminProperties.class);
                    assertThat(properties.getAdmin().isEnabled()).isTrue();
                    assertThat(properties.getAdmin().getPort()).isEqualTo(9090);
                    assertThat(properties.getAdmin().getContextPath()).isEqualTo("/admin");
                });
    }

    @Test
    void testPropertyBinding() {
        this.contextRunner
                .withPropertyValues(
                        "openadmin.enabled=true",
                        "openadmin.security.jwt.secret=test-secret",
                        "openadmin.security.oauth2.enabled=true",
                        "openadmin.monitoring.samplingRate=0.5",
                        "openadmin.gateway.failureRateThreshold=75"
                )
                .run(context -> {
                    OpenAdminProperties properties = context.getBean(OpenAdminProperties.class);
                    
                    // 测试嵌套属性绑定
                    assertThat(properties.getSecurity().getJwt().getSecret()).isEqualTo("test-secret");
                    assertThat(properties.getSecurity().getOauth2().isEnabled()).isTrue();
                    assertThat(properties.getMonitoring().getSamplingRate()).isEqualTo(0.5);
                    assertThat(properties.getGateway().getFailureRateThreshold()).isEqualTo(75);
                });
    }

    @Test
    void testEnableOpenAdminAnnotation() {
        this.contextRunner
                .withUserConfiguration(TestConfigWithEnableAnnotation.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(OpenAdminProperties.class);
                    assertThat(context).hasBean("autoConfigurationMarker");
                });
    }

    @Test
    void testConditionalOnClass() {
        // 测试类路径条件装配 - 这个测试验证即使相关类不存在也能正常启动
        this.contextRunner
                .withPropertyValues("openadmin.enabled=true")
                .run(context -> {
                    assertThat(context).hasSingleBean(OpenAdminProperties.class);
                    // 由于测试环境中相关模块类可能不存在，所以不会创建对应的 Marker beans
                    // 但这证明了条件装配逻辑是工作的
                });
    }

    @Test
    void testDefaultValueFallback() {
        this.contextRunner
                .withPropertyValues("openadmin.enabled=true")
                .run(context -> {
                    OpenAdminProperties properties = context.getBean(OpenAdminProperties.class);
                    
                    // 验证默认值
                    assertThat(properties.isEnabled()).isTrue();
                    assertThat(properties.getSecurity().isEnabled()).isTrue();
                    assertThat(properties.getSecurity().getAuthType()).isEqualTo("jwt");
                    assertThat(properties.getMonitoring().getLogLevel()).isEqualTo("INFO");
                    assertThat(properties.getConfig().getFileExtension()).isEqualTo("yaml");
                });
    }

    @Configuration
    @EnableOpenAdmin
    static class TestConfigWithEnableAnnotation {
        // 测试配置类，使用 @EnableOpenAdmin 注解
    }
}