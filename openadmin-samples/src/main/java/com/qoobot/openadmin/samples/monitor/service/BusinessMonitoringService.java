package com.qoobot.openadmin.samples.monitor.service;

import com.qoobot.openadmin.samples.monitor.entity.BusinessMetric;
import com.qoobot.openadmin.samples.monitor.repository.BusinessMetricRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 业务监控服务
 * 负责收集和处理业务相关指标
 */
@Service
public class BusinessMonitoringService {
    
    @Autowired
    private BusinessMetricRepository businessMetricRepository;
    
    // 为控制器提供访问Repository的方法
    public BusinessMetricRepository getBusinessMetricRepository() {
        return businessMetricRepository;
    }
    
    private static final String BUSINESS_DOMAIN = "sample-monitor-app";
    
    /**
     * 记录业务操作指标
     */
    public void recordBusinessOperation(String operationName, double value, String unit) {
        BusinessMetric metric = new BusinessMetric();
        metric.setBusinessDomain(BUSINESS_DOMAIN);
        metric.setMetricName(operationName);
        metric.setMetricValue(value);
        metric.setUnit(unit);
        metric.setTimestamp(LocalDateTime.now());
        metric.setDescription("业务操作: " + operationName);
        
        businessMetricRepository.save(metric);
    }
    
    /**
     * 记录用户行为指标
     */
    public void recordUserActivity(String activityType, double value) {
        BusinessMetric metric = new BusinessMetric();
        metric.setBusinessDomain(BUSINESS_DOMAIN);
        metric.setMetricName("user_" + activityType);
        metric.setMetricValue(value);
        metric.setUnit("count");
        metric.setTimestamp(LocalDateTime.now());
        metric.setDescription("用户活动: " + activityType);
        
        // 添加维度信息
        Map<String, String> dimensions = new HashMap<>();
        dimensions.put("activity_type", activityType);
        dimensions.put("source", "web");
        metric.setDimensions(convertDimensionsToJson(dimensions));
        
        businessMetricRepository.save(metric);
    }
    
    /**
     * 获取业务指标概览
     */
    public Map<String, Object> getBusinessOverview() {
        Map<String, Object> overview = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneDayAgo = now.minusDays(1);
        
        // 计算今日业务总量
        Double totalOperations = businessMetricRepository.calculateSum(
            BUSINESS_DOMAIN, "business_operations", oneDayAgo, now);
        overview.put("today_total_operations", totalOperations != null ? totalOperations.intValue() : 0);
        
        // 计算活跃用户数
        Double activeUsers = businessMetricRepository.calculateSum(
            BUSINESS_DOMAIN, "user_active", oneDayAgo, now);
        overview.put("today_active_users", activeUsers != null ? activeUsers.intValue() : 0);
        
        // 获取最新的业务指标
        Pageable pageable = PageRequest.of(0, 5);
        List<BusinessMetric> recentMetrics = businessMetricRepository
            .findLatestByBusinessDomain(BUSINESS_DOMAIN, pageable);
        overview.put("recent_business_metrics", recentMetrics);
        
        return overview;
    }
    
    /**
     * 获取特定业务指标的趋势数据
     */
    public List<BusinessMetric> getMetricTrend(String metricName, int hours) {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusHours(hours);
        
        Pageable pageable = PageRequest.of(0, 100); // 限制返回数据量
        Page<BusinessMetric> pageResult = businessMetricRepository.findByBusinessDomainAndTimestampBetween(
            BUSINESS_DOMAIN, startTime, endTime, pageable);
        return pageResult.getContent();
    }
    
    /**
     * 定时生成模拟业务数据（每5分钟执行一次）
     */
    @Scheduled(fixedRate = 300000)
    public void generateMockData() {
        // 模拟业务操作
        recordBusinessOperation("business_operations", Math.random() * 100 + 50, "count");
        
        // 模拟用户活动
        recordUserActivity("login", Math.random() * 20 + 5);
        recordUserActivity("page_view", Math.random() * 200 + 100);
        recordUserActivity("api_call", Math.random() * 500 + 200);
        
        // 模拟业务成功率
        recordBusinessOperation("success_rate", Math.random() * 20 + 80, "percentage");
    }
    
    /**
     * 获取业务健康度评分
     */
    public Map<String, Object> getBusinessHealthScore() {
        Map<String, Object> healthScore = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourAgo = now.minusHours(1);
        
        // 计算各项指标得分
        Double successRate = businessMetricRepository.calculateAverage(
            BUSINESS_DOMAIN, "success_rate", oneHourAgo, now);
        
        Double userActivity = businessMetricRepository.calculateAverage(
            BUSINESS_DOMAIN, "user_page_view", oneHourAgo, now);
        
        // 综合评分计算
        double score = 0.0;
        if (successRate != null) {
            score += successRate * 0.6; // 成功率占60%
        }
        if (userActivity != null && userActivity > 0) {
            score += Math.min(userActivity / 10, 40); // 用户活跃度占40%
        }
        
        healthScore.put("score", Math.round(score * 100.0) / 100.0);
        healthScore.put("level", getHealthLevel(score));
        healthScore.put("success_rate", successRate);
        healthScore.put("user_activity", userActivity);
        
        return healthScore;
    }
    
    // 私有辅助方法
    
    private String convertDimensionsToJson(Map<String, String> dimensions) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, String> entry : dimensions.entrySet()) {
            if (!first) {
                json.append(",");
            }
            json.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\"");
            first = false;
        }
        json.append("}");
        return json.toString();
    }
    
    private String getHealthLevel(double score) {
        if (score >= 90) return "EXCELLENT";
        if (score >= 80) return "GOOD";
        if (score >= 70) return "FAIR";
        if (score >= 60) return "POOR";
        return "CRITICAL";
    }
}