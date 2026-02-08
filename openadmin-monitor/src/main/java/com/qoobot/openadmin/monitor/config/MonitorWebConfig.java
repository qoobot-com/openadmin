package com.qoobot.openadmin.monitor.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 监控Web配置
 */
@Configuration
public class MonitorWebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/monitor/dashboard");
        registry.addViewController("/monitor").setViewName("redirect:/monitor/dashboard");
    }
}