package com.qoobot.openadmin.admin.service.impl;

import com.qoobot.openadmin.admin.dto.RoleDTO;
import com.qoobot.openadmin.admin.entity.Role;
import com.qoobot.openadmin.admin.mapper.RolePermissionMapper;
import com.qoobot.openadmin.admin.repository.RoleRepository;
import com.qoobot.openadmin.admin.service.RoleService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simplified in-memory implementation that demonstrates RBAC + inheritance + caching.
 * In production, this would use JPA/Redis and a distributed transaction manager.
 */
@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final RolePermissionMapper rolePermissionMapper;

    // simple cache to simulate Redis (roleId -> aggregated permission set)
    private final Map<String, Set<String>> permissionCache = new ConcurrentHashMap<>();

    public RoleServiceImpl(RoleRepository roleRepository, RolePermissionMapper rolePermissionMapper) {
        this.roleRepository = roleRepository;
        this.rolePermissionMapper = rolePermissionMapper;
    }

    @Override
    @PreAuthorize("hasAuthority('perm.admin') or hasRole('ADMIN')")
    public RoleDTO createRole(RoleDTO dto) {
        Role r = toEntity(dto);
        r.setId(UUID.randomUUID().toString());
        r.setCreatedAt(Instant.now());
        roleRepository.save(r);
        return toDTO(r);
    }

    @Override
    @PreAuthorize("hasAuthority('perm.admin') or hasRole('ADMIN')")
    public RoleDTO updateRole(RoleDTO dto) {
        Role r = toEntity(dto);
        r.setUpdatedAt(Instant.now());
        roleRepository.save(r);
        invalidateCache(r.getId());
        return toDTO(r);
    }

    @Override
    @PreAuthorize("hasAuthority('perm.admin') or hasRole('ADMIN')")
    public void deleteRole(String roleId) {
        roleRepository.deleteById(roleId);
        invalidateCache(roleId);
    }

    @Override
    public RoleDTO findRoleById(String roleId) {
        return roleRepository.findById(roleId).map(this::toDTO).orElse(null);
    }

    @Override
    public List<RoleDTO> findRolesByOrg(String organizationId) {
        if (organizationId == null) return List.of();
        List<Role> roles = roleRepository.findByOrganizationId(organizationId);
        List<RoleDTO> dtos = new ArrayList<>();
        for (Role r : roles) dtos.add(toDTO(r));
        return dtos;
    }

    @Override
    @PreAuthorize("hasAuthority('perm.admin') or hasRole('ADMIN')")
    @CacheEvict(value = "rolePermissions", key = "#roleId")
    public void assignPermissions(String roleId, List<String> permissionIds) {
        rolePermissionMapper.deleteByRoleId(roleId);
        for (String pid : permissionIds) rolePermissionMapper.insert(roleId, pid);
        invalidateCache(roleId);
    }

    @Override
    @PreAuthorize("hasAuthority('perm.admin') or hasRole('ADMIN')")
    @Cacheable(value = "rolePermissions", key = "#roleId")
    public List<String> getPermissionsAggregated(String roleId) {
        // build aggregated permissions by traversing parent links
        Set<String> agg = new LinkedHashSet<>();
        buildPermissionsRecursive(roleId, agg, new HashSet<>());
        return new ArrayList<>(agg);
    }

    private void buildPermissionsRecursive(String roleId, Set<String> agg, Set<String> visited) {
        if (roleId == null || roleId.isBlank() || visited.contains(roleId)) return;
        visited.add(roleId);
        // direct permissions
        List<String> direct = rolePermissionMapper.findPermissionIdsByRoleId(roleId);
        if (direct != null) agg.addAll(direct);
        // parent role
        Role parent = roleRepository.findById(roleId).orElse(null);
        if (parent != null && parent.getParentRoleId() != null) {
            buildPermissionsRecursive(parent.getParentRoleId(), agg, visited);
        }
    }

    @Override
    @PreAuthorize("hasAuthority('perm.admin') or hasRole('ADMIN')")
    @CacheEvict(value = "rolePermissions", key = "#roleId")
     public void setParentRole(String roleId, String parentRoleId) {
         Role r = roleRepository.findById(roleId).orElse(null);
         if (r == null) throw new IllegalArgumentException("role not found");
         r.setParentRoleId(parentRoleId);
         roleRepository.save(r);
         invalidateCache(roleId);
     }

    private void invalidateCache(String roleId) {
        permissionCache.remove(roleId);
    }

    private Role toEntity(RoleDTO dto) {
        Role r = new Role();
        r.setId(dto.getId());
        r.setName(dto.getName());
        r.setDescription(dto.getDescription());
        r.setParentRoleId(dto.getParentRoleId());
        r.setEnabled(dto.isEnabled());
        r.setOrganizationId(dto.getOrganizationId());
        r.setPermissionIds(dto.getPermissionIds());
        return r;
    }

    private RoleDTO toDTO(Role r) {
        RoleDTO d = new RoleDTO();
        d.setId(r.getId());
        d.setName(r.getName());
        d.setDescription(r.getDescription());
        d.setParentRoleId(r.getParentRoleId());
        d.setEnabled(r.isEnabled());
        d.setOrganizationId(r.getOrganizationId());
        d.setPermissionIds(r.getPermissionIds());
        d.setCreatedAt(r.getCreatedAt());
        d.setUpdatedAt(r.getUpdatedAt());
        return d;
    }
}
