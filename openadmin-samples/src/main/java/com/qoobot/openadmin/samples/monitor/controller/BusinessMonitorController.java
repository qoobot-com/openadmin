package com.qoobot.openadmin.samples.monitor.controller;

import com.qoobot.openadmin.samples.monitor.entity.BusinessMetric;
import com.qoobot.openadmin.samples.monitor.service.BusinessMonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 业务监控控制器
 * 提供业务指标相关的REST API
 */
@RestController
@RequestMapping("/api/monitor/business")
public class BusinessMonitorController {
    
    @Autowired
    private BusinessMonitoringService businessService;
    
    /**
     * 获取业务指标概览
     */
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getBusinessOverview() {
        Map<String, Object> overview = businessService.getBusinessOverview();
        return ResponseEntity.ok(overview);
    }
    
    /**
     * 获取业务健康度评分
     */
    @GetMapping("/health-score")
    public ResponseEntity<Map<String, Object>> getBusinessHealthScore() {
        Map<String, Object> healthScore = businessService.getBusinessHealthScore();
        return ResponseEntity.ok(healthScore);
    }
    
    /**
     * 记录业务操作指标
     */
    @PostMapping("/operations")
    public ResponseEntity<Map<String, String>> recordBusinessOperation(
            @RequestParam String operationName,
            @RequestParam double value,
            @RequestParam String unit) {
        
        businessService.recordBusinessOperation(operationName, value, unit);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "业务操作指标记录成功");
        response.put("operation_name", operationName);
        response.put("value", String.valueOf(value));
        response.put("unit", unit);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 记录用户活动指标
     */
    @PostMapping("/user-activity")
    public ResponseEntity<Map<String, String>> recordUserActivity(
            @RequestParam String activityType,
            @RequestParam double value) {
        
        businessService.recordUserActivity(activityType, value);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "用户活动指标记录成功");
        response.put("activity_type", activityType);
        response.put("value", String.valueOf(value));
        return ResponseEntity.ok(response);
    }
    
    /**
     * 查询业务指标趋势
     */
    @GetMapping("/trends/{metricName}")
    public ResponseEntity<List<BusinessMetric>> getMetricTrend(
            @PathVariable String metricName,
            @RequestParam(defaultValue = "24") int hours) {
        
        List<BusinessMetric> trends = businessService.getMetricTrend(metricName, hours);
        return ResponseEntity.ok(trends);
    }
    
    /**
     * 获取特定时间段的业务指标
     */
    @GetMapping("/metrics")
    public ResponseEntity<Page<BusinessMetric>> getBusinessMetrics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String businessDomain,
            @RequestParam(required = false) String metricName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        Pageable pageable = PageRequest.of(page, size);
        
        if (startTime == null) {
            startTime = LocalDateTime.now().minusDays(1); // 默认查询最近1天
        }
        if (endTime == null) {
            endTime = LocalDateTime.now();
        }
        
        if (businessDomain != null && metricName != null) {
            // 根据业务域和指标名称查询
            List<BusinessMetric> metrics = businessService.getBusinessMetricRepository()
                .findByBusinessDomainAndMetricName(businessDomain, metricName);
            return ResponseEntity.ok(new org.springframework.data.domain.PageImpl<>(metrics));
        } else if (businessDomain != null) {
            // 根据业务域查询
            Page<BusinessMetric> metrics = businessService.getBusinessMetricRepository()
                .findByBusinessDomainAndTimestampBetween(businessDomain, startTime, endTime, pageable);
            return ResponseEntity.ok(metrics);
        } else {
            // 返回所有业务指标
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 生成模拟业务数据（测试用）
     */
    @PostMapping("/generate-mock-data")
    public ResponseEntity<Map<String, String>> generateMockData() {
        businessService.generateMockData();
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "模拟业务数据生成完成");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
}