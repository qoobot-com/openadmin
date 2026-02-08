package com.qoobot.openadmin.samples.monitor.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 业务指标实体类
 * 用于存储业务相关的监控指标
 */
@Entity
@Table(name = "business_metrics")
public class BusinessMetric {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 业务域
     */
    @Column(name = "business_domain", nullable = false)
    private String businessDomain;
    
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
     * 维度信息（JSON格式）
     */
    @Column(name = "dimensions", columnDefinition = "TEXT")
    private String dimensions;
    
    /**
     * 描述信息
     */
    @Column(name = "description")
    private String description;
    
    /**
     * 单位
     */
    @Column(name = "unit")
    private String unit;
    
    // Constructors
    public BusinessMetric() {}
    
    public BusinessMetric(String businessDomain, String metricName, Double metricValue) {
        this.businessDomain = businessDomain;
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
    
    public String getBusinessDomain() {
        return businessDomain;
    }
    
    public void setBusinessDomain(String businessDomain) {
        this.businessDomain = businessDomain;
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
    
    public String getDimensions() {
        return dimensions;
    }
    
    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    @Override
    public String toString() {
        return "BusinessMetric{" +
                "id=" + id +
                ", businessDomain='" + businessDomain + '\'' +
                ", metricName='" + metricName + '\'' +
                ", metricValue=" + metricValue +
                ", timestamp=" + timestamp +
                '}';
    }
}