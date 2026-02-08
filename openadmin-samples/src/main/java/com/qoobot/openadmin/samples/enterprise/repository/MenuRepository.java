package com.qoobot.openadmin.samples.enterprise.repository;

import com.qoobot.openadmin.samples.enterprise.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 菜单仓库接口
 */
@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    
    List<Menu> findByParentIdOrderBySortAsc(Long parentId);
    
    List<Menu> findByEnabledTrueAndVisibleTrueOrderByLevelAscSortAsc();
    
    List<Menu> findByRolesName(String roleName);
}