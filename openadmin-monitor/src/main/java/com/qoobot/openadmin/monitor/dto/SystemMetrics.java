package com.qoobot.openadmin.monitor.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统指标DTO
 */
@Data
@Builder
public class SystemMetrics {
    private LocalDateTime timestamp;
    private long heapMemoryUsed;
    private long heapMemoryMax;
    private int threadCount;
    private int peakThreadCount;
    private int daemonThreadCount;
    private long gcCollectionCount;
}