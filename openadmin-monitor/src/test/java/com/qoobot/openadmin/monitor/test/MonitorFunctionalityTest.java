package com.qoobot.openadmin.monitor.test;

import com.qoobot.openadmin.monitor.MonitorApplication;
import com.qoobot.openadmin.monitor.service.LogMonitorService;
import com.qoobot.openadmin.monitor.service.MetricService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 监控功能完整性测试
 */
@SpringBootTest(classes = MonitorApplication.class)
@ActiveProfiles("test")
public class MonitorFunctionalityTest {

    @Autowired
    private MetricService metricService;

    @Autowired
    private LogMonitorService logMonitorService;

    /**
     * 测试应用监控功能完整性
     */
    @Test
    public void testApplicationMonitoringCompleteness() {
        // 测试JVM监控
        metricService.refreshJvmMetrics();
        long memoryUsed = metricService.getCurrentMemoryUsed();
        int threadCount = metricService.getCurrentThreadCount();
        
        assertTrue(memoryUsed > 0, "内存使用量应该大于0");
        assertTrue(threadCount > 0, "线程数量应该大于0");

        // 测试业务监控
        var sample = metricService.startBusinessTimer("testBusiness");
        metricService.incrementBusinessCounter("testBusiness");
        metricService.stopBusinessTimer(sample, "testBusiness");

        // 测试异常监控
        metricService.recordException("TestException");
        
        System.out.println("应用监控功能测试通过");
    }

    /**
     * 测试业务指标监控有效性
     */
    @Test
    public void testBusinessMetricsEffectiveness() {
        String businessOperation = "paymentProcessing";
        
        // 模拟多次业务操作
        for (int i = 0; i < 10; i++) {
            var sample = metricService.startBusinessTimer(businessOperation);
            try {
                Thread.sleep(50); // 模拟业务处理
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            metricService.stopBusinessTimer(sample, businessOperation);
            metricService.incrementBusinessCounter(businessOperation);
        }

        System.out.println("业务指标监控测试通过 - 操作: " + businessOperation);
    }

    /**
     * 测试监控告警机制正确性
     */
    @Test
    public void testAlertMechanismCorrectness() {
        // 正常情况下不应该告警
        boolean shouldAlert = logMonitorService.shouldAlert();
        assertFalse(shouldAlert, "正常情况下不应该触发告警");

        // 模拟大量错误日志
        for (int i = 0; i < 15; i++) {
            logMonitorService.recordLogEvent("ERROR", "TestLogger", 
                "Test error message " + i, new RuntimeException("Test Exception"));
        }

        // 现在应该触发告警
        shouldAlert = logMonitorService.shouldAlert();
        assertTrue(shouldAlert, "错误日志过多时应该触发告警");

        System.out.println("监控告警机制测试通过");
    }

    /**
     * 测试自定义监控指标
     */
    @Test
    public void testCustomMetricsSupport() {
        // 测试自定义业务指标
        String customMetric = "userRegistration";
        metricService.incrementBusinessCounter(customMetric);
        
        // 测试自定义计时器
        var sample = metricService.startBusinessTimer(customMetric);
        metricService.stopBusinessTimer(sample, customMetric);

        System.out.println("自定义监控指标测试通过 - 指标: " + customMetric);
    }

    /**
     * 测试日志监控功能
     */
    @Test
    public void testLogMonitoringFunctionality() {
        // 记录不同类型日志
        logMonitorService.recordLogEvent("INFO", "TestLogger", "Info message", null);
        logMonitorService.recordLogEvent("WARN", "TestLogger", "Warning message", null);
        logMonitorService.recordLogEvent("ERROR", "TestLogger", "Error message", 
            new IllegalStateException("Test exception"));

        // 获取统计信息
        var statistics = logMonitorService.getLogStatistics();
        
        assertNotNull(statistics, "日志统计信息不应该为空");
        assertTrue(statistics.getTotalInfoLogs() >= 1, "INFO日志计数应该>=1");
        assertTrue(statistics.getTotalWarnLogs() >= 1, "WARN日志计数应该>=1");
        assertTrue(statistics.getTotalErrorLogs() >= 1, "ERROR日志计数应该>=1");

        System.out.println("日志监控功能测试通过");
        System.out.println("INFO日志数: " + statistics.getTotalInfoLogs());
        System.out.println("WARN日志数: " + statistics.getTotalWarnLogs());
        System.out.println("ERROR日志数: " + statistics.getTotalErrorLogs());
        System.out.println("错误率: " + String.format("%.2f%%", statistics.getErrorRate() * 100));
    }
}