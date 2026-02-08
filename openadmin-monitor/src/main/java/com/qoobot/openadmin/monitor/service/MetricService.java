package com.qoobot.openadmin.monitor.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 监控指标服务
 * 提供应用性能监控、业务指标监控等功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MetricService {

    private final MeterRegistry meterRegistry;
    
    // JVM监控指标
    private AtomicLong jvmMemoryUsed;
    private AtomicLong jvmThreadCount;
    private Counter errorCounter;
    
    // 业务监控指标
    private final ConcurrentHashMap<String, Timer> businessTimers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Counter> businessCounters = new ConcurrentHashMap<>();
    
    // 应用性能指标
    private Timer responseTimeTimer;
    private Counter requestCounter;
    private Counter exceptionCounter;

    @PostConstruct
    public void initMetrics() {
        // 初始化JVM监控指标
        jvmMemoryUsed = meterRegistry.gauge("jvm.memory.used", new AtomicLong(0));
        jvmThreadCount = meterRegistry.gauge("jvm.thread.count", new AtomicLong(0));
        errorCounter = Counter.builder("application.errors.total")
                .description("Total application errors")
                .register(meterRegistry);
        
        // 初始化应用性能指标
        responseTimeTimer = Timer.builder("http.server.requests")
                .description("HTTP server request latency")
                .register(meterRegistry);
        
        requestCounter = Counter.builder("http.server.requests.total")
                .description("Total HTTP requests")
                .register(meterRegistry);
        
        exceptionCounter = Counter.builder("application.exceptions.total")
                .description("Total application exceptions")
                .register(meterRegistry);
        
        log.info("Metric service initialized successfully");
    }

    /**
     * 记录HTTP请求响应时间
     */
    public void recordResponseTime(long durationMillis) {
        responseTimeTimer.record(durationMillis, TimeUnit.MILLISECONDS);
        requestCounter.increment();
    }

    /**
     * 记录业务方法执行时间
     */
    public Timer.Sample startBusinessTimer(String businessName) {
        Timer timer = businessTimers.computeIfAbsent(businessName, 
            name -> Timer.builder("business.operation.duration")
                    .description("Business operation duration")
                    .tag("operation", name)
                    .register(meterRegistry));
        return Timer.start(meterRegistry);
    }

    /**
     * 停止并记录业务计时器
     */
    public void stopBusinessTimer(Timer.Sample sample, String businessName) {
        Timer timer = businessTimers.get(businessName);
        if (timer != null) {
            sample.stop(timer);
        }
    }

    /**
     * 记录业务操作次数
     */
    public void incrementBusinessCounter(String businessName) {
        Counter counter = businessCounters.computeIfAbsent(businessName,
            name -> Counter.builder("business.operation.count")
                    .description("Business operation count")
                    .tag("operation", name)
                    .register(meterRegistry));
        counter.increment();
    }

    /**
     * 记录异常
     */
    public void recordException(String exceptionType) {
        exceptionCounter.increment();
        errorCounter.increment();
        
        Counter.builder("application.exceptions.by.type")
                .description("Application exceptions by type")
                .tag("exception", exceptionType)
                .register(meterRegistry)
                .increment();
    }

    /**
     * 更新JVM内存使用量
     */
    public void updateJvmMemoryUsed(long bytes) {
        jvmMemoryUsed.set(bytes);
    }

    /**
     * 更新JVM线程数量
     */
    public void updateJvmThreadCount(int count) {
        jvmThreadCount.set(count);
    }

    /**
     * 获取当前JVM内存使用情况
     */
    public long getCurrentMemoryUsed() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    /**
     * 获取当前线程数量
     */
    public int getCurrentThreadCount() {
        return Thread.activeCount();
    }

    /**
     * 刷新JVM指标
     */
    public void refreshJvmMetrics() {
        updateJvmMemoryUsed(getCurrentMemoryUsed());
        updateJvmThreadCount(getCurrentThreadCount());
    }
}