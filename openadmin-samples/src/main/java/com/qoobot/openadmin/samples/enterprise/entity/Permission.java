package com.qoobot.openadmin.samples.enterprise.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限实体类
 */
@Data
@Entity
@Table(name = "sample_permissions")
@EqualsAndHashCode(callSuper = true)
public class Permission extends BaseEntity {
    
    @Column(unique = true, nullable = false)
    private String code;
    
    private String name;
    
    private String description;
    
    @Column(name = "resource_type")
    private String resourceType;
    
    private String action;
}