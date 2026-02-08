package com.qoobot.openadmin.config.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qoobot.openadmin.config.dto.ConfigDTO;
import com.qoobot.openadmin.config.entity.ConfigItem;
import com.qoobot.openadmin.config.repository.ConfigRepository;
import com.qoobot.openadmin.config.service.ConfigService;
import lombok.extern.slf4j.Slf4j;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 配置服务实现类
 * 实现配置管理的核心业务逻辑
 */
@Slf4j
@Service
public class ConfigServiceImpl implements ConfigService {
    
    @Autowired
    private ConfigRepository configRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // 配置变更监听器注册表
    private final Map<String, List<ConfigChangeListener>> listeners = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public ConfigDTO createConfig(ConfigDTO configDTO) {
        // 参数校验
        validateConfigDTO(configDTO);
        
        // 检查配置键是否已存在
        if (configRepository.existsByConfigKeyAndEnvironment(
                configDTO.getConfigKey(), configDTO.getEnvironment())) {
            throw new IllegalArgumentException("配置键已存在: " + configDTO.getConfigKey());
        }

        // 敏感配置自动加密
        if (shouldEncrypt(configDTO)) {
            configDTO.setConfigValue(configRepository.encryptConfigValue(configDTO.getConfigValue()));
            configDTO.setEncrypted(true);
        }

        // 转换为实体对象
        ConfigItem configItem = convertToEntity(configDTO);
        configItem.setCreatedAt(LocalDateTime.now());
        configItem.setUpdatedAt(LocalDateTime.now());

        // 保存配置
        ConfigItem savedItem = configRepository.save(configItem);

        // 转换为DTO返回
        ConfigDTO result = convertToDTO(savedItem);

        log.info("创建配置项成功: {} - {}", configDTO.getConfigKey(), configDTO.getEnvironment());
        return result;
    }

    @Override
    @Transactional
    public ConfigDTO updateConfig(ConfigDTO configDTO) {
        if (configDTO.getId() == null) {
            throw new IllegalArgumentException("配置ID不能为空");
        }

        // 获取原有配置
        ConfigItem existingItem = configRepository.findById(configDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("配置项不存在"));

        String oldValue = existingItem.getConfigValue();

        // 敏感配置处理
        if (shouldEncrypt(configDTO) && !Boolean.TRUE.equals(existingItem.getEncrypted())) {
            configDTO.setConfigValue(configRepository.encryptConfigValue(configDTO.getConfigValue()));
            configDTO.setEncrypted(true);
        }

        // 更新配置
        ConfigItem configItem = convertToEntity(configDTO);
        configItem.setCreatedAt(existingItem.getCreatedAt());
        configItem.setUpdatedAt(LocalDateTime.now());

        ConfigItem updatedItem = configRepository.save(configItem);

        // 触发配置变更监听
        notifyConfigChange(configDTO.getConfigKey(), configDTO.getEnvironment(), oldValue, configDTO.getConfigValue());

        // 转换为DTO返回
        ConfigDTO result = convertToDTO(updatedItem);

        log.info("更新配置项成功: {} - {}", configDTO.getConfigKey(), configDTO.getEnvironment());
        return result;
    }

    @Override
    @Transactional
    public void deleteConfig(Long id) {
        configRepository.deleteById(id);
        log.info("删除配置项成功: id={}", id);
    }

    @Override
    public ConfigDTO getConfigById(Long id) {
        ConfigItem configItem = configRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("配置项不存在"));
        return convertToDTO(configItem);
    }

    @Override
    public String getConfigValue(String configKey, String environment) {
        return configRepository.findByConfigKeyAndEnvironment(configKey, environment)
                .map(this::decryptIfEncrypted)
                .orElse(null);
    }

    @Override
    public Map<String, String> batchGetConfigValues(List<String> configKeys, String environment) {
        Map<String, String> result = new HashMap<>();
        for (String configKey : configKeys) {
            String value = getConfigValue(configKey, environment);
            if (value != null) {
                result.put(configKey, value);
            }
        }
        return result;
    }

    @Override
    public Page<ConfigDTO> getConfigsByPage(ConfigDTO.ConfigQuery query) {
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize());
        
        List<ConfigItem> items = configRepository.findByCondition(
                query.getConfigKey(),
                query.getGroupName(),
                query.getEnvironment(),
                query.getStatus(),
                query.getConfigType(),
                query.getEncrypted(),
                query.getKeyword()
        );

        List<ConfigDTO> dtos = items.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // 简化分页实现
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), dtos.size());
        List<ConfigDTO> pageContent = dtos.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, dtos.size());
    }

    @Override
    public List<ConfigDTO> getConfigsByGroup(Long groupId) {
        List<ConfigItem> items = configRepository.findByGroupId(groupId);
        return items.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConfigDTO> getConfigsByEnvironment(String environment) {
        List<ConfigItem> items = configRepository.findByEnvironment(environment);
        return items.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void hotReloadConfig(Long configId) {
        ConfigItem configItem = configRepository.findById(configId)
                .orElseThrow(() -> new IllegalArgumentException("配置项不存在"));
        
        configRepository.notifyConfigChanged(configItem.getConfigKey(), configItem.getEnvironment());
        log.info("配置热更新: {} - {}", configItem.getConfigKey(), configItem.getEnvironment());
    }

    @Override
    @Transactional
    public void batchUpdateStatus(List<Long> ids, String status, String operator) {
        configRepository.updateStatus(ids, status, operator);
        log.info("批量更新配置状态: {} 条记录更新为 {}", ids.size(), status);
    }

    @Override
    @Transactional
    public void publishConfig(ConfigDTO.ConfigPublishRequest request) {
        configRepository.publishConfigs(
                Arrays.asList(request.getConfigId()),
                request.getTargetEnvironment(),
                request.getPublisher()
        );
        log.info("配置发布成功: configId={}, targetEnv={}", 
                request.getConfigId(), request.getTargetEnvironment());
    }

    @Override
    @Transactional
    public void grayReleaseConfig(ConfigDTO.ConfigPublishRequest request) {
        configRepository.grayReleaseConfig(
                request.getConfigId(),
                request.getTargetEnvironment(),
                request.getGrayRatio(),
                request.getPublisher()
        );
        log.info("配置灰度发布: configId={}, ratio={}", 
                request.getConfigId(), request.getGrayRatio());
    }

    @Override
    @Transactional
    public ConfigDTO rollbackConfig(Long configId, Integer version, String operator) {
        ConfigItem rolledBackItem = configRepository.rollbackToVersion(configId, version, operator);
        if (rolledBackItem != null) {
            log.info("配置回滚成功: configId={}, version={}", configId, version);
            return convertToDTO(rolledBackItem);
        }
        return null;
    }

    @Override
    public List<ConfigDTO.ConfigVersion> getConfigHistory(Long configId) {
        List<ConfigItem> historyItems = configRepository.findHistoryVersions(configId);
        return historyItems.stream()
                .map(item -> {
                    ConfigDTO.ConfigVersion version = new ConfigDTO.ConfigVersion();
                    version.setConfigId(item.getId());
                    version.setVersion(item.getVersion());
                    version.setConfigValue(decryptIfEncrypted(item));
                    version.setDescription(item.getDescription());
                    version.setUpdatedBy(item.getUpdatedBy());
                    version.setUpdatedAt(item.getUpdatedAt());
                    return version;
                })
                .collect(Collectors.toList());
    }

    @Override
    public String encryptConfigValue(String plainValue) {
        return configRepository.encryptConfigValue(plainValue);
    }

    @Override
    public String decryptConfigValue(String encryptedValue) {
        return configRepository.decryptConfigValue(encryptedValue);
    }

    @Override
    public List<Object> getConfigOperationLogs(Long configId, int page, int size) {
        return configRepository.getConfigOperationLogs(configId, page, size);
    }

    @Override
    public boolean hasConfigPermission(String username, String configKey, String operation) {
        return configRepository.hasConfigPermission(username, configKey, operation);
    }

    @Override
    public void syncToNacos(Long configId) {
        configRepository.syncToNacos(configId);
    }

    @Override
    public void syncFromNacos(String dataId, String group, String namespace) {
        configRepository.syncFromNacos(dataId, group, namespace);
    }

    @Override
    public List<String> getEnvironments() {
        return configRepository.getEnvironments();
    }

    @Override
    public List<String> getConfigGroups() {
        return configRepository.getConfigGroups();
    }

    @Override
    public String exportConfigs(ConfigDTO.ConfigQuery query) {
        try {
            List<ConfigItem> items = configRepository.findByCondition(
                    query.getConfigKey(),
                    query.getGroupName(),
                    query.getEnvironment(),
                    query.getStatus(),
                    query.getConfigType(),
                    query.getEncrypted(),
                    query.getKeyword()
            );
            
            List<ConfigDTO> dtos = items.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            return objectMapper.writeValueAsString(dtos);
        } catch (Exception e) {
            log.error("导出配置失败", e);
            throw new RuntimeException("导出配置失败", e);
        }
    }

    @Override
    @Transactional
    public void importConfigs(String jsonData, String operator) {
        try {
            List<ConfigDTO> configs = Arrays.asList(objectMapper.readValue(jsonData, ConfigDTO[].class));
            for (ConfigDTO configDTO : configs) {
                configDTO.setCreatedBy(operator);
                configDTO.setUpdatedBy(operator);
                createConfig(configDTO);
            }
            log.info("导入配置成功: {} 条记录", configs.size());
        } catch (Exception e) {
            log.error("导入配置失败", e);
            throw new RuntimeException("导入配置失败", e);
        }
    }

    @Override
    public void backupConfigs(String backupName, String operator) {
        // 配置备份实现
        log.info("配置备份: name={}, operator={}", backupName, operator);
    }

    @Override
    public void restoreConfigs(String backupName, String operator) {
        // 配置恢复实现
        log.info("配置恢复: name={}, operator={}", backupName, operator);
    }

    @Override
    public Map<String, Object> getConfigStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", configRepository.count());
        stats.put("environments", getEnvironments());
        stats.put("groups", getConfigGroups());
        stats.put("encrypted", configRepository.countByCondition(null, null, null, null, null, true, null));
        return stats;
    }

    @Override
    public boolean validateConfigFormat(String configValue, String configType) {
        if (!StringUtils.hasText(configValue)) {
            return true;
        }

        try {
            switch (configType.toUpperCase()) {
                case "NUMBER":
                    Double.parseDouble(configValue);
                    break;
                case "BOOLEAN":
                    if (!"true".equalsIgnoreCase(configValue) && !"false".equalsIgnoreCase(configValue)) {
                        return false;
                    }
                    break;
                case "JSON":
                    objectMapper.readTree(configValue);
                    break;
                default:
                    // STRING类型无需特殊验证
                    break;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void monitorConfigChanges(String configKey, String environment, ConfigChangeListener listener) {
        String key = configKey + ":" + environment;
        listeners.computeIfAbsent(key, k -> new ArrayList<>()).add(listener);
    }

    // 私有辅助方法
    private void validateConfigDTO(ConfigDTO configDTO) {
        if (!StringUtils.hasText(configDTO.getConfigKey())) {
            throw new IllegalArgumentException("配置键不能为空");
        }
        if (!StringUtils.hasText(configDTO.getEnvironment())) {
            throw new IllegalArgumentException("环境不能为空");
        }
        if (!validateConfigFormat(configDTO.getConfigValue(), configDTO.getConfigType())) {
            throw new IllegalArgumentException("配置值格式不正确");
        }
    }

    private boolean shouldEncrypt(ConfigDTO configDTO) {
        // 根据配置键判断是否需要加密
        String key = configDTO.getConfigKey().toLowerCase();
        return key.contains("password") || key.contains("secret") || key.contains("token") || 
               key.contains("key") || key.contains("credential");
    }

    private String decryptIfEncrypted(ConfigItem item) {
        if (Boolean.TRUE.equals(item.getEncrypted())) {
            return configRepository.decryptConfigValue(item.getConfigValue());
        }
        return item.getConfigValue();
    }

    private String decryptIfEncrypted(ConfigDTO dto) {
        if (Boolean.TRUE.equals(dto.getEncrypted())) {
            return configRepository.decryptConfigValue(dto.getConfigValue());
        }
        return dto.getConfigValue();
    }

    private ConfigItem convertToEntity(ConfigDTO dto) {
        ConfigItem item = new ConfigItem();
        BeanUtils.copyProperties(dto, item);
        return item;
    }

    private ConfigDTO convertToDTO(ConfigItem item) {
        ConfigDTO dto = new ConfigDTO();
        BeanUtils.copyProperties(item, dto);
        return dto;
    }

    private void notifyConfigChange(String configKey, String environment, String oldValue, String newValue) {
        String key = configKey + ":" + environment;
        List<ConfigChangeListener> configListeners = listeners.get(key);
        if (configListeners != null) {
            for (ConfigChangeListener listener : configListeners) {
                try {
                    listener.onConfigChanged(configKey, environment, oldValue, newValue);
                } catch (Exception e) {
                    log.error("配置变更监听器执行失败", e);
                }
            }
        }
    }
}