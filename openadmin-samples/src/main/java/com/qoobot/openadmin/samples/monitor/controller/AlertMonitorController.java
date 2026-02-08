package com.qoobot.openadmin.samples.monitor.controller;

import com.qoobot.openadmin.samples.monitor.entity.MonitorAlert;
import com.qoobot.openadmin.samples.monitor.service.AlertMonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 监控告警控制器
 * 提供告警相关的REST API
 */
@RestController
@RequestMapping("/api/monitor/alerts")
public class AlertMonitorController {
    
    @Autowired
    private AlertMonitoringService alertService;
    
    /**
     * 获取活跃告警列表
     */
    @GetMapping("/active")
    public ResponseEntity<List<MonitorAlert>> getActiveAlerts() {
        List<MonitorAlert> alerts = alertService.getActiveAlerts();
        return ResponseEntity.ok(alerts);
    }
    
    /**
     * 获取告警统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getAlertStatistics() {
        Map<String, Object> statistics = alertService.getAlertStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * 获取告警历史（分页）
     */
    @GetMapping("/history")
    public ResponseEntity<Page<MonitorAlert>> getAlertHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<MonitorAlert> alerts = alertService.getAlertHistory(page, size);
        return ResponseEntity.ok(alerts);
    }
    
    /**
     * 根据ID获取告警详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<MonitorAlert> getAlertById(@PathVariable Long id) {
        MonitorAlert alert = alertService.getAlertRepository().findById(id).orElse(null);
        if (alert != null) {
            return ResponseEntity.ok(alert);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 确认告警
     */
    @PutMapping("/{id}/acknowledge")
    public ResponseEntity<Map<String, Object>> acknowledgeAlert(@PathVariable Long id) {
        MonitorAlert alert = alertService.acknowledgeAlert(id);
        if (alert != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "告警确认成功");
            response.put("alert_id", alert.getId());
            response.put("status", alert.getStatus());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 解决告警
     */
    @PutMapping("/{id}/resolve")
    public ResponseEntity<Map<String, Object>> resolveAlert(@PathVariable Long id) {
        MonitorAlert alert = alertService.resolveAlert(id);
        if (alert != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "告警解决成功");
            response.put("alert_id", alert.getId());
            response.put("status", alert.getStatus());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 手动创建告警
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createAlert(
            @RequestParam String alertName,
            @RequestParam String severity,
            @RequestParam String message) {
        
        MonitorAlert alert = alertService.createAlert(alertName, severity, message);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "告警创建成功");
        response.put("alert_id", alert.getId());
        response.put("alert_name", alert.getAlertName());
        response.put("severity", alert.getSeverity());
        return ResponseEntity.ok(response);
    }
    
    /**
     * 手动触发告警检查
     */
    @PostMapping("/check")
    public ResponseEntity<Map<String, Object>> triggerAlertCheck() {
        alertService.checkAndTriggerAlerts();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "告警检查完成");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
    
    /**
     * 根据状态查询告警
     */
    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<MonitorAlert>> getAlertsByStatus(@PathVariable String status) {
        List<MonitorAlert> alerts = alertService.getAlertRepository().findByStatus(status);
        return ResponseEntity.ok(alerts);
    }
    
    /**
     * 根据严重程度查询告警
     */
    @GetMapping("/by-severity/{severity}")
    public ResponseEntity<List<MonitorAlert>> getAlertsBySeverity(@PathVariable String severity) {
        List<MonitorAlert> alerts = alertService.getAlertRepository().findBySeverity(severity);
        return ResponseEntity.ok(alerts);
    }
    
    /**
     * 搜索告警（按名称模糊查询）
     */
    @GetMapping("/search")
    public ResponseEntity<List<MonitorAlert>> searchAlerts(@RequestParam String keyword) {
        List<MonitorAlert> alerts = alertService.getAlertRepository()
            .findByAlertNameContainingIgnoreCase(keyword);
        return ResponseEntity.ok(alerts);
    }
}