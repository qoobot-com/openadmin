package com.qoobot.openadmin.gateway.test;

import com.qoobot.openadmin.gateway.filter.AuthenticationGatewayFilter;
import com.qoobot.openadmin.gateway.filter.RateLimitGatewayFilter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 网关组件单元测试
 */
class GatewayComponentTest {

    @Test
    void testAuthenticationFilterCreation() {
        // 测试认证过滤器能够正常创建
        AuthenticationGatewayFilter filter = new AuthenticationGatewayFilter();
        assertThat(filter).isNotNull();
        assertThat(filter.getBlacklistSize()).isEqualTo(0);
    }

    @Test
    void testRateLimitFilterCreation() {
        // 测试限流过滤器能够正常创建
        RateLimitGatewayFilter filter = new RateLimitGatewayFilter();
        assertThat(filter).isNotNull();
    }

    @Test
    void testAuthenticationBlacklistOperations() {
        // 测试黑名单操作
        AuthenticationGatewayFilter filter = new AuthenticationGatewayFilter();
        
        String testToken = "test-token-123";
        filter.addToBlacklist(testToken);
        assertThat(filter.getBlacklistSize()).isEqualTo(1);
        
        filter.removeFromBlacklist(testToken);
        assertThat(filter.getBlacklistSize()).isEqualTo(0);
        
        filter.clearBlacklist();
        assertThat(filter.getBlacklistSize()).isEqualTo(0);
    }

    @Test
    void testRateLimitStatistics() {
        // 测试限流统计功能
        RateLimitGatewayFilter filter = new RateLimitGatewayFilter();
        
        // 测试获取统计信息
        var globalStats = filter.getGlobalRateLimitStats();
        assertThat(globalStats).isNotNull();
        assertThat(globalStats).containsKeys("availablePermissions", "numberOfWaitingThreads");
    }
}