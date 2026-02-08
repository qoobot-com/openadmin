package com.qoobot.openadmin.samples.enterprise.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

/**
 * 角色实体类
 */
@Data
@Entity
@Table(name = "sample_roles")
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseEntity {
    
    @Column(unique = true, nullable = false)
    private String name;
    
    private String description;
    
    @Column(name = "is_system")
    private Boolean systemRole = false;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "sample_role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions;
}