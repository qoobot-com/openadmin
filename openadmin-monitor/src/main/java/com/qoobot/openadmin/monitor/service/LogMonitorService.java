package com.qoobot.openadmin.monitor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 日志监控服务
 * 提供结构化日志、日志分析统计、异常日志告警等功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogMonitorService {

    private final MetricService metricService;
    
    // 日志统计指标
    private final ConcurrentHashMap<String, AtomicLong> logLevelCounters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> errorTypeCounters = new ConcurrentHashMap<>();
    
    // 异常日志统计
    private AtomicLong totalErrorLogs;
    private AtomicLong totalWarnLogs;
    private AtomicLong totalInfoLogs;

    @PostConstruct
    public void initLogMetrics() {
        // 初始化日志级别计数器
        totalErrorLogs = new AtomicLong(0);
        totalWarnLogs = new AtomicLong(0);
        totalInfoLogs = new AtomicLong(0);
        
        logLevelCounters.put("ERROR", totalErrorLogs);
        logLevelCounters.put("WARN", totalWarnLogs);
        logLevelCounters.put("INFO", totalInfoLogs);
        
        log.info("Log monitor service initialized successfully");
    }

    /**
     * 记录日志事件
     */
    public void recordLogEvent(String level, String loggerName, String message, Throwable throwable) {
        // 更新日志级别统计
        AtomicLong counter = logLevelCounters.get(level.toUpperCase());
        if (counter != null) {
            counter.incrementAndGet();
        }
        
        // 如果是错误日志，记录异常类型
        if ("ERROR".equalsIgnoreCase(level) && throwable != null) {
            String exceptionType = throwable.getClass().getSimpleName();
            errorTypeCounters.computeIfAbsent(exceptionType, k -> new AtomicLong(0))
                    .incrementAndGet();
            
            // 记录到指标服务
            metricService.recordException(exceptionType);
        }
        
        // 记录结构化日志
        logStructuredEvent(level, loggerName, message, throwable);
    }

    /**
     * 记录结构化日志事件
     */
    private void logStructuredEvent(String level, String loggerName, String message, Throwable throwable) {
        StructuredLogEvent event = StructuredLogEvent.builder()
                .timestamp(LocalDateTime.now())
                .level(level)
                .logger(loggerName)
                .message(message)
                .threadName(Thread.currentThread().getName())
                .build();
        
        if (throwable != null) {
            event.setExceptionClass(throwable.getClass().getSimpleName());
            event.setExceptionMessage(throwable.getMessage());
        }
        
        // 输出结构化日志
        log.debug("Structured log event: {}", event.toJson());
    }

    /**
     * 获取日志统计信息
     */
    public LogStatistics getLogStatistics() {
        return LogStatistics.builder()
                .totalErrorLogs(totalErrorLogs.get())
                .totalWarnLogs(totalWarnLogs.get())
                .totalInfoLogs(totalInfoLogs.get())
                .errorTypes(errorTypeCounters)
                .build();
    }

    /**
     * 检查是否需要告警
     */
    public boolean shouldAlert() {
        long errorCount = totalErrorLogs.get();
        long warnCount = totalWarnLogs.get();
        
        // 简单的告警规则：错误日志超过10条或警告日志超过50条
        return errorCount > 10 || warnCount > 50;
    }

    /**
     * 重置统计计数器
     */
    public void resetCounters() {
        totalErrorLogs.set(0);
        totalWarnLogs.set(0);
        totalInfoLogs.set(0);
        errorTypeCounters.clear();
    }

    /**
     * 结构化日志事件
     */
    @lombok.Data
    @lombok.Builder
    public static class StructuredLogEvent {
        private LocalDateTime timestamp;
        private String level;
        private String logger;
        private String message;
        private String threadName;
        private String exceptionClass;
        private String exceptionMessage;
        
        public String toJson() {
            return String.format(
                "{\"timestamp\":\"%s\",\"level\":\"%s\",\"logger\":\"%s\",\"message\":\"%s\",\"thread\":\"%s\",\"exceptionClass\":\"%s\",\"exceptionMessage\":\"%s\"}",
                timestamp, level, logger, message, threadName, 
                exceptionClass != null ? exceptionClass : "",
                exceptionMessage != null ? exceptionMessage : ""
            );
        }
    }

    /**
     * 日志统计信息
     */
    @lombok.Data
    @lombok.Builder
    public static class LogStatistics {
        private long totalErrorLogs;
        private long totalWarnLogs;
        private long totalInfoLogs;
        private ConcurrentHashMap<String, AtomicLong> errorTypes;
        
        public double getErrorRate() {
            long totalLogs = totalErrorLogs + totalWarnLogs + totalInfoLogs;
            return totalLogs > 0 ? (double) totalErrorLogs / totalLogs : 0.0;
        }
    }
}