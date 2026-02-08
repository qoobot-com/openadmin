package com.qoobot.openadmin.samples.monitor.controller;

import com.qoobot.openadmin.samples.monitor.entity.PerformanceMetric;
import com.qoobot.openadmin.samples.monitor.service.PerformanceMonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 性能监控控制器
 * 提供性能指标相关的REST API
 */
@RestController
@RequestMapping("/api/monitor/performance")
public class PerformanceMonitorController {
    
    @Autowired
    private PerformanceMonitoringService performanceService;
    
    /**
     * 获取实时性能概览
     */
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getPerformanceOverview() {
        Map<String, Object> overview = performanceService.getPerformanceOverview();
        return ResponseEntity.ok(overview);
    }
    
    /**
     * 手动触发性能指标收集
     */
    @PostMapping("/collect")
    public ResponseEntity<Map<String, String>> collectMetrics() {
        performanceService.collectJvmMetrics();
        Map<String, String> response = new HashMap<>();
        response.put("message", "性能指标收集完成");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
    
    /**
     * 记录自定义性能指标
     */
    @PostMapping("/metrics/custom")
    public ResponseEntity<Map<String, String>> recordCustomMetric(
            @RequestParam String name,
            @RequestParam double value,
            @RequestParam(required = false) String tags) {
        
        Map<String, String> tagMap = new HashMap<>();
        if (tags != null && !tags.isEmpty()) {
            String[] tagPairs = tags.split(",");
            for (String tagPair : tagPairs) {
                String[] parts = tagPair.split(":");
                if (parts.length == 2) {
                    tagMap.put(parts[0].trim(), parts[1].trim());
                }
            }
        }
        
        performanceService.collectCustomMetric(name, value, tagMap);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "自定义指标记录成功");
        response.put("metric_name", name);
        response.put("value", String.valueOf(value));
        return ResponseEntity.ok(response);
    }
    
    /**
     * 查询性能指标历史数据
     */
    @GetMapping("/metrics/history")
    public ResponseEntity<Page<PerformanceMetric>> getMetricsHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String applicationName,
            @RequestParam(required = false) String metricType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        Pageable pageable = PageRequest.of(page, size);
        
        if (startTime == null) {
            startTime = LocalDateTime.now().minusDays(7); // 默认查询最近7天
        }
        if (endTime == null) {
            endTime = LocalDateTime.now();
        }
        
        if (applicationName != null && metricType != null) {
            // 根据应用名和指标类型查询
            List<PerformanceMetric> metrics = performanceService.getPerformanceMetricRepository()
                .findByApplicationNameAndMetricType(applicationName, metricType);
            return ResponseEntity.ok(new org.springframework.data.domain.PageImpl<>(metrics));
        } else if (applicationName != null) {
            // 根据应用名查询
            Page<PerformanceMetric> metrics = performanceService.getPerformanceMetricRepository()
                .findByApplicationNameAndTimestampBetween(applicationName, startTime, endTime, pageable);
            return ResponseEntity.ok(metrics);
        } else {
            // 返回所有指标（实际应用中可能需要限制）
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 获取JVM内存信息
     */
    @GetMapping("/jvm/memory")
    public ResponseEntity<Map<String, Object>> getJvmMemoryInfo() {
        Map<String, Object> memoryInfo = new HashMap<>();
        
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        memoryInfo.put("max_memory_mb", maxMemory / (1024 * 1024));
        memoryInfo.put("total_memory_mb", totalMemory / (1024 * 1024));
        memoryInfo.put("free_memory_mb", freeMemory / (1024 * 1024));
        memoryInfo.put("used_memory_mb", usedMemory / (1024 * 1024));
        memoryInfo.put("memory_usage_percent", (usedMemory * 100.0) / totalMemory);
        
        return ResponseEntity.ok(memoryInfo);
    }
    
    /**
     * 获取线程信息
     */
    @GetMapping("/jvm/threads")
    public ResponseEntity<Map<String, Object>> getJvmThreadInfo() {
        Map<String, Object> threadInfo = new HashMap<>();
        
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        
        threadInfo.put("thread_count", threadBean.getThreadCount());
        threadInfo.put("peak_thread_count", threadBean.getPeakThreadCount());
        threadInfo.put("daemon_thread_count", threadBean.getDaemonThreadCount());
        threadInfo.put("total_started_thread_count", threadBean.getTotalStartedThreadCount());
        
        return ResponseEntity.ok(threadInfo);
    }
    

}