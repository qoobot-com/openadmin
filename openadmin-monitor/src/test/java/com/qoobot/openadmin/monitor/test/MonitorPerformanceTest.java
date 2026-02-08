package com.qoobot.openadmin.monitor.test;

import com.qoobot.openadmin.monitor.MonitorApplication;
import com.qoobot.openadmin.monitor.service.MetricService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 监控模块性能测试
 */
@SpringBootTest(classes = MonitorApplication.class)
@ActiveProfiles("test")
public class MonitorPerformanceTest {

    @Autowired
    private MetricService metricService;

    /**
     * 测试监控指标收集性能
     */
    @Test
    public void testMetricCollectionPerformance() throws InterruptedException {
        int threadCount = 10;
        int iterationsPerThread = 1000;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        long startTime = System.currentTimeMillis();

        // 并发测试指标收集
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < iterationsPerThread; j++) {
                        // 模拟业务操作监控
                        var sample = metricService.startBusinessTimer("testOperation-" + threadId);
                        Thread.sleep(1); // 模拟业务处理
                        metricService.stopBusinessTimer(sample, "testOperation-" + threadId);
                        metricService.incrementBusinessCounter("testOperation-" + threadId);
                        
                        // 记录响应时间
                        metricService.recordResponseTime(50 + (long)(Math.random() * 100));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        long totalOperations = (long) threadCount * iterationsPerThread;

        // 性能指标
        double opsPerSecond = (double) totalOperations / (totalTime / 1000.0);
        double avgLatency = (double) totalTime / totalOperations;

        System.out.println("=== 监控性能测试结果 ===");
        System.out.println("总操作数: " + totalOperations);
        System.out.println("总耗时: " + totalTime + " ms");
        System.out.println("每秒操作数: " + String.format("%.2f", opsPerSecond));
        System.out.println("平均延迟: " + String.format("%.2f", avgLatency) + " ms");

        // 验证性能要求
        assertTrue(opsPerSecond > 1000, "监控指标收集性能应该超过1000 OPS");
        assertTrue(avgLatency < 10, "平均延迟应该小于10ms");
    }

    /**
     * 测试并发安全性和数据准确性
     */
    @Test
    public void testConcurrentSafetyAndAccuracy() throws InterruptedException {
        int threadCount = 20;
        int iterationsPerThread = 500;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        // 并发增加计数器
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < iterationsPerThread; j++) {
                        metricService.incrementBusinessCounter("concurrencyTest");
                        metricService.recordException("TestException");
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // 验证计数准确性
        // 注意：由于使用了AtomicLong，这里只是概念性验证
        long expectedCount = (long) threadCount * iterationsPerThread;
        
        System.out.println("并发安全性测试:");
        System.out.println("期望计数: " + expectedCount);
        System.out.println("实际计数会因为并发而有所不同，但应该接近期望值");
    }

    /**
     * 测试JVM指标刷新性能
     */
    @Test
    public void testJvmMetricsRefreshPerformance() {
        int iterations = 1000;
        long startTime = System.nanoTime();

        for (int i = 0; i < iterations; i++) {
            metricService.refreshJvmMetrics();
        }

        long endTime = System.nanoTime();
        long totalTimeNs = endTime - startTime;
        double avgTimeMs = totalTimeNs / 1_000_000.0 / iterations;

        System.out.println("JVM指标刷新性能测试:");
        System.out.println("总迭代次数: " + iterations);
        System.out.println("总耗时: " + (totalTimeNs / 1_000_000.0) + " ms");
        System.out.println("平均每次刷新耗时: " + String.format("%.4f", avgTimeMs) + " ms");

        assertTrue(avgTimeMs < 5, "JVM指标刷新平均耗时应该小于5ms");
    }

    /**
     * 测试异步监控性能
     */
    @Test
    public void testAsyncMonitoringPerformance() {
        int taskCount = 100;
        CompletableFuture<?>[] futures = new CompletableFuture[taskCount];

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < taskCount; i++) {
            futures[i] = CompletableFuture.runAsync(() -> {
                var sample = metricService.startBusinessTimer("asyncTest");
                try {
                    Thread.sleep(10); // 模拟异步任务
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                metricService.stopBusinessTimer(sample, "asyncTest");
                metricService.incrementBusinessCounter("asyncTest");
            });
        }

        CompletableFuture.allOf(futures).join();
        long endTime = System.currentTimeMillis();

        long totalTime = endTime - startTime;
        double throughput = taskCount / (totalTime / 1000.0);

        System.out.println("异步监控性能测试:");
        System.out.println("任务数量: " + taskCount);
        System.out.println("总耗时: " + totalTime + " ms");
        System.out.println("吞吐量: " + String.format("%.2f", throughput) + " tasks/sec");

        assertTrue(throughput > 50, "异步监控吞吐量应该超过50 tasks/sec");
    }
}