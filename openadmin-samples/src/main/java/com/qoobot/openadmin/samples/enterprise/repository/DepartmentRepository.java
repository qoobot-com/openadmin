package com.qoobot.openadmin.samples.enterprise.repository;

import com.qoobot.openadmin.samples.enterprise.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 部门仓库接口
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    
    List<Department> findByParentIdOrderBySortAsc(Long parentId);
    
    List<Department> findByEnabledTrueOrderByLevelAscSortAsc();
}