package com.qoobot.openadmin.monitor.dto;

import lombok.Data;

/**
 * 监控响应DTO
 */
@Data
public class MonitorResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private long timestamp;

    public MonitorResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> MonitorResponse<T> success(T data, String message) {
        MonitorResponse<T> response = new MonitorResponse<>();
        response.success = true;
        response.data = data;
        response.message = message;
        return response;
    }

    public static <T> MonitorResponse<T> error(String message) {
        MonitorResponse<T> response = new MonitorResponse<>();
        response.success = false;
        response.message = message;
        return response;
    }
}