package com.qoobot.openadmin.samples.enterprise.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

/**
 * 菜单实体类
 */
@Data
@Entity
@Table(name = "sample_menus")
@EqualsAndHashCode(callSuper = true)
public class Menu extends BaseEntity {
    
    @Column(nullable = false)
    private String name;
    
    private String code;
    
    private String path;
    
    private String component;
    
    private String icon;
    
    @Column(name = "parent_id")
    private Long parentId;
    
    private Integer level;
    
    private Integer sort;
    
    @Column(name = "is_enabled")
    private Boolean enabled = true;
    
    @Column(name = "is_visible")
    private Boolean visible = true;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "sample_menu_roles",
        joinColumns = @JoinColumn(name = "menu_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;
}