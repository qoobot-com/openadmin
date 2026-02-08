package com.qoobot.openadmin.gateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 熔断器配置类
 * 配置Resilience4j的熔断器参数和策略
 */
@Configuration
public class CircuitBreakerConfiguration {

    private final Map<String, CircuitBreaker> circuitBreakers = new HashMap<>();

    /**
     * 创建默认熔断器配置
     */
    @Bean
    public CircuitBreaker defaultCircuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .slowCallRateThreshold(50)
                .slowCallDurationThreshold(Duration.ofSeconds(2))
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .permittedNumberOfCallsInHalfOpenState(10)
                .minimumNumberOfCalls(20)
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.TIME_BASED)
                .slidingWindowSize(60)
                .recordExceptions(Exception.class)
                .ignoreExceptions(IllegalArgumentException.class)
                .build();
        
        CircuitBreaker circuitBreaker = CircuitBreaker.of("default", config);
        circuitBreakers.put("default", circuitBreaker);
        return circuitBreaker;
    }

    /**
     * 创建管理服务熔断器
     */
    @Bean
    public CircuitBreaker adminServiceCircuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(40)
                .slowCallRateThreshold(40)
                .slowCallDurationThreshold(Duration.ofSeconds(3))
                .waitDurationInOpenState(Duration.ofSeconds(60))
                .permittedNumberOfCallsInHalfOpenState(5)
                .minimumNumberOfCalls(10)
                .slidingWindowSize(120)
                .build();
        
        CircuitBreaker circuitBreaker = CircuitBreaker.of("admin-service", config);
        circuitBreakers.put("admin-service", circuitBreaker);
        return circuitBreaker;
    }

    /**
     * 创建安全服务熔断器
     */
    @Bean
    public CircuitBreaker securityServiceCircuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(30)
                .slowCallRateThreshold(30)
                .slowCallDurationThreshold(Duration.ofSeconds(1))
                .waitDurationInOpenState(Duration.ofSeconds(15))
                .permittedNumberOfCallsInHalfOpenState(3)
                .minimumNumberOfCalls(5)
                .slidingWindowSize(30)
                .build();
        
        CircuitBreaker circuitBreaker = CircuitBreaker.of("security-service", config);
        circuitBreakers.put("security-service", circuitBreaker);
        return circuitBreaker;
    }

    /**
     * 获取熔断器实例
     */
    public CircuitBreaker getCircuitBreaker(String serviceName) {
        return circuitBreakers.getOrDefault(serviceName, defaultCircuitBreaker());
    }

    /**
     * 获取所有熔断器统计信息
     */
    public Map<String, Object> getAllCircuitBreakerStats() {
        Map<String, Object> stats = new HashMap<>();
        circuitBreakers.forEach((name, cb) -> {
            CircuitBreaker.Metrics metrics = cb.getMetrics();
            Map<String, Object> cbStats = new HashMap<>();
            cbStats.put("state", cb.getState().toString());
            cbStats.put("failureRate", metrics.getFailureRate());
            cbStats.put("slowCallRate", metrics.getSlowCallRate());
            cbStats.put("numberOfSuccessfulCalls", metrics.getNumberOfSuccessfulCalls());
            cbStats.put("numberOfFailedCalls", metrics.getNumberOfFailedCalls());
            cbStats.put("numberOfSlowCalls", metrics.getNumberOfSlowCalls());
            stats.put(name, cbStats);
        });
        return stats;
    }
}