package com.qoobot.openadmin.config.simple;

import lombok.extern.slf4j.Slf4j;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 简化版配置控制器
 * 用于演示配置管理核心功能
 */
@Slf4j
@RestController
@RequestMapping("/api/config/simple")
public class SimpleConfigController {

    // 模拟配置存储
    private final Map<String, String> configStore = new HashMap<>();
    
    public SimpleConfigController() {
        // 初始化一些示例配置
        configStore.put("app.name", "OpenAdmin");
        configStore.put("app.version", "10.3.0");
        configStore.put("server.port", "8080");
        configStore.put("database.url", "jdbc:mysql://localhost:3306/openadmin");
    }

    /**
     * 获取所有配置
     */
    @GetMapping
    public ResponseEntity<Map<String, String>> getAllConfigs() {
        log.info("获取所有配置");
        return ResponseEntity.ok(configStore);
    }

    /**
     * 根据键获取配置值
     */
    @GetMapping("/{key}")
    public ResponseEntity<Map<String, String>> getConfigByKey(@PathVariable String key) {
        log.info("获取配置: {}", key);
        Map<String, String> result = new HashMap<>();
        result.put("key", key);
        result.put("value", configStore.getOrDefault(key, "未找到配置"));
        return ResponseEntity.ok(result);
    }

    /**
     * 创建或更新配置
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> createConfig(@RequestBody Map<String, String> request) {
        String key = request.get("key");
        String value = request.get("value");
        
        if (key == null || value == null) {
            return ResponseEntity.badRequest().build();
        }
        
        configStore.put(key, value);
        log.info("创建/更新配置: {} = {}", key, value);
        
        Map<String, String> result = new HashMap<>();
        result.put("message", "配置保存成功");
        result.put("key", key);
        result.put("value", value);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除配置
     */
    @DeleteMapping("/{key}")
    public ResponseEntity<Map<String, String>> deleteConfig(@PathVariable String key) {
        String removedValue = configStore.remove(key);
        if (removedValue != null) {
            log.info("删除配置: {}", key);
            Map<String, String> result = new HashMap<>();
            result.put("message", "配置删除成功");
            result.put("key", key);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 批量获取配置
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, String>> batchGetConfigs(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        java.util.List<String> keys = (java.util.List<String>) request.get("keys");
        
        Map<String, String> result = new HashMap<>();
        for (String key : keys) {
            result.put(key, configStore.getOrDefault(key, "未找到"));
        }
        
        log.info("批量获取配置: {}", keys);
        return ResponseEntity.ok(result);
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("service", "simple-config-service");
        result.put("configCount", configStore.size());
        result.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(result);
    }
}