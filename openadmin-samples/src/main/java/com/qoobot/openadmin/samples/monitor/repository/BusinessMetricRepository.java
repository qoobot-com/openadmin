package com.qoobot.openadmin.samples.monitor.repository;

import com.qoobot.openadmin.samples.monitor.entity.BusinessMetric;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 业务指标数据访问接口
 */
@Repository
public interface BusinessMetricRepository extends JpaRepository<BusinessMetric, Long> {
    
    /**
     * 根据业务域查询指标
     */
    List<BusinessMetric> findByBusinessDomain(String businessDomain);
    
    /**
     * 根据业务域和指标名称查询
     */
    List<BusinessMetric> findByBusinessDomainAndMetricName(String businessDomain, String metricName);
    
    /**
     * 分页查询指定时间范围内的业务指标
     */
    Page<BusinessMetric> findByBusinessDomainAndTimestampBetween(
            String businessDomain,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Pageable pageable);
    
    /**
     * 查询最新的N条记录
     */
    @Query("SELECT b FROM BusinessMetric b WHERE b.businessDomain = :domain ORDER BY b.timestamp DESC")
    List<BusinessMetric> findLatestByBusinessDomain(@Param("domain") String domain, Pageable pageable);
    
    /**
     * 统计指定时间范围内的总和
     */
    @Query("SELECT SUM(b.metricValue) FROM BusinessMetric b " +
           "WHERE b.businessDomain = :domain AND b.metricName = :metricName " +
           "AND b.timestamp BETWEEN :startTime AND :endTime")
    Double calculateSum(@Param("domain") String domain,
                       @Param("metricName") String metricName,
                       @Param("startTime") LocalDateTime startTime,
                       @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计指定时间范围内的平均值
     */
    @Query("SELECT AVG(b.metricValue) FROM BusinessMetric b " +
           "WHERE b.businessDomain = :domain AND b.metricName = :metricName " +
           "AND b.timestamp BETWEEN :startTime AND :endTime")
    Double calculateAverage(@Param("domain") String domain,
                          @Param("metricName") String metricName,
                          @Param("startTime") LocalDateTime startTime,
                          @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询最新的一条记录
     */
    @Query("SELECT b FROM BusinessMetric b WHERE b.businessDomain = :domain AND b.metricName = :metricName ORDER BY b.timestamp DESC")
    BusinessMetric findLatest(@Param("domain") String domain, @Param("metricName") String metricName);
}