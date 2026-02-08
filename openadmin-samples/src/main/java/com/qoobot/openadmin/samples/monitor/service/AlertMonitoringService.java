package com.qoobot.openadmin.samples.monitor.service;

import com.qoobot.openadmin.samples.monitor.entity.MonitorAlert;
import com.qoobot.openadmin.samples.monitor.repository.MonitorAlertRepository;
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
 * 监控告警服务
 * 负责告警的检测、触发和管理
 */
@Service
public class AlertMonitoringService {
    
    @Autowired
    private MonitorAlertRepository alertRepository;
    
    // 为控制器提供访问Repository的方法
    public MonitorAlertRepository getAlertRepository() {
        return alertRepository;
    }
    
    @Autowired
    private PerformanceMonitoringService performanceService;
    
    @Autowired
    private BusinessMonitoringService businessService;
    
    private static final String APPLICATION_NAME = "sample-monitor-app";
    
    /**
     * 检查并触发告警
     */
    public void checkAndTriggerAlerts() {
        checkMemoryAlert();
        checkThreadAlert();
        checkBusinessHealthAlert();
    }
    
    /**
     * 创建告警
     */
    public MonitorAlert createAlert(String alertName, String severity, String message) {
        MonitorAlert alert = new MonitorAlert(alertName, severity, message);
        alert.setApplicationName(APPLICATION_NAME);
        return alertRepository.save(alert);
    }
    
    /**
     * 确认告警
     */
    public MonitorAlert acknowledgeAlert(Long alertId) {
        MonitorAlert alert = alertRepository.findById(alertId).orElse(null);
        if (alert != null && "ACTIVE".equals(alert.getStatus())) {
            alert.setStatus("ACKNOWLEDGED");
            alert.setAcknowledgeTime(LocalDateTime.now());
            return alertRepository.save(alert);
        }
        return null;
    }
    
    /**
     * 解决告警
     */
    public MonitorAlert resolveAlert(Long alertId) {
        MonitorAlert alert = alertRepository.findById(alertId).orElse(null);
        if (alert != null && !"RESOLVED".equals(alert.getStatus())) {
            alert.setStatus("RESOLVED");
            alert.setResolveTime(LocalDateTime.now());
            return alertRepository.save(alert);
        }
        return null;
    }
    
    /**
     * 获取活跃告警列表
     */
    public List<MonitorAlert> getActiveAlerts() {
        return alertRepository.findActiveAlerts();
    }
    
    /**
     * 获取告警统计信息
     */
    public Map<String, Object> getAlertStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 按状态统计
        List<Object[]> statusCounts = alertRepository.countByStatus();
        Map<String, Integer> statusMap = new HashMap<>();
        for (Object[] row : statusCounts) {
            statusMap.put((String) row[0], ((Long) row[1]).intValue());
        }
        statistics.put("by_status", statusMap);
        
        // 按严重程度统计
        List<Object[]> severityCounts = alertRepository.countBySeverity();
        Map<String, Integer> severityMap = new HashMap<>();
        for (Object[] row : severityCounts) {
            severityMap.put((String) row[0], ((Long) row[1]).intValue());
        }
        statistics.put("by_severity", severityMap);
        
        // 未确认告警数量
        int unacknowledgedCount = alertRepository.findUnacknowledgedAlerts().size();
        statistics.put("unacknowledged_count", unacknowledgedCount);
        
        return statistics;
    }
    
    /**
     * 分页查询告警历史
     */
    public Page<MonitorAlert> getAlertHistory(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return alertRepository.findByTriggerTimeBetween(thirtyDaysAgo, LocalDateTime.now(), pageable);
    }
    
    /**
     * 定时检查告警（每2分钟执行一次）
     */
    @Scheduled(fixedRate = 120000)
    public void scheduledAlertCheck() {
        checkAndTriggerAlerts();
    }
    
    /**
     * 清理过期告警（每天执行一次）
     */
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行
    public void cleanupExpiredAlerts() {
        LocalDateTime ninetyDaysAgo = LocalDateTime.now().minusDays(90);
        // 实际应用中可以删除或归档90天前的已解决告警
    }
    
    // 私有告警检查方法
    
    private void checkMemoryAlert() {
        Map<String, Object> overview = performanceService.getPerformanceOverview();
        Double memoryUsageRate = (Double) overview.get("memory_usage_rate");
        
        if (memoryUsageRate != null && memoryUsageRate > 85) {
            String severity = memoryUsageRate > 95 ? "CRITICAL" : "HIGH";
            String message = String.format("内存使用率过高: %.2f%%", memoryUsageRate);
            
            // 检查是否已有相同类型的活跃告警
            if (!hasActiveAlert("MEMORY_HIGH_USAGE")) {
                createAlert("MEMORY_HIGH_USAGE", severity, message);
            }
        } else {
            // 如果内存恢复正常，解决相关告警
            resolveRelatedAlerts("MEMORY_HIGH_USAGE");
        }
    }
    
    private void checkThreadAlert() {
        Map<String, Object> overview = performanceService.getPerformanceOverview();
        Integer threadCount = (Integer) overview.get("average_thread_count");
        
        if (threadCount != null && threadCount > 200) {
            String message = String.format("线程数过多: %d", threadCount);
            
            if (!hasActiveAlert("THREAD_COUNT_HIGH")) {
                createAlert("THREAD_COUNT_HIGH", "MEDIUM", message);
            }
        } else {
            resolveRelatedAlerts("THREAD_COUNT_HIGH");
        }
    }
    
    private void checkBusinessHealthAlert() {
        Map<String, Object> healthScore = businessService.getBusinessHealthScore();
        Double score = (Double) healthScore.get("score");
        
        if (score != null && score < 70) {
            String level = (String) healthScore.get("level");
            String message = String.format("业务健康度评分较低: %.2f (%s)", score, level);
            
            if (!hasActiveAlert("BUSINESS_HEALTH_LOW")) {
                createAlert("BUSINESS_HEALTH_LOW", "HIGH", message);
            }
        } else {
            resolveRelatedAlerts("BUSINESS_HEALTH_LOW");
        }
    }
    
    private boolean hasActiveAlert(String alertName) {
        List<MonitorAlert> activeAlerts = alertRepository.findActiveAlerts();
        return activeAlerts.stream()
            .anyMatch(alert -> alertName.equals(alert.getAlertName()));
    }
    
    private void resolveRelatedAlerts(String alertName) {
        List<MonitorAlert> activeAlerts = alertRepository.findActiveAlerts();
        activeAlerts.stream()
            .filter(alert -> alertName.equals(alert.getAlertName()))
            .forEach(alert -> {
                alert.setStatus("RESOLVED");
                alert.setResolveTime(LocalDateTime.now());
                alertRepository.save(alert);
            });
    }
}