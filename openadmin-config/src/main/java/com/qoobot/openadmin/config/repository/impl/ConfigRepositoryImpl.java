package com.qoobot.openadmin.config.repository.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.qoobot.openadmin.config.entity.ConfigItem;
import com.qoobot.openadmin.config.mapper.ConfigMapper;
import com.qoobot.openadmin.config.repository.ConfigRepository;
import lombok.extern.slf4j.Slf4j;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.*;

/**
 * 配置仓储实现类
 * 实现配置管理的核心数据访问逻辑
 */
@Slf4j
@Repository
public class ConfigRepositoryImpl implements ConfigRepository {
    
    @Autowired
    private ConfigMapper configMapper;

    // 配置缓存（10分钟过期）
    private final Cache<String, ConfigItem> configCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(10))
            .build();

    // AES-256-GCM加密相关
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    private static final String SECRET_KEY = "your-secret-key-here-32-bytes-long!!"; // 实际应用中应该从配置中读取

    @Override
    @Transactional
    public ConfigItem save(ConfigItem configItem) {
        if (configItem.getId() == null) {
            configMapper.insert(configItem);
            log.info("新增配置项: {} - {}", configItem.getConfigKey(), configItem.getEnvironment());
        } else {
            configMapper.updateById(configItem);
            // 清除缓存
            String cacheKey = buildCacheKey(configItem.getConfigKey(), configItem.getEnvironment());
            configCache.invalidate(cacheKey);
            log.info("更新配置项: {} - {}", configItem.getConfigKey(), configItem.getEnvironment());
        }
        
        // 记录操作日志
        logConfigOperation("SAVE", configItem.getId(), configItem.getUpdatedBy(), 
                          null, configItem.getConfigValue(), "保存配置项");
        
        return configItem;
    }

    @Override
    public Optional<ConfigItem> findById(Long id) {
        ConfigItem configItem = configMapper.selectById(id);
        return Optional.ofNullable(configItem);
    }

    @Override
    public Optional<ConfigItem> findByConfigKeyAndEnvironment(String configKey, String environment) {
        String cacheKey = buildCacheKey(configKey, environment);
        
        // 先从缓存获取
        ConfigItem cachedConfig = configCache.getIfPresent(cacheKey);
        if (cachedConfig != null) {
            return Optional.of(cachedConfig);
        }
        
        // 缓存未命中，从数据库查询
        ConfigItem configItem = configMapper.selectByKeyAndEnvironment(configKey, environment);
        if (configItem != null) {
            configCache.put(cacheKey, configItem);
        }
        
        return Optional.ofNullable(configItem);
    }

    @Override
    public List<ConfigItem> findAll() {
        return configMapper.selectAll();
    }

    @Override
    public Page<ConfigItem> findAll(Pageable pageable) {
        // 这里简化实现，实际应该根据Pageable参数进行分页查询
        List<ConfigItem> allConfigs = configMapper.selectAll();
        return new PageImpl<>(allConfigs, pageable, allConfigs.size());
    }

    @Override
    public List<ConfigItem> findByCondition(String configKey, String groupName, String environment,
                                          String status, String configType, Boolean encrypted, String keyword) {
        int offset = 0;
        int limit = 1000; // 默认最大返回1000条
        
        return configMapper.selectByPage(configKey, groupName, environment, status, 
                                       configType, encrypted, keyword, offset, limit);
    }

    @Override
    public List<ConfigItem> findByGroupId(Long groupId) {
        return configMapper.selectByGroupId(groupId);
    }

    @Override
    public List<ConfigItem> findByEnvironment(String environment) {
        return configMapper.selectByEnvironment(environment);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        ConfigItem configItem = configMapper.selectById(id);
        if (configItem != null) {
            configMapper.deleteById(id);
            // 清除缓存
            String cacheKey = buildCacheKey(configItem.getConfigKey(), configItem.getEnvironment());
            configCache.invalidate(cacheKey);
            log.info("删除配置项: {} - {}", configItem.getConfigKey(), configItem.getEnvironment());
            
            // 记录操作日志
            logConfigOperation("DELETE", id, "system", configItem.getConfigValue(), null, "删除配置项");
        }
    }

    @Override
    @Transactional
    public List<ConfigItem> saveAll(List<ConfigItem> configItems) {
        List<ConfigItem> savedItems = new ArrayList<>();
        for (ConfigItem item : configItems) {
            ConfigItem saved = save(item);
            savedItems.add(saved);
        }
        return savedItems;
    }

    @Override
    @Transactional
    public void deleteAllById(List<Long> ids) {
        for (Long id : ids) {
            deleteById(id);
        }
    }

    @Override
    public boolean existsByConfigKeyAndEnvironment(String configKey, String environment) {
        return findByConfigKeyAndEnvironment(configKey, environment).isPresent();
    }

    @Override
    public long count() {
        return configMapper.countByCondition(null, null, null, null, null, null, null);
    }

    @Override
    public long countByCondition(String configKey, String groupName, String environment,
                               String status, String configType, Boolean encrypted, String keyword) {
        return configMapper.countByCondition(configKey, groupName, environment, status, 
                                           configType, encrypted, keyword);
    }

    @Override
    @Transactional
    public int updateStatus(List<Long> ids, String status, String updatedBy) {
        int result = configMapper.batchUpdateStatus(ids, status, updatedBy);
        if (result > 0) {
            // 清除相关缓存
            for (Long id : ids) {
                Optional<ConfigItem> configOpt = findById(id);
                if (configOpt.isPresent()) {
                    ConfigItem config = configOpt.get();
                    String cacheKey = buildCacheKey(config.getConfigKey(), config.getEnvironment());
                    configCache.invalidate(cacheKey);
                }
            }
            log.info("批量更新配置状态: {} 条记录更新为 {}", ids.size(), status);
        }
        return result;
    }

    @Override
    public List<ConfigItem> findHistoryVersions(Long configId) {
        return configMapper.selectHistoryVersions(configId);
    }

    @Override
    public void notifyConfigChanged(String configKey, String environment) {
        String cacheKey = buildCacheKey(configKey, environment);
        configCache.invalidate(cacheKey);
        log.info("配置变更通知: {} - {}", configKey, environment);
    }

    @Override
    @Transactional
    public void publishConfigs(List<Long> configIds, String targetEnvironment, String publisher) {
        for (Long configId : configIds) {
            Optional<ConfigItem> configOpt = findById(configId);
            if (configOpt.isPresent()) {
                ConfigItem originalConfig = configOpt.get();
                ConfigItem newConfig = new ConfigItem();
                newConfig.setConfigKey(originalConfig.getConfigKey());
                newConfig.setConfigValue(originalConfig.getConfigValue());
                newConfig.setDescription(originalConfig.getDescription());
                newConfig.setGroupId(originalConfig.getGroupId());
                newConfig.setGroupName(originalConfig.getGroupName());
                newConfig.setEnvironment(targetEnvironment);
                newConfig.setConfigType(originalConfig.getConfigType());
                newConfig.setEncrypted(originalConfig.getEncrypted());
                newConfig.setStatus("ACTIVE");
                newConfig.setVersion(originalConfig.getVersion() + 1);
                newConfig.setTags(originalConfig.getTags());
                newConfig.setCreatedBy(publisher);
                newConfig.setUpdatedBy(publisher);
                newConfig.setSource("DATABASE");
                
                save(newConfig);
                log.info("配置发布: {} 从 {} 发布到 {}", 
                        originalConfig.getConfigKey(), originalConfig.getEnvironment(), targetEnvironment);
            }
        }
    }

    @Override
    @Transactional
    public void grayReleaseConfig(Long configId, String targetEnvironment, Double grayRatio, String publisher) {
        // 灰度发布逻辑实现
        log.info("配置灰度发布: configId={}, environment={}, ratio={}", 
                configId, targetEnvironment, grayRatio);
        // 实际实现需要更复杂的灰度策略
    }

    @Override
    @Transactional
    public ConfigItem rollbackToVersion(Long configId, Integer version, String operator) {
        // 版本回滚逻辑实现
        log.info("配置版本回滚: configId={}, version={}, operator={}", 
                configId, version, operator);
        return null; // 实际实现需要查询历史版本数据
    }

    @Override
    public String encryptConfigValue(String plainValue) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKey secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
            
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);
            
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);
            
            byte[] encrypted = cipher.doFinal(plainValue.getBytes(StandardCharsets.UTF_8));
            
            // 将IV和加密数据组合
            byte[] result = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, result, 0, iv.length);
            System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);
            
            return Base64.getEncoder().encodeToString(result);
        } catch (Exception e) {
            log.error("配置加密失败", e);
            throw new RuntimeException("配置加密失败", e);
        }
    }

    @Override
    public String decryptConfigValue(String encryptedValue) {
        try {
            byte[] decoded = Base64.getDecoder().decode(encryptedValue);
            
            byte[] iv = Arrays.copyOfRange(decoded, 0, GCM_IV_LENGTH);
            byte[] encrypted = Arrays.copyOfRange(decoded, GCM_IV_LENGTH, decoded.length);
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKey secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
            byte[] decrypted = cipher.doFinal(encrypted);
            
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("配置解密失败", e);
            throw new RuntimeException("配置解密失败", e);
        }
    }

    @Override
    public void logConfigOperation(String operationType, Long configId, String operator,
                                 String oldValue, String newValue, String remark) {
        // 配置操作日志记录实现
        log.info("配置操作日志: type={}, configId={}, operator={}, remark={}", 
                operationType, configId, operator, remark);
        // 实际应该保存到操作日志表中
    }

    @Override
    public List<Object> getConfigOperationLogs(Long configId, int page, int size) {
        // 返回配置操作日志
        return new ArrayList<>(); // 实际实现需要查询日志表
    }

    @Override
    public boolean hasConfigPermission(String username, String configKey, String operation) {
        // 权限校验逻辑
        return true; // 简化实现，实际需要查询用户权限
    }

    @Override
    public void syncToNacos(Long configId) {
        // 同步到Nacos的实现
        log.info("同步配置到Nacos: configId={}", configId);
    }

    @Override
    public void syncFromNacos(String dataId, String group, String namespace) {
        // 从Nacos同步配置的实现
        log.info("从Nacos同步配置: dataId={}, group={}, namespace={}", dataId, group, namespace);
    }

    @Override
    public List<String> getEnvironments() {
        // 返回支持的环境列表
        return Arrays.asList("dev", "test", "prod");
    }

    @Override
    public List<String> getConfigGroups() {
        // 返回配置分组列表
        Set<String> groups = new HashSet<>();
        List<ConfigItem> allConfigs = findAll();
        for (ConfigItem config : allConfigs) {
            if (StringUtils.hasText(config.getGroupName())) {
                groups.add(config.getGroupName());
            }
        }
        return new ArrayList<>(groups);
    }

    private String buildCacheKey(String configKey, String environment) {
        return configKey + ":" + environment;
    }
}