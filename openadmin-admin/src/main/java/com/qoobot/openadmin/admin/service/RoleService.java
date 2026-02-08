package com.qoobot.openadmin.admin.service;

import com.qoobot.openadmin.admin.dto.RoleDTO;
import com.qoobot.openadmin.admin.entity.Role;

import java.util.List;

public interface RoleService {
    RoleDTO createRole(RoleDTO dto);
    RoleDTO updateRole(RoleDTO dto);
    void deleteRole(String roleId);
    RoleDTO findRoleById(String roleId);
    List<RoleDTO> findRolesByOrg(String organizationId);

    void assignPermissions(String roleId, List<String> permissionIds);
    List<String> getPermissionsAggregated(String roleId);

    void setParentRole(String roleId, String parentRoleId);
}

