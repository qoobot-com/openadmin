package com.qoobot.openadmin.samples.monitor.repository;

import com.qoobot.openadmin.samples.monitor.entity.PerformanceMetric;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 性能指标数据访问接口
 */
@Repository
public interface PerformanceMetricRepository extends JpaRepository<PerformanceMetric, Long> {
    
    /**
     * 根据应用名称查询指标
     */
    List<PerformanceMetric> findByApplicationName(String applicationName);
    
    /**
     * 根据应用名称和指标类型查询
     */
    List<PerformanceMetric> findByApplicationNameAndMetricType(String applicationName, String metricType);
    
    /**
     * 分页查询指定时间范围内的指标
     */
    Page<PerformanceMetric> findByApplicationNameAndTimestampBetween(
            String applicationName, 
            LocalDateTime startTime, 
            LocalDateTime endTime, 
            Pageable pageable);
    
    /**
     * 查询最新的N条记录
     */
    @Query("SELECT p FROM PerformanceMetric p WHERE p.applicationName = :appName ORDER BY p.timestamp DESC")
    List<PerformanceMetric> findLatestByApplicationName(@Param("appName") String appName, Pageable pageable);
    
    /**
     * 统计指定时间范围内的平均值
     */
    @Query("SELECT AVG(p.metricValue) FROM PerformanceMetric p " +
           "WHERE p.applicationName = :appName AND p.metricName = :metricName " +
           "AND p.timestamp BETWEEN :startTime AND :endTime")
    Double calculateAverage(@Param("appName") String appName, 
                          @Param("metricName") String metricName,
                          @Param("startTime") LocalDateTime startTime,
                          @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询最大值
     */
    @Query("SELECT MAX(p.metricValue) FROM PerformanceMetric p " +
           "WHERE p.applicationName = :appName AND p.metricName = :metricName " +
           "AND p.timestamp BETWEEN :startTime AND :endTime")
    Double findMaxValue(@Param("appName") String appName,
                       @Param("metricName") String metricName,
                       @Param("startTime") LocalDateTime startTime,
                       @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询最小值
     */
    @Query("SELECT MIN(p.metricValue) FROM PerformanceMetric p " +
           "WHERE p.applicationName = :appName AND p.metricName = :metricName " +
           "AND p.timestamp BETWEEN :startTime AND :endTime")
    Double findMinValue(@Param("appName") String appName,
                       @Param("metricName") String metricName,
                       @Param("startTime") LocalDateTime startTime,
                       @Param("endTime") LocalDateTime endTime);
}