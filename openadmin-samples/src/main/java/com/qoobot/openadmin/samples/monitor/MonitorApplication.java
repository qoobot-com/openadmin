package com.qoobot.openadmin.samples.monitor;

import com.qoobot.openadmin.starter.EnableOpenAdmin;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 监控示例应用
 * 演示应用性能监控、业务指标监控、监控告警等功能
 */
@SpringBootApplication
@EnableOpenAdmin(
    security = true,
    monitoring = true,
    gateway = true
)
@ComponentScan(basePackages = {
    "com.qoobot.openadmin.samples.monitor",
    "com.qoobot.openadmin.monitor",
    "com.qoobot.openadmin.security",
    "com.qoobot.openadmin.gateway"
})
public class MonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonitorApplication.class, args);
    }
}