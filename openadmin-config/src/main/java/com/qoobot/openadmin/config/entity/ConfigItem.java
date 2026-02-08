package com.qoobot.openadmin.config.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 配置项实体类
 * 实现系统配置管理的核心数据模型
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "config_items", indexes = {
    @Index(name = "idx_config_key_env", columnList = "configKey,environment"),
    @Index(name = "idx_group_id", columnList = "groupId"),
    @Index(name = "idx_status", columnList = "status")
})
public class ConfigItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 手动添加必要的getter/setter方法以解决Lombok不生效的问题
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    /**
     * 配置键名
     */
    @Column(name = "config_key", nullable = false, length = 255)
    private String configKey;
    
    public String getConfigKey() { return configKey; }
    public void setConfigKey(String configKey) { this.configKey = configKey; }

    /**
     * 配置值
     */
    @Column(name = "config_value", columnDefinition = "TEXT")
    private String configValue;
    
    public String getConfigValue() { return configValue; }
    public void setConfigValue(String configValue) { this.configValue = configValue; }

    /**
     * 配置描述
     */
    @Column(length = 500)
    private String description;
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    /**
     * 配置分组ID
     */
    @Column(name = "group_id")
    private Long groupId;
    
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    /**
     * 配置分组名称
     */
    @Column(name = "group_name", length = 100)
    private String groupName;
    
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    /**
     * 环境标识 (dev/test/prod)
     */
    @Column(length = 20)
    private String environment;
    
    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }

    /**
     * 配置类型 (STRING, NUMBER, BOOLEAN, JSON)
     */
    @Column(length = 20)
    private String configType;
    
    public String getConfigType() { return configType; }
    public void setConfigType(String configType) { this.configType = configType; }

    /**
     * 是否加密存储
     */
    @Column(name = "encrypted")
    private Boolean encrypted = false;
    
    public Boolean getEncrypted() { return encrypted; }
    public void setEncrypted(Boolean encrypted) { this.encrypted = encrypted; }

    /**
     * 配置状态 (ACTIVE, INACTIVE, DEPRECATED)
     */
    @Column(length = 20)
    private String status = "ACTIVE";
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    /**
     * 版本号
     */
    @Column(name = "version")
    private Integer version = 1;
    
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    /**
     * 标签（JSON格式存储）
     */
    @Column(columnDefinition = "TEXT")
    private String tags;
    
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    /**
     * 创建人
     */
    @Column(name = "created_by", length = 100)
    private String createdBy;
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    /**
     * 更新人
     */
    @Column(name = "updated_by", length = 100)
    private String updatedBy;
    
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    /**
     * 删除标记
     */
    @Column(name = "deleted")
    private Boolean deleted = false;
    
    public Boolean getDeleted() { return deleted; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }

    /**
     * 配置来源 (DATABASE, NACOS, CONFIG_SERVER)
     */
    @Column(name = "source", length = 20)
    private String source = "DATABASE";
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}