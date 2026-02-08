package com.qoobot.openadmin.samples.config;

import com.qoobot.openadmin.starter.EnableOpenAdmin;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 配置管理示例应用
 * 演示系统参数配置、配置热更新、配置审计等功能
 */
@SpringBootApplication
@EnableOpenAdmin(
    security = true,
    monitoring = true,
    config = true
)
@ComponentScan(basePackages = {
    "com.qoobot.openadmin.samples.config",
    "com.qoobot.openadmin.config",
    "com.qoobot.openadmin.security",
    "com.qoobot.openadmin.monitor"
})
public class ConfigApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigApplication.class, args);
    }
}