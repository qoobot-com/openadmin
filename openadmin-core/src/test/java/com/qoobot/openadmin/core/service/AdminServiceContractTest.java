package com.qoobot.openadmin.core.service;

import com.qoobot.openadmin.core.model.*;
import com.qoobot.openadmin.core.paging.PageResult;
import com.qoobot.openadmin.core.paging.Pageable;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple contract test to ensure service interfaces and model records compile and basic contracts are satisfied.
 */
public class AdminServiceContractTest {

    @Test
    public void interfacesShouldBePresent() {
        AdminUserService userService = new AdminUserService() {
            @Override
            public User createUser(User user) {
                return user;
            }

            @Override
            public User updateUser(User user) {
                return user;
            }

            @Override
            public void deleteUser(String userId) {
            }

            @Override
            public User findUserById(String userId) {
                return null;
            }

            @Override
            public PageResult<User> findUsersByPage(Pageable pageable) {
                return PageResult.of(Collections.emptyList(), 0, pageable.page(), pageable.size());
            }

            @Override
            public boolean changePassword(String userId, String oldPassword, String newPassword) {
                return true;
            }
        };

        assertNotNull(userService);

        AdminRoleService roleService = new AdminRoleService() {
            @Override
            public Role createRole(Role role) {
                return role;
            }

            @Override
            public Role updateRole(Role role) {
                return role;
            }

            @Override
            public void deleteRole(String roleId) {
            }

            @Override
            public void assignPermissionsToRole(String roleId, java.util.List<String> permissionIds) {
            }

            @Override
            public java.util.List<Role> findRolesByUserId(String userId) {
                return java.util.List.of();
            }
        };
        assertNotNull(roleService);

        AdminMenuService menuService = new AdminMenuService() {
            @Override
            public Menu createMenu(Menu menu) {
                return menu;
            }

            @Override
            public Menu updateMenu(Menu menu) {
                return menu;
            }

            @Override
            public void deleteMenu(String menuId) {
            }

            @Override
            public java.util.List<Menu> findMenusByRoleId(String roleId) {
                return java.util.List.of();
            }

            @Override
            public java.util.List<Menu> findUserMenus(String userId) {
                return java.util.List.of();
            }
        };
        assertNotNull(menuService);

        AdminPermissionService permissionService = new AdminPermissionService() {
            @Override
            public Permission createPermission(Permission permission) {
                return permission;
            }

            @Override
            public Permission updatePermission(Permission permission) {
                return permission;
            }

            @Override
            public void deletePermission(String permissionId) {
            }

            @Override
            public boolean checkPermission(String userId, String permissionCode) {
                return false;
            }
        };
        assertNotNull(permissionService);

        // sanity check entity records
        User u = new User("1", "admin", "Administrator", "admin@example.com", "", null, Collections.emptyMap(), false, 0, Instant.now(), Instant.now());
        assertEquals("admin", u.username());
    }
}
