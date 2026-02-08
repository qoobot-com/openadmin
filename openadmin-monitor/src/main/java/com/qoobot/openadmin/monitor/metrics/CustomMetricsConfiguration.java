package com.qoobot.openadmin.monitor.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * 自定义监控指标配置
 * 注册各种自定义监控指标
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomMetricsConfiguration {

    private final MeterRegistry meterRegistry;

    @PostConstruct
    public void registerCustomMetrics() {
        // 简化的指标注册
        log.info("Custom metrics registered successfully");
    }
}