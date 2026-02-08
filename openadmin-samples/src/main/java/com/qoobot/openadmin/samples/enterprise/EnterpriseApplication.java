package com.qoobot.openadmin.samples.enterprise;

import com.qoobot.openadmin.starter.EnableOpenAdmin;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 企业管理系统示例应用
 * 演示用户管理、部门管理、菜单管理等企业级功能
 */
@SpringBootApplication
@EnableOpenAdmin(
    security = true,
    monitoring = true,
    config = true,
    admin = true
)
@ComponentScan(basePackages = {
    "com.qoobot.openadmin.samples.enterprise",
    "com.qoobot.openadmin.security",
    "com.qoobot.openadmin.monitor"
})
public class EnterpriseApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnterpriseApplication.class, args);
    }
}