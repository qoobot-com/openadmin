package com.qoobot.openadmin.samples.simple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 简单示例应用 - 不依赖外部服务
 * 用于快速验证OpenAdmin核心功能
 */
@SpringBootApplication
public class SimpleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleApplication.class, args);
    }
}