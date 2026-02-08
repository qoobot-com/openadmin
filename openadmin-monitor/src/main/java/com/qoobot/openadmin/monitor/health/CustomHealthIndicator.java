package com.qoobot.openadmin.monitor.health;

import com.qoobot.openadmin.monitor.service.LogMonitorService;
import com.qoobot.openadmin.monitor.service.MetricService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义健康检查指示器
 * 提供详细的系统健康状态检查
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomHealthIndicator /* implements HealthIndicator */ {

    private final MetricService metricService;
    private final LogMonitorService logMonitorService;

    /*
    @Override
    public Health health() {
        try {
            Health.Builder builder = Health.up();
            
            // 检查JVM健康状态
            checkJvmHealth(builder);
            
            // 检查应用健康状态
            checkApplicationHealth(builder);
            
            // 检查日志健康状态
            checkLogHealth(builder);
            
            return builder.build();
        } catch (Exception e) {
            log.error("Health check failed", e);
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
    */

    /**
     * 获取健康状态信息
     */
    public Map<String, Object> getHealthInfo() {
        Map<String, Object> healthInfo = new HashMap<>();
        
        try {
            // 检查JVM健康状态
            checkJvmHealth(healthInfo);
            
            // 检查应用健康状态
            checkApplicationHealth(healthInfo);
            
            // 检查日志健康状态
            checkLogHealth(healthInfo);
            
            healthInfo.put("status", "UP");
        } catch (Exception e) {
            log.error("Health check failed", e);
            healthInfo.put("status", "DOWN");
            healthInfo.put("error", e.getMessage());
        }
        
        return healthInfo;
    }

    /**
     * 检查JVM健康状态
     */
    private void checkJvmHealth(Map<String, Object> healthInfo) {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        
        long heapUsed = memoryBean.getHeapMemoryUsage().getUsed();
        long heapMax = memoryBean.getHeapMemoryUsage().getMax();
        double heapUsagePercent = (double) heapUsed / heapMax * 100;
        
        int threadCount = threadBean.getThreadCount();
        int peakThreadCount = threadBean.getPeakThreadCount();
        
        Map<String, Object> jvmInfo = new HashMap<>();
        
        // 内存使用率检查
        if (heapUsagePercent > 90) {
            jvmInfo.put("memory.status", "CRITICAL");
        } else if (heapUsagePercent > 80) {
            jvmInfo.put("memory.status", "WARNING");
        } else {
            jvmInfo.put("memory.status", "HEALTHY");
        }
        jvmInfo.put("memory.usage.percent", String.format("%.2f%%", heapUsagePercent));
        
        // 线程数检查
        if (threadCount > 500) {
            jvmInfo.put("threads.status", "CRITICAL");
        } else if (threadCount > 300) {
            jvmInfo.put("threads.status", "WARNING");
        } else {
            jvmInfo.put("threads.status", "HEALTHY");
        }
        jvmInfo.put("threads.count", threadCount);
        jvmInfo.put("threads.peak", peakThreadCount);
        
        healthInfo.put("jvm", jvmInfo);
    }

    /**
     * 检查应用健康状态
     */
    private void checkApplicationHealth(Map<String, Object> healthInfo) {
        try {
            Map<String, Object> appInfo = new HashMap<>();
            
            // 刷新并检查JVM指标
            metricService.refreshJvmMetrics();
            
            long currentMemory = metricService.getCurrentMemoryUsed();
            int currentThreads = metricService.getCurrentThreadCount();
            
            appInfo.put("memory.used", currentMemory);
            appInfo.put("thread.count", currentThreads);
            
            // 检查是否有过多的错误
            LogMonitorService.LogStatistics stats = logMonitorService.getLogStatistics();
            if (stats.getErrorRate() > 0.1) { // 错误率超过10%
                appInfo.put("error.rate.status", "WARNING");
            } else {
                appInfo.put("error.rate.status", "HEALTHY");
            }
            appInfo.put("error.rate", String.format("%.2f%%", stats.getErrorRate() * 100));
            
            healthInfo.put("application", appInfo);
        } catch (Exception e) {
            Map<String, Object> appInfo = new HashMap<>();
            appInfo.put("status", "ERROR");
            appInfo.put("error", e.getMessage());
            healthInfo.put("application", appInfo);
        }
    }

    /**
     * 检查日志健康状态
     */
    private void checkLogHealth(Map<String, Object> healthInfo) {
        try {
            Map<String, Object> logInfo = new HashMap<>();
            LogMonitorService.LogStatistics stats = logMonitorService.getLogStatistics();
            
            logInfo.put("error.count", stats.getTotalErrorLogs());
            logInfo.put("warn.count", stats.getTotalWarnLogs());
            logInfo.put("info.count", stats.getTotalInfoLogs());
            
            // 检查是否需要告警
            if (logMonitorService.shouldAlert()) {
                logInfo.put("alert.status", "ALERT_NEEDED");
            } else {
                logInfo.put("alert.status", "NORMAL");
            }
            
            healthInfo.put("log", logInfo);
        } catch (Exception e) {
            Map<String, Object> logInfo = new HashMap<>();
            logInfo.put("status", "ERROR");
            logInfo.put("error", e.getMessage());
            healthInfo.put("log", logInfo);
        }
    }
}