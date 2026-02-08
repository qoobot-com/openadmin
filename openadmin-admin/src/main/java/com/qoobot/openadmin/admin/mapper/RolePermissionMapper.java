package com.qoobot.openadmin.admin.mapper;

import org.apache.ibatis.annotations.*;
import java.util.List;

public interface RolePermissionMapper {
    @Insert("INSERT INTO role_permission (role_id, permission_id) VALUES (#{roleId}, #{permissionId})")
    void insert(String roleId, String permissionId);

    @Delete("DELETE FROM role_permission WHERE role_id=#{roleId}")
    void deleteByRoleId(String roleId);

    @Select("SELECT permission_id FROM role_permission WHERE role_id=#{roleId}")
    List<String> findPermissionIdsByRoleId(String roleId);
}

