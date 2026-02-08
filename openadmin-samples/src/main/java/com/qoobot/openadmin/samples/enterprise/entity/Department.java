package com.qoobot.openadmin.samples.enterprise.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

/**
 * 部门实体类
 */
@Data
@Entity
@Table(name = "sample_departments")
@EqualsAndHashCode(callSuper = true)
public class Department extends BaseEntity {
    
    @Column(nullable = false)
    private String name;
    
    private String code;
    
    private String description;
    
    @Column(name = "parent_id")
    private Long parentId;
    
    private Integer level;
    
    private Integer sort;
    
    @Column(name = "is_enabled")
    private Boolean enabled = true;
    
    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    private Set<User> users;
}