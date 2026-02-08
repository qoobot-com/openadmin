package com.qoobot.openadmin.monitor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 监控页面控制器
 */
@Controller
@RequestMapping("/monitor")
public class MonitorPageController {

    /**
     * 监控仪表板页面
     */
    @GetMapping("/dashboard")
    public String dashboard() {
        return "monitor/dashboard";
    }

    /**
     * 监控配置页面
     */
    @GetMapping("/config")
    public String config() {
        return "monitor/config";
    }
}