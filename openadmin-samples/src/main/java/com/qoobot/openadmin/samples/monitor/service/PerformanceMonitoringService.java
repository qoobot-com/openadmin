package com.qoobot.openadmin.samples.monitor.service;

import com.qoobot.openadmin.samples.monitor.entity.PerformanceMetric;
import com.qoobot.openadmin.samples.monitor.repository.PerformanceMetricRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 性能监控服务
 * 负责收集和处理应用性能指标
 */
@Service
public class PerformanceMonitoringService {
    
    @Autowired
    private PerformanceMetricRepository performanceMetricRepository;
    
    // 为控制器提供访问Repository的方法
    public PerformanceMetricRepository getPerformanceMetricRepository() {
        return performanceMetricRepository;
    }
    
    private final String applicationName = "sample-monitor-app";
    private final String instanceId = UUID.randomUUID().toString();
    
    /**
     * 收集JVM性能指标
     */
    public void collectJvmMetrics() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        
        // 收集堆内存使用情况
        collectMemoryMetric("heap_used", memoryBean.getHeapMemoryUsage().getUsed());
        collectMemoryMetric("heap_max", memoryBean.getHeapMemoryUsage().getMax());
        collectMemoryMetric("non_heap_used", memoryBean.getNonHeapMemoryUsage().getUsed());
        
        // 收集线程信息
        collectThreadMetric("thread_count", threadBean.getThreadCount());
        collectThreadMetric("peak_thread_count", threadBean.getPeakThreadCount());
        
        // 收集垃圾回收信息
        collectGcMetric();
    }
    
    /**
     * 收集自定义业务指标
     */
    public void collectCustomMetric(String metricName, double value, Map<String, String> tags) {
        PerformanceMetric metric = new PerformanceMetric();
        metric.setApplicationName(applicationName);
        metric.setInstanceId(instanceId);
        metric.setMetricType("custom");
        metric.setMetricName(metricName);
        metric.setMetricValue(value);
        metric.setTimestamp(LocalDateTime.now());
        
        // 构建标签JSON
        if (tags != null && !tags.isEmpty()) {
            metric.setTags(convertTagsToJson(tags));
        }
        
        performanceMetricRepository.save(metric);
    }
    
    /**
     * 获取应用性能概览
     */
    public Map<String, Object> getPerformanceOverview() {
        Map<String, Object> overview = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourAgo = now.minusHours(1);
        
        // 内存使用率
        Double heapUsedAvg = performanceMetricRepository.calculateAverage(
            applicationName, "heap_used", oneHourAgo, now);
        Double heapMaxAvg = performanceMetricRepository.calculateAverage(
            applicationName, "heap_max", oneHourAgo, now);
        
        if (heapUsedAvg != null && heapMaxAvg != null && heapMaxAvg > 0) {
            double memoryUsageRate = (heapUsedAvg / heapMaxAvg) * 100;
            overview.put("memory_usage_rate", Math.round(memoryUsageRate * 100.0) / 100.0);
        }
        
        // 线程数
        Double threadCountAvg = performanceMetricRepository.calculateAverage(
            applicationName, "thread_count", oneHourAgo, now);
        if (threadCountAvg != null) {
            overview.put("average_thread_count", Math.round(threadCountAvg));
        }
        
        // 最近指标数据
        Pageable pageable = PageRequest.of(0, 10);
        List<PerformanceMetric> recentMetrics = performanceMetricRepository
            .findLatestByApplicationName(applicationName, pageable);
        overview.put("recent_metrics", recentMetrics);
        
        return overview;
    }
    
    /**
     * 定时收集性能指标（每分钟执行一次）
     */
    @Scheduled(fixedRate = 60000)
    public void scheduledCollection() {
        collectJvmMetrics();
    }
    
    // 私有辅助方法
    
    private void collectMemoryMetric(String metricName, long value) {
        PerformanceMetric metric = new PerformanceMetric();
        metric.setApplicationName(applicationName);
        metric.setInstanceId(instanceId);
        metric.setMetricType("memory");
        metric.setMetricName(metricName);
        metric.setMetricValue((double) value);
        metric.setTimestamp(LocalDateTime.now());
        performanceMetricRepository.save(metric);
    }
    
    private void collectThreadMetric(String metricName, int value) {
        PerformanceMetric metric = new PerformanceMetric();
        metric.setApplicationName(applicationName);
        metric.setInstanceId(instanceId);
        metric.setMetricType("thread");
        metric.setMetricName(metricName);
        metric.setMetricValue((double) value);
        metric.setTimestamp(LocalDateTime.now());
        performanceMetricRepository.save(metric);
    }
    
    private void collectGcMetric() {
        // 简化的GC指标收集
        // 实际应用中可以从GarbageCollectorMXBean获取详细信息
        PerformanceMetric metric = new PerformanceMetric();
        metric.setApplicationName(applicationName);
        metric.setInstanceId(instanceId);
        metric.setMetricType("gc");
        metric.setMetricName("gc_count");
        metric.setMetricValue(Math.random() * 10); // 模拟GC次数
        metric.setTimestamp(LocalDateTime.now());
        performanceMetricRepository.save(metric);
    }
    
    private String convertTagsToJson(Map<String, String> tags) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, String> entry : tags.entrySet()) {
            if (!first) {
                json.append(",");
            }
            json.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\"");
            first = false;
        }
        json.append("}");
        return json.toString();
    }
}