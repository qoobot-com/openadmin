package com.qoobot.openadmin.config.simple;

import lombok.extern.slf4j.Slf4j;


import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 简化版配置服务
 * 实现基本的配置管理功能
 */
@Slf4j
@Service
public class SimpleConfigService {


    // 模拟配置存储
    private final Map<String, ConfigEntry> configStore = new HashMap<>();

    public SimpleConfigService() {
        // 初始化示例配置
        addConfig("app.name", "OpenAdmin", "应用名称", "dev");
        addConfig("app.version", "10.3.0", "应用版本", "dev");
        addConfig("server.port", "8080", "服务端口", "dev");
        addConfig("database.url", "jdbc:mysql://localhost:3306/openadmin", "数据库连接URL", "dev");
        addConfig("cache.enabled", "true", "缓存开关", "dev");
    }

    private void addConfig(String key, String value, String description, String environment) {
        ConfigEntry entry = new ConfigEntry();
        entry.setKey(key);
        entry.setValue(value);
        entry.setDescription(description);
        entry.setEnvironment(environment);
        entry.setEncrypted(false);
        entry.setStatus("ACTIVE");
        configStore.put(key + ":" + environment, entry);
    }

    /**
     * 获取配置值
     */
    public String getConfigValue(String key, String environment) {
        ConfigEntry entry = configStore.get(key + ":" + environment);
        if (entry != null) {
            log.debug("获取配置: {}:{} = {}", key, environment, entry.getValue());
            return entry.getValue();
        }
        return null;
    }

    /**
     * 设置配置值
     */
    public void setConfigValue(String key, String value, String environment, String description) {
        ConfigEntry entry = configStore.get(key + ":" + environment);
        if (entry == null) {
            entry = new ConfigEntry();
            entry.setKey(key);
            entry.setEnvironment(environment);
        }
        entry.setValue(value);
        entry.setDescription(description);
        entry.setStatus("ACTIVE");
        configStore.put(key + ":" + environment, entry);
        log.info("设置配置: {}:{} = {}", key, environment, value);
    }

    /**
     * 删除配置
     */
    public boolean deleteConfig(String key, String environment) {
        ConfigEntry removed = configStore.remove(key + ":" + environment);
        if (removed != null) {
            log.info("删除配置: {}:{}", key, environment);
            return true;
        }
        return false;
    }

    /**
     * 获取所有配置
     */
    public Map<String, ConfigEntry> getAllConfigs() {
        return new HashMap<>(configStore);
    }

    /**
     * 检查配置是否存在
     */
    public boolean exists(String key, String environment) {
        return configStore.containsKey(key + ":" + environment);
    }

    /**
     * 配置条目类
     */
    public static class ConfigEntry {
        private String key;
        private String value;
        private String description;
        private String environment;
        private boolean encrypted;
        private String status;

        // Getters and Setters
        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getEnvironment() {
            return environment;
        }

        public void setEnvironment(String environment) {
            this.environment = environment;
        }

        public boolean isEncrypted() {
            return encrypted;
        }

        public void setEncrypted(boolean encrypted) {
            this.encrypted = encrypted;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}