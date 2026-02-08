package com.qoobot.openadmin.samples.monitor.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 监控告警实体类
 * 用于存储监控告警信息
 */
@Entity
@Table(name = "monitor_alerts")
public class MonitorAlert {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 告警名称
     */
    @Column(name = "alert_name", nullable = false)
    private String alertName;
    
    /**
     * 告警级别（CRITICAL, HIGH, MEDIUM, LOW）
     */
    @Column(name = "severity", nullable = false)
    private String severity;
    
    /**
     * 告警状态（ACTIVE, RESOLVED, ACKNOWLEDGED）
     */
    @Column(name = "status", nullable = false)
    private String status;
    
    /**
     * 告警消息
     */
    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;
    
    /**
     * 触发时间
     */
    @Column(name = "trigger_time", nullable = false)
    private LocalDateTime triggerTime;
    
    /**
     * 解决时间
     */
    @Column(name = "resolve_time")
    private LocalDateTime resolveTime;
    
    /**
     * 确认时间
     */
    @Column(name = "acknowledge_time")
    private LocalDateTime acknowledgeTime;
    
    /**
     * 相关指标信息（JSON格式）
     */
    @Column(name = "related_metrics", columnDefinition = "TEXT")
    private String relatedMetrics;
    
    /**
     * 告警规则ID
     */
    @Column(name = "rule_id")
    private String ruleId;
    
    /**
     * 应用名称
     */
    @Column(name = "application_name")
    private String applicationName;
    
    /**
     * 实例ID
     */
    @Column(name = "instance_id")
    private String instanceId;
    
    // Constructors
    public MonitorAlert() {}
    
    public MonitorAlert(String alertName, String severity, String message) {
        this.alertName = alertName;
        this.severity = severity;
        this.message = message;
        this.status = "ACTIVE";
        this.triggerTime = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getAlertName() {
        return alertName;
    }
    
    public void setAlertName(String alertName) {
        this.alertName = alertName;
    }
    
    public String getSeverity() {
        return severity;
    }
    
    public void setSeverity(String severity) {
        this.severity = severity;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public LocalDateTime getTriggerTime() {
        return triggerTime;
    }
    
    public void setTriggerTime(LocalDateTime triggerTime) {
        this.triggerTime = triggerTime;
    }
    
    public LocalDateTime getResolveTime() {
        return resolveTime;
    }
    
    public void setResolveTime(LocalDateTime resolveTime) {
        this.resolveTime = resolveTime;
    }
    
    public LocalDateTime getAcknowledgeTime() {
        return acknowledgeTime;
    }
    
    public void setAcknowledgeTime(LocalDateTime acknowledgeTime) {
        this.acknowledgeTime = acknowledgeTime;
    }
    
    public String getRelatedMetrics() {
        return relatedMetrics;
    }
    
    public void setRelatedMetrics(String relatedMetrics) {
        this.relatedMetrics = relatedMetrics;
    }
    
    public String getRuleId() {
        return ruleId;
    }
    
    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }
    
    public String getApplicationName() {
        return applicationName;
    }
    
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
    
    public String getInstanceId() {
        return instanceId;
    }
    
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
    
    @Override
    public String toString() {
        return "MonitorAlert{" +
                "id=" + id +
                ", alertName='" + alertName + '\'' +
                ", severity='" + severity + '\'' +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", triggerTime=" + triggerTime +
                '}';
    }
}