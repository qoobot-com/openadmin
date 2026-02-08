package com.qoobot.openadmin.config.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 配置传输对象
 * 用于前后端数据传输和业务逻辑处理
 */
@Data
public class ConfigDTO {

    private Long id;
    
    // 手动添加必要的getter/setter方法
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    /**
     * 配置键名
     */
    @NotBlank(message = "配置键名不能为空")
    @Size(max = 255, message = "配置键名长度不能超过255个字符")
    private String configKey;
    
    public String getConfigKey() { return configKey; }
    public void setConfigKey(String configKey) { this.configKey = configKey; }

    /**
     * 配置值
     */
    private String configValue;
    
    public String getConfigValue() { return configValue; }
    public void setConfigValue(String configValue) { this.configValue = configValue; }

    /**
     * 配置描述
     */
    @Size(max = 500, message = "配置描述长度不能超过500个字符")
    private String description;
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    /**
     * 配置分组ID
     */
    private Long groupId;
    
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    /**
     * 配置分组名称
     */
    @Size(max = 100, message = "分组名称长度不能超过100个字符")
    private String groupName;
    
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    /**
     * 环境标识
     */
    @NotBlank(message = "环境标识不能为空")
    @Size(max = 20, message = "环境标识长度不能超过20个字符")
    private String environment;
    
    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }

    /**
     * 配置类型
     */
    @NotBlank(message = "配置类型不能为空")
    @Size(max = 20, message = "配置类型长度不能超过20个字符")
    private String configType;
    
    public String getConfigType() { return configType; }
    public void setConfigType(String configType) { this.configType = configType; }

    /**
     * 是否加密
     */
    private Boolean encrypted = false;
    
    public Boolean getEncrypted() { return encrypted; }
    public void setEncrypted(Boolean encrypted) { this.encrypted = encrypted; }

    /**
     * 配置状态
     */
    @Size(max = 20, message = "状态长度不能超过20个字符")
    private String status = "ACTIVE";
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    /**
     * 版本号
     */
    private Integer version = 1;
    
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    /**
     * 标签
     */
    private Map<String, String> tags;
    
    public Map<String, String> getTags() { return tags; }
    public void setTags(Map<String, String> tags) { this.tags = tags; }

    /**
     * 创建人
     */
    @Size(max = 100, message = "创建人长度不能超过100个字符")
    private String createdBy;
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    /**
     * 更新人
     */
    @Size(max = 100, message = "更新人长度不能超过100个字符")
    private String updatedBy;
    
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    /**
     * 数据源
     */
    @Size(max = 20, message = "数据源长度不能超过20个字符")
    private String source = "DATABASE";
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    /**
     * 批量操作时使用的配置列表
     */
    private java.util.List<ConfigDTO> configs;
    
    public java.util.List<ConfigDTO> getConfigs() { return configs; }
    public void setConfigs(java.util.List<ConfigDTO> configs) { this.configs = configs; }

    /**
     * 配置查询参数
     */
    @Data
    public static class ConfigQuery {
        private String configKey;
        private String groupName;
        private String environment;
        private String status;
        private String configType;
        private Boolean encrypted;
        private String keyword; // 搜索关键词
        private Integer page = 0;
        private Integer size = 10;
        
        // 手动添加getter/setter方法
        public String getConfigKey() { return configKey; }
        public void setConfigKey(String configKey) { this.configKey = configKey; }
        
        public String getGroupName() { return groupName; }
        public void setGroupName(String groupName) { this.groupName = groupName; }
        
        public String getEnvironment() { return environment; }
        public void setEnvironment(String environment) { this.environment = environment; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getConfigType() { return configType; }
        public void setConfigType(String configType) { this.configType = configType; }
        
        public Boolean getEncrypted() { return encrypted; }
        public void setEncrypted(Boolean encrypted) { this.encrypted = encrypted; }
        
        public String getKeyword() { return keyword; }
        public void setKeyword(String keyword) { this.keyword = keyword; }
        
        public Integer getPage() { return page; }
        public void setPage(Integer page) { this.page = page; }
        
        public Integer getSize() { return size; }
        public void setSize(Integer size) { this.size = size; }
    }

    /**
     * 配置版本信息
     */
    @Data
    public static class ConfigVersion {
        private Long configId;
        private Integer version;
        private String configValue;
        private String description;
        private String updatedBy;
        private LocalDateTime updatedAt;
        
        // 手动添加getter/setter方法
        public Long getConfigId() { return configId; }
        public void setConfigId(Long configId) { this.configId = configId; }
        
        public Integer getVersion() { return version; }
        public void setVersion(Integer version) { this.version = version; }
        
        public String getConfigValue() { return configValue; }
        public void setConfigValue(String configValue) { this.configValue = configValue; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getUpdatedBy() { return updatedBy; }
        public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
        
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }

    /**
     * 配置变更请求
     */
    @Data
    public static class ConfigChangeRequest {
        @NotNull(message = "配置ID不能为空")
        private Long configId;
        
        private String oldValue;
        private String newValue;
        private String changeReason;
        private String operator;
        
        // 手动添加getter/setter方法
        public Long getConfigId() { return configId; }
        public void setConfigId(Long configId) { this.configId = configId; }
        
        public String getOldValue() { return oldValue; }
        public void setOldValue(String oldValue) { this.oldValue = oldValue; }
        
        public String getNewValue() { return newValue; }
        public void setNewValue(String newValue) { this.newValue = newValue; }
        
        public String getChangeReason() { return changeReason; }
        public void setChangeReason(String changeReason) { this.changeReason = changeReason; }
        
        public String getOperator() { return operator; }
        public void setOperator(String operator) { this.operator = operator; }
    }

    /**
     * 配置发布请求
     */
    @Data
    public static class ConfigPublishRequest {
        @NotNull(message = "配置ID不能为空")
        private Long configId;
        
        private String environment;
        private String targetEnvironment;
        private Boolean grayRelease = false;
        private Double grayRatio = 0.0;
        private String publishReason;
        private String publisher;
        
        // 手动添加getter/setter方法
        public Long getConfigId() { return configId; }
        public void setConfigId(Long configId) { this.configId = configId; }
        
        public String getEnvironment() { return environment; }
        public void setEnvironment(String environment) { this.environment = environment; }
        
        public String getTargetEnvironment() { return targetEnvironment; }
        public void setTargetEnvironment(String targetEnvironment) { this.targetEnvironment = targetEnvironment; }
        
        public Boolean getGrayRelease() { return grayRelease; }
        public void setGrayRelease(Boolean grayRelease) { this.grayRelease = grayRelease; }
        
        public Double getGrayRatio() { return grayRatio; }
        public void setGrayRatio(Double grayRatio) { this.grayRatio = grayRatio; }
        
        public String getPublishReason() { return publishReason; }
        public void setPublishReason(String publishReason) { this.publishReason = publishReason; }
        
        public String getPublisher() { return publisher; }
        public void setPublisher(String publisher) { this.publisher = publisher; }
    }
}