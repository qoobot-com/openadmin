package com.qoobot.openadmin.monitor.controller;

import com.qoobot.openadmin.monitor.dto.MonitorResponse;
import com.qoobot.openadmin.monitor.dto.SystemMetrics;
import com.qoobot.openadmin.monitor.service.LogMonitorService;
import com.qoobot.openadmin.monitor.service.MetricService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 监控控制器
 * 提供系统监控API接口
 */
@Slf4j
@RestController
@RequestMapping("/api/monitor")
@RequiredArgsConstructor
public class MonitorController {

    private final MetricService metricService;
    private final LogMonitorService logMonitorService;
    private final MeterRegistry meterRegistry;

    /**
     * 获取系统健康状态
     */
    @GetMapping("/health")
    public MonitorResponse<SystemMetrics> getSystemHealth() {
        try {
            SystemMetrics metrics = collectSystemMetrics();
            return MonitorResponse.success(metrics, "System health status retrieved successfully");
        } catch (Exception e) {
            log.error("Failed to get system health", e);
            return MonitorResponse.error("Failed to retrieve system health: " + e.getMessage());
        }
    }

    /**
     * 获取JVM监控指标
     */
    @GetMapping("/metrics/jvm")
    public MonitorResponse<Map<String, Object>> getJvmMetrics() {
        try {
            Map<String, Object> jvmMetrics = new HashMap<>();
            
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
            
            // 内存指标
            jvmMetrics.put("heapMemoryUsed", memoryBean.getHeapMemoryUsage().getUsed());
            jvmMetrics.put("heapMemoryMax", memoryBean.getHeapMemoryUsage().getMax());
            jvmMetrics.put("nonHeapMemoryUsed", memoryBean.getNonHeapMemoryUsage().getUsed());
            jvmMetrics.put("nonHeapMemoryMax", memoryBean.getNonHeapMemoryUsage().getMax());
            
            // 线程指标
            jvmMetrics.put("threadCount", threadBean.getThreadCount());
            jvmMetrics.put("peakThreadCount", threadBean.getPeakThreadCount());
            jvmMetrics.put("daemonThreadCount", threadBean.getDaemonThreadCount());
            
            // GC信息
            jvmMetrics.put("gcCollectionCount", ManagementFactory.getGarbageCollectorMXBeans().stream()
                    .mapToLong(gc -> gc.getCollectionCount()).sum());
            
            return MonitorResponse.success(jvmMetrics, "JVM metrics retrieved successfully");
        } catch (Exception e) {
            log.error("Failed to get JVM metrics", e);
            return MonitorResponse.error("Failed to retrieve JVM metrics: " + e.getMessage());
        }
    }

    /**
     * 获取应用性能指标
     */
    @GetMapping("/metrics/application")
    public MonitorResponse<Map<String, Object>> getApplicationMetrics() {
        try {
            Map<String, Object> appMetrics = new HashMap<>();
            
            // 刷新JVM指标
            metricService.refreshJvmMetrics();
            
            // 获取注册表中的指标
            meterRegistry.forEachMeter(meter -> {
                String name = meter.getId().getName();
                if (name.startsWith("http.") || name.startsWith("application.")) {
                    appMetrics.put(name, meter.measure().iterator().next().getValue());
                }
            });
            
            return MonitorResponse.success(appMetrics, "Application metrics retrieved successfully");
        } catch (Exception e) {
            log.error("Failed to get application metrics", e);
            return MonitorResponse.error("Failed to retrieve application metrics: " + e.getMessage());
        }
    }

    /**
     * 获取日志监控统计
     */
    @GetMapping("/logs/statistics")
    public MonitorResponse<LogMonitorService.LogStatistics> getLogStatistics() {
        try {
            LogMonitorService.LogStatistics statistics = logMonitorService.getLogStatistics();
            return MonitorResponse.success(statistics, "Log statistics retrieved successfully");
        } catch (Exception e) {
            log.error("Failed to get log statistics", e);
            return MonitorResponse.error("Failed to retrieve log statistics: " + e.getMessage());
        }
    }

    /**
     * 检查是否需要告警
     */
    @GetMapping("/alerts/check")
    public MonitorResponse<Map<String, Object>> checkAlerts() {
        try {
            Map<String, Object> alertInfo = new HashMap<>();
            
            boolean shouldAlert = logMonitorService.shouldAlert();
            alertInfo.put("shouldAlert", shouldAlert);
            alertInfo.put("alertReason", shouldAlert ? "High error/warn log count" : "Normal");
            
            LogMonitorService.LogStatistics stats = logMonitorService.getLogStatistics();
            alertInfo.put("errorCount", stats.getTotalErrorLogs());
            alertInfo.put("warnCount", stats.getTotalWarnLogs());
            alertInfo.put("errorRate", stats.getErrorRate());
            
            return MonitorResponse.success(alertInfo, "Alert check completed");
        } catch (Exception e) {
            log.error("Failed to check alerts", e);
            return MonitorResponse.error("Failed to check alerts: " + e.getMessage());
        }
    }

    /**
     * 重置监控计数器
     */
    @PostMapping("/reset")
    public MonitorResponse<Void> resetCounters() {
        try {
            logMonitorService.resetCounters();
            return MonitorResponse.success(null, "Counters reset successfully");
        } catch (Exception e) {
            log.error("Failed to reset counters", e);
            return MonitorResponse.error("Failed to reset counters: " + e.getMessage());
        }
    }

    /**
     * 测试监控功能
     */
    @PostMapping("/test")
    public MonitorResponse<String> testMonitoring(@RequestParam(defaultValue = "testOperation") String operation) {
        try {
            // 模拟业务操作监控
            var sample = metricService.startBusinessTimer(operation);
            Thread.sleep(100); // 模拟业务处理时间
            metricService.stopBusinessTimer(sample, operation);
            metricService.incrementBusinessCounter(operation);
            
            // 模拟日志记录
            logMonitorService.recordLogEvent("INFO", "MonitorController", 
                "Test monitoring operation: " + operation, null);
            
            return MonitorResponse.success("Test completed for operation: " + operation, 
                "Monitoring test successful");
        } catch (Exception e) {
            log.error("Monitoring test failed", e);
            return MonitorResponse.error("Monitoring test failed: " + e.getMessage());
        }
    }

    /**
     * 收集系统指标
     */
    private SystemMetrics collectSystemMetrics() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        
        return SystemMetrics.builder()
                .timestamp(LocalDateTime.now())
                .heapMemoryUsed(memoryBean.getHeapMemoryUsage().getUsed())
                .heapMemoryMax(memoryBean.getHeapMemoryUsage().getMax())
                .threadCount(threadBean.getThreadCount())
                .peakThreadCount(threadBean.getPeakThreadCount())
                .daemonThreadCount(threadBean.getDaemonThreadCount())
                .gcCollectionCount(ManagementFactory.getGarbageCollectorMXBeans().stream()
                        .mapToLong(gc -> gc.getCollectionCount()).sum())
                .build();
    }
}