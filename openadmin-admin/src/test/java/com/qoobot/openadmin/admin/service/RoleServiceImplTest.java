package com.qoobot.openadmin.admin.service;

import com.qoobot.openadmin.admin.entity.Role;
import com.qoobot.openadmin.admin.mapper.RolePermissionMapper;
import com.qoobot.openadmin.admin.repository.RoleRepository;
import com.qoobot.openadmin.admin.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class RoleServiceImplTest {
    private RoleRepository roleRepository;
    private RolePermissionMapper rolePermissionMapper;
    private RoleServiceImpl roleService;

    @BeforeEach
    public void setup() {
        roleRepository = Mockito.mock(RoleRepository.class);
        rolePermissionMapper = Mockito.mock(RolePermissionMapper.class);
        roleService = new RoleServiceImpl(roleRepository, rolePermissionMapper);
    }

    @Test
    public void testPermissionAggregationWithInheritance() {
        // parent role
        Role parent = new Role();
        parent.setId("r1");
        parent.setPermissionIds(List.of("perm.read"));
        when(roleRepository.findById("r1")).thenReturn(Optional.of(parent));

        // child role
        Role child = new Role();
        child.setId("r2");
        child.setParentRoleId("r1");
        child.setPermissionIds(List.of("perm.write"));
        when(roleRepository.findById("r2")).thenReturn(Optional.of(child));

        when(rolePermissionMapper.findPermissionIdsByRoleId("r1")).thenReturn(List.of("perm.read"));
        when(rolePermissionMapper.findPermissionIdsByRoleId("r2")).thenReturn(List.of("perm.write"));

        List<String> perms = roleService.getPermissionsAggregated("r2");
        assertTrue(perms.contains("perm.read"));
        assertTrue(perms.contains("perm.write"));

        // assign new permission to parent and evict cache
        roleService.assignPermissions("r1", List.of("perm.read", "perm.admin"));
        when(rolePermissionMapper.findPermissionIdsByRoleId("r1")).thenReturn(List.of("perm.read", "perm.admin"));
        List<String> perms2 = roleService.getPermissionsAggregated("r2");
        assertTrue(perms2.contains("perm.admin"));
    }
}
