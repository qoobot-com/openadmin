package com.qoobot.openadmin.config.repository;

import com.qoobot.openadmin.config.entity.ConfigItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 配置仓储接口
 * 定义配置管理的数据访问规范
 */
public interface ConfigRepository {

    /**
     * 保存配置项
     */
    ConfigItem save(ConfigItem configItem);

    /**
     * 根据ID查找配置项
     */
    Optional<ConfigItem> findById(Long id);

    /**
     * 根据配置键和环境查找配置项
     */
    Optional<ConfigItem> findByConfigKeyAndEnvironment(String configKey, String environment);

    /**
     * 查找所有配置项
     */
    List<ConfigItem> findAll();

    /**
     * 分页查找配置项
     */
    Page<ConfigItem> findAll(Pageable pageable);

    /**
     * 根据条件查找配置项
     */
    List<ConfigItem> findByCondition(String configKey, String groupName, String environment, 
                                   String status, String configType, Boolean encrypted, String keyword);

    /**
     * 根据分组ID查找配置项
     */
    List<ConfigItem> findByGroupId(Long groupId);

    /**
     * 根据环境查找配置项
     */
    List<ConfigItem> findByEnvironment(String environment);

    /**
     * 删除配置项
     */
    void deleteById(Long id);

    /**
     * 批量保存配置项
     */
    List<ConfigItem> saveAll(List<ConfigItem> configItems);

    /**
     * 批量删除配置项
     */
    void deleteAllById(List<Long> ids);

    /**
     * 检查配置键是否存在
     */
    boolean existsByConfigKeyAndEnvironment(String configKey, String environment);

    /**
     * 统计配置项数量
     */
    long count();

    /**
     * 根据条件统计配置项数量
     */
    long countByCondition(String configKey, String groupName, String environment, 
                         String status, String configType, Boolean encrypted, String keyword);

    /**
     * 更新配置项状态
     */
    int updateStatus(List<Long> ids, String status, String updatedBy);

    /**
     * 获取配置项历史版本
     */
    List<ConfigItem> findHistoryVersions(Long configId);

    /**
     * 配置热更新通知
     */
    void notifyConfigChanged(String configKey, String environment);

    /**
     * 批量发布配置到指定环境
     */
    void publishConfigs(List<Long> configIds, String targetEnvironment, String publisher);

    /**
     * 配置灰度发布
     */
    void grayReleaseConfig(Long configId, String targetEnvironment, Double grayRatio, String publisher);

    /**
     * 回滚配置到指定版本
     */
    ConfigItem rollbackToVersion(Long configId, Integer version, String operator);

    /**
     * 加密敏感配置
     */
    String encryptConfigValue(String plainValue);

    /**
     * 解密配置值
     */
    String decryptConfigValue(String encryptedValue);

    /**
     * 记录配置操作日志
     */
    void logConfigOperation(String operationType, Long configId, String operator, 
                          String oldValue, String newValue, String remark);

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
     * 获取环境列表
     */
    List<String> getEnvironments();

    /**
     * 获取配置分组列表
     */
    List<String> getConfigGroups();
}