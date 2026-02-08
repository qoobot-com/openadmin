package com.qoobot.openadmin.monitor.aspect;

import com.qoobot.openadmin.monitor.service.MetricService;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 监控切面
 * 自动监控方法执行时间和异常
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class MonitoringAspect {

    private final MetricService metricService;

    /**
     * 监控业务方法执行
     */
    @Around("@annotation(com.qoobot.openadmin.monitor.annotation.Monitored)")
    public Object monitorBusinessMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String businessName = className + "." + methodName;
        
        log.debug("Starting monitoring for business method: {}", businessName);
        
        // 开始计时
        Timer.Sample sample = metricService.startBusinessTimer(businessName);
        
        try {
            // 执行目标方法
            Object result = joinPoint.proceed();
            
            // 记录成功执行
            metricService.incrementBusinessCounter(businessName);
            
            return result;
        } catch (Exception e) {
            // 记录异常
            metricService.recordException(e.getClass().getSimpleName());
            throw e;
        } finally {
            // 停止计时
            metricService.stopBusinessTimer(sample, businessName);
            log.debug("Finished monitoring for business method: {}", businessName);
        }
    }

    /**
     * 监控控制器方法执行时间
     */
    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object monitorRestController(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        try {
            return joinPoint.proceed();
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            metricService.recordResponseTime(duration);
        }
    }
}