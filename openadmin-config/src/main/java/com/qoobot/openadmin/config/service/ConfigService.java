package com.qoobot.openadmin.config.service;

import com.qoobot.openadmin.config.dto.ConfigDTO;
import com.qoobot.openadmin.config.entity.ConfigItem;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * 配置服务接口
 * 定义配置管理的核心业务逻辑
 */
public interface ConfigService {

    /**
     * 创建配置项
     */
    ConfigDTO createConfig(ConfigDTO configDTO);

    /**
     * 更新配置项
     */
    ConfigDTO updateConfig(ConfigDTO configDTO);

    /**
     * 删除配置项
     */
    void deleteConfig(Long id);

    /**
     * 根据ID获取配置项
     */
    ConfigDTO getConfigById(Long id);

    /**
     * 根据配置键和环境获取配置值
     */
    String getConfigValue(String configKey, String environment);

    /**
     * 批量获取配置值
     */
    Map<String, String> batchGetConfigValues(List<String> configKeys, String environment);

    /**
     * 分页查询配置项
     */
    Page<ConfigDTO> getConfigsByPage(ConfigDTO.ConfigQuery query);

    /**
     * 根据分组获取配置项
     */
    List<ConfigDTO> getConfigsByGroup(Long groupId);

    /**
     * 根据环境获取配置项
     */
    List<ConfigDTO> getConfigsByEnvironment(String environment);

    /**
     * 配置热更新
     */
    void hotReloadConfig(Long configId);

    /**
     * 批量更新配置状态
     */
    void batchUpdateStatus(List<Long> ids, String status, String operator);

    /**
     * 配置发布到目标环境
     */
    void publishConfig(ConfigDTO.ConfigPublishRequest request);

    /**
     * 配置灰度发布
     */
    void grayReleaseConfig(ConfigDTO.ConfigPublishRequest request);

    /**
     * 配置回滚
     */
    ConfigDTO rollbackConfig(Long configId, Integer version, String operator);

    /**
     * 获取配置历史版本
     */
    List<ConfigDTO.ConfigVersion> getConfigHistory(Long configId);

    /**
     * 加密配置值
     */
    String encryptConfigValue(String plainValue);

    /**
     * 解密配置值
     */
    String decryptConfigValue(String encryptedValue);

    /**
     * 获取配置操作日志
     */
    List<Object> getConfigOperationLogs(Long configId, int page, int size);

    /**
     * 验证配置权限
     */
    boolean hasConfigPermission(String username, String configKey, String operation);

    /**
     * 同步配置到Nacos
     */
    void syncToNacos(Long configId);

    /**
     * 从Nacos同步配置
     */
    void syncFromNacos(String dataId, String group, String namespace);

    /**
     * 获取支持的环境列表
     */
    List<String> getEnvironments();

    /**
     * 获取配置分组列表
     */
    List<String> getConfigGroups();

    /**
     * 导出配置
     */
    String exportConfigs(ConfigDTO.ConfigQuery query);

    /**
     * 导入配置
     */
    void importConfigs(String jsonData, String operator);

    /**
     * 配置备份
     */
    void backupConfigs(String backupName, String operator);

    /**
     * 配置恢复
     */
    void restoreConfigs(String backupName, String operator);

    /**
     * 获取配置统计信息
     */
    Map<String, Object> getConfigStatistics();

    /**
     * 验证配置格式
     */
    boolean validateConfigFormat(String configValue, String configType);

    /**
     * 监控配置变更
     */
    void monitorConfigChanges(String configKey, String environment, ConfigChangeListener listener);

    /**
     * 配置变更监听器接口
     */
    interface ConfigChangeListener {
        void onConfigChanged(String configKey, String environment, String oldValue, String newValue);
    }
}