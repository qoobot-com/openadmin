package com.qoobot.openadmin.samples.monitor.repository;

import com.qoobot.openadmin.samples.monitor.entity.MonitorAlert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 监控告警数据访问接口
 */
@Repository
public interface MonitorAlertRepository extends JpaRepository<MonitorAlert, Long> {
    
    /**
     * 根据状态查询告警
     */
    List<MonitorAlert> findByStatus(String status);
    
    /**
     * 根据严重程度查询告警
     */
    List<MonitorAlert> findBySeverity(String severity);
    
    /**
     * 根据应用名称查询告警
     */
    List<MonitorAlert> findByApplicationName(String applicationName);
    
    /**
     * 查询活跃告警
     */
    @Query("SELECT m FROM MonitorAlert m WHERE m.status = 'ACTIVE' ORDER BY m.triggerTime DESC")
    List<MonitorAlert> findActiveAlerts();
    
    /**
     * 分页查询指定时间范围内的告警
     */
    Page<MonitorAlert> findByTriggerTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    /**
     * 根据告警名称模糊查询
     */
    List<MonitorAlert> findByAlertNameContainingIgnoreCase(String alertName);
    
    /**
     * 统计各状态的告警数量
     */
    @Query("SELECT m.status, COUNT(m) FROM MonitorAlert m GROUP BY m.status")
    List<Object[]> countByStatus();
    
    /**
     * 统计各严重程度的告警数量
     */
    @Query("SELECT m.severity, COUNT(m) FROM MonitorAlert m GROUP BY m.severity")
    List<Object[]> countBySeverity();
    
    /**
     * 查询最近解决的告警
     */
    @Query("SELECT m FROM MonitorAlert m WHERE m.status = 'RESOLVED' ORDER BY m.resolveTime DESC")
    List<MonitorAlert> findRecentlyResolved(Pageable pageable);
    
    /**
     * 查询未确认的活跃告警
     */
    @Query("SELECT m FROM MonitorAlert m WHERE m.status = 'ACTIVE' AND m.acknowledgeTime IS NULL ORDER BY m.triggerTime ASC")
    List<MonitorAlert> findUnacknowledgedAlerts();
}