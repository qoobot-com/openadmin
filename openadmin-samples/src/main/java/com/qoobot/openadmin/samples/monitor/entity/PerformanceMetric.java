package com.qoobot.openadmin.samples.monitor.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 性能指标实体类
 * 用于存储应用性能监控数据
 */
@Entity
@Table(name = "performance_metrics")
public class PerformanceMetric {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 应用名称
     */
    @Column(name = "application_name", nullable = false)
    private String applicationName;
    
    /**
     * 指标类型
     */
    @Column(name = "metric_type", nullable = false)
    private String metricType;
    
    /**
     * 指标名称
     */
    @Column(name = "metric_name", nullable = false)
    private String metricName;
    
    /**
     * 指标值
     */
    @Column(name = "metric_value", nullable = false)
    private Double metricValue;
    
    /**
     * 时间戳
     */
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    /**
     * 标签信息（JSON格式）
     */
    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags;
    
    /**
     * 实例ID
     */
    @Column(name = "instance_id")
    private String instanceId;
    
    // Constructors
    public PerformanceMetric() {}
    
    public PerformanceMetric(String applicationName, String metricType, 
                           String metricName, Double metricValue) {
        this.applicationName = applicationName;
        this.metricType = metricType;
        this.metricName = metricName;
        this.metricValue = metricValue;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getApplicationName() {
        return applicationName;
    }
    
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
    
    public String getMetricType() {
        return metricType;
    }
    
    public void setMetricType(String metricType) {
        this.metricType = metricType;
    }
    
    public String getMetricName() {
        return metricName;
    }
    
    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }
    
    public Double getMetricValue() {
        return metricValue;
    }
    
    public void setMetricValue(Double metricValue) {
        this.metricValue = metricValue;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getTags() {
        return tags;
    }
    
    public void setTags(String tags) {
        this.tags = tags;
    }
    
    public String getInstanceId() {
        return instanceId;
    }
    
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
    
    @Override
    public String toString() {
        return "PerformanceMetric{" +
                "id=" + id +
                ", applicationName='" + applicationName + '\'' +
                ", metricType='" + metricType + '\'' +
                ", metricName='" + metricName + '\'' +
                ", metricValue=" + metricValue +
                ", timestamp=" + timestamp +
                '}';
    }
}