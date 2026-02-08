package com.qoobot.openadmin.config.controller;

import com.qoobot.openadmin.config.dto.ConfigDTO;
import com.qoobot.openadmin.config.service.ConfigService;
import lombok.extern.slf4j.Slf4j;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置管理控制器
 * 提供配置管理的RESTful API接口
 */
@Slf4j
@RestController
@RequestMapping("/api/config")
public class ConfigController {
    
    @Autowired
    private ConfigService configService;

    /**
     * 创建配置项
     */
    @PostMapping
    public ResponseEntity<ConfigDTO> createConfig(@Valid @RequestBody ConfigDTO configDTO) {
        try {
            ConfigDTO result = configService.createConfig(configDTO);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("创建配置失败: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("创建配置异常", e);
            throw new RuntimeException("创建配置失败");
        }
    }

    /**
     * 更新配置项
     */
    @PutMapping("/{id}")
    public ResponseEntity<ConfigDTO> updateConfig(@PathVariable Long id, 
                                                 @Valid @RequestBody ConfigDTO configDTO) {
        try {
            configDTO.setId(id);
            ConfigDTO result = configService.updateConfig(configDTO);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("更新配置失败: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("更新配置异常", e);
            throw new RuntimeException("更新配置失败");
        }
    }

    /**
     * 删除配置项
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConfig(@PathVariable Long id) {
        try {
            configService.deleteConfig(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("删除配置异常", e);
            throw new RuntimeException("删除配置失败");
        }
    }

    /**
     * 根据ID获取配置项
     */
    @GetMapping("/{id}")
    public ResponseEntity<ConfigDTO> getConfigById(@PathVariable Long id) {
        try {
            ConfigDTO result = configService.getConfigById(id);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("获取配置失败: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("获取配置异常", e);
            throw new RuntimeException("获取配置失败");
        }
    }

    /**
     * 根据配置键和环境获取配置值
     */
    @GetMapping("/value")
    public ResponseEntity<Map<String, String>> getConfigValue(
            @RequestParam String key,
            @RequestParam String environment) {
        try {
            String value = configService.getConfigValue(key, environment);
            Map<String, String> result = new HashMap<>();
            result.put("key", key);
            result.put("environment", environment);
            result.put("value", value);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("获取配置值异常", e);
            throw new RuntimeException("获取配置值失败");
        }
    }

    /**
     * 批量获取配置值
     */
    @PostMapping("/batch-values")
    public ResponseEntity<Map<String, String>> batchGetConfigValues(
            @RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<String> keys = (List<String>) request.get("keys");
            String environment = (String) request.get("environment");
            
            Map<String, String> values = configService.batchGetConfigValues(keys, environment);
            return ResponseEntity.ok(values);
        } catch (Exception e) {
            log.error("批量获取配置值异常", e);
            throw new RuntimeException("批量获取配置值失败");
        }
    }

    /**
     * 分页查询配置项
     */
    @PostMapping("/search")
    public ResponseEntity<Page<ConfigDTO>> searchConfigs(@RequestBody ConfigDTO.ConfigQuery query) {
        try {
            Page<ConfigDTO> result = configService.getConfigsByPage(query);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("查询配置异常", e);
            throw new RuntimeException("查询配置失败");
        }
    }

    /**
     * 根据分组获取配置项
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<ConfigDTO>> getConfigsByGroup(@PathVariable Long groupId) {
        try {
            List<ConfigDTO> result = configService.getConfigsByGroup(groupId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("按分组查询配置异常", e);
            throw new RuntimeException("按分组查询配置失败");
        }
    }

    /**
     * 根据环境获取配置项
     */
    @GetMapping("/environment/{environment}")
    public ResponseEntity<List<ConfigDTO>> getConfigsByEnvironment(@PathVariable String environment) {
        try {
            List<ConfigDTO> result = configService.getConfigsByEnvironment(environment);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("按环境查询配置异常", e);
            throw new RuntimeException("按环境查询配置失败");
        }
    }

    /**
     * 配置热更新
     */
    @PostMapping("/{id}/reload")
    public ResponseEntity<Void> hotReloadConfig(@PathVariable Long id) {
        try {
            configService.hotReloadConfig(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("配置热更新异常", e);
            throw new RuntimeException("配置热更新失败");
        }
    }

    /**
     * 批量更新配置状态
     */
    @PostMapping("/batch-status")
    public ResponseEntity<Void> batchUpdateStatus(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Long> ids = (List<Long>) request.get("ids");
            String status = (String) request.get("status");
            String operator = (String) request.get("operator");
            
            configService.batchUpdateStatus(ids, status, operator);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("批量更新状态异常", e);
            throw new RuntimeException("批量更新状态失败");
        }
    }

    /**
     * 配置发布
     */
    @PostMapping("/publish")
    public ResponseEntity<Void> publishConfig(@Valid @RequestBody ConfigDTO.ConfigPublishRequest request) {
        try {
            configService.publishConfig(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("配置发布异常", e);
            throw new RuntimeException("配置发布失败");
        }
    }

    /**
     * 配置灰度发布
     */
    @PostMapping("/gray-release")
    public ResponseEntity<Void> grayReleaseConfig(@Valid @RequestBody ConfigDTO.ConfigPublishRequest request) {
        try {
            configService.grayReleaseConfig(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("配置灰度发布异常", e);
            throw new RuntimeException("配置灰度发布失败");
        }
    }

    /**
     * 配置回滚
     */
    @PostMapping("/{id}/rollback")
    public ResponseEntity<ConfigDTO> rollbackConfig(
            @PathVariable Long id,
            @RequestParam Integer version,
            @RequestParam String operator) {
        try {
            ConfigDTO result = configService.rollbackConfig(id, version, operator);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("配置回滚异常", e);
            throw new RuntimeException("配置回滚失败");
        }
    }

    /**
     * 获取配置历史版本
     */
    @GetMapping("/{id}/history")
    public ResponseEntity<List<ConfigDTO.ConfigVersion>> getConfigHistory(@PathVariable Long id) {
        try {
            List<ConfigDTO.ConfigVersion> result = configService.getConfigHistory(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("获取配置历史异常", e);
            throw new RuntimeException("获取配置历史失败");
        }
    }

    /**
     * 获取配置操作日志
     */
    @GetMapping("/{id}/logs")
    public ResponseEntity<List<Object>> getConfigOperationLogs(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<Object> result = configService.getConfigOperationLogs(id, page, size);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("获取操作日志异常", e);
            throw new RuntimeException("获取操作日志失败");
        }
    }

    /**
     * 导出配置
     */
    @PostMapping("/export")
    public ResponseEntity<Map<String, String>> exportConfigs(@RequestBody ConfigDTO.ConfigQuery query) {
        try {
            String jsonData = configService.exportConfigs(query);
            Map<String, String> result = new HashMap<>();
            result.put("data", jsonData);
            result.put("timestamp", String.valueOf(System.currentTimeMillis()));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("导出配置异常", e);
            throw new RuntimeException("导出配置失败");
        }
    }

    /**
     * 导入配置
     */
    @PostMapping("/import")
    public ResponseEntity<Void> importConfigs(@RequestBody Map<String, String> request) {
        try {
            String jsonData = request.get("data");
            String operator = request.get("operator");
            configService.importConfigs(jsonData, operator);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("导入配置异常", e);
            throw new RuntimeException("导入配置失败");
        }
    }

    /**
     * 获取支持的环境列表
     */
    @GetMapping("/environments")
    public ResponseEntity<List<String>> getEnvironments() {
        try {
            List<String> result = configService.getEnvironments();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("获取环境列表异常", e);
            throw new RuntimeException("获取环境列表失败");
        }
    }

    /**
     * 获取配置分组列表
     */
    @GetMapping("/groups")
    public ResponseEntity<List<String>> getConfigGroups() {
        try {
            List<String> result = configService.getConfigGroups();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("获取分组列表异常", e);
            throw new RuntimeException("获取分组列表失败");
        }
    }

    /**
     * 获取配置统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getConfigStatistics() {
        try {
            Map<String, Object> result = configService.getConfigStatistics();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("获取统计信息异常", e);
            throw new RuntimeException("获取统计信息失败");
        }
    }

    /**
     * 验证配置格式
     */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Boolean>> validateConfigFormat(@RequestBody Map<String, String> request) {
        try {
            String value = request.get("value");
            String type = request.get("type");
            boolean valid = configService.validateConfigFormat(value, type);
            Map<String, Boolean> result = new HashMap<>();
            result.put("valid", valid);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("验证配置格式异常", e);
            throw new RuntimeException("验证配置格式失败");
        }
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("service", "config-service");
        result.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(result);
    }
}