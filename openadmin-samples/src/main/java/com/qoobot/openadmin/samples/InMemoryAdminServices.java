package com.qoobot.openadmin.samples;

import com.qoobot.openadmin.core.model.*;
import com.qoobot.openadmin.core.paging.PageResult;
import com.qoobot.openadmin.core.paging.Pageable;
import com.qoobot.openadmin.core.service.*;
import com.qoobot.openadmin.core.service.exceptions.ServiceException;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryAdminServices implements AdminUserService, AdminRoleService, AdminMenuService, AdminPermissionService {
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, Role> roles = new ConcurrentHashMap<>();
    private final Map<String, Menu> menus = new ConcurrentHashMap<>();
    private final Map<String, Permission> permissions = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1000);

    private String nextId() {
        return String.valueOf(idGen.incrementAndGet());
    }

    // AdminUserService
    @Override
    public User createUser(User user) throws ServiceException {
        String id = nextId();
        User created = new User(id, user.username(), user.displayName(), user.email(), user.mobile(), user.hashedPassword(), user.attributes(), false, 0, Instant.now(), Instant.now());
        users.put(id, created);
        return created;
    }

    @Override
    public User updateUser(User user) throws ServiceException {
        if (user == null || user.id() == null) throw new ServiceException("user or id is null");
        users.compute(user.id(), (k, old) -> {
            if (old == null) return null;
            long newVersion = old.version() + 1;
            return new User(old.id(), user.username(), user.displayName(), user.email(), user.mobile(), user.hashedPassword(), user.attributes(), old.deleted(), newVersion, old.createdAt(), Instant.now());
        });
        return users.get(user.id());
    }

    @Override
    public void deleteUser(String userId) throws ServiceException {
        users.computeIfPresent(userId, (k, v) -> new User(v.id(), v.username(), v.displayName(), v.email(), v.mobile(), v.hashedPassword(), v.attributes(), true, v.version() + 1, v.createdAt(), Instant.now()));
    }

    @Override
    public User findUserById(String userId) throws ServiceException {
        return users.get(userId);
    }

    @Override
    public PageResult<User> findUsersByPage(Pageable pageable) throws ServiceException {
        List<User> list = new ArrayList<>(users.values());
        int from = Math.min(pageable.page() * pageable.size(), list.size());
        int to = Math.min(from + pageable.size(), list.size());
        return PageResult.of(list.subList(from, to), list.size(), pageable.page(), pageable.size());
    }

    @Override
    public boolean changePassword(String userId, String oldPassword, String newPassword) throws ServiceException {
        return true;
    }

    // AdminRoleService
    @Override
    public Role createRole(Role role) throws ServiceException {
        String id = nextId();
        Role r = new Role(id, role.name(), role.description(), role.permissionIds(), false, 0, Instant.now(), Instant.now());
        roles.put(id, r);
        return r;
    }

    @Override
    public Role updateRole(Role role) throws ServiceException {
        roles.put(role.id(), role);
        return role;
    }

    @Override
    public void deleteRole(String roleId) throws ServiceException {
        roles.remove(roleId);
    }

    @Override
    public void assignPermissionsToRole(String roleId, List<String> permissionIds) throws ServiceException {
        roles.computeIfPresent(roleId, (k, v) -> new Role(v.id(), v.name(), v.description(), permissionIds, v.deleted(), v.version() + 1, v.createdAt(), Instant.now()));
    }

    @Override
    public List<Role> findRolesByUserId(String userId) throws ServiceException {
        return List.copyOf(roles.values());
    }

    // AdminMenuService
    @Override
    public Menu createMenu(Menu menu) throws ServiceException {
        String id = nextId();
        Menu m = new Menu(id, menu.parentId(), menu.name(), menu.path(), menu.icon(), menu.order(), menu.meta(), menu.childrenIds(), false, 0, Instant.now(), Instant.now());
        menus.put(id, m);
        return m;
    }

    @Override
    public Menu updateMenu(Menu menu) throws ServiceException {
        menus.put(menu.id(), menu);
        return menu;
    }

    @Override
    public void deleteMenu(String menuId) throws ServiceException {
        menus.remove(menuId);
    }

    @Override
    public List<Menu> findMenusByRoleId(String roleId) throws ServiceException {
        return List.copyOf(menus.values());
    }

    @Override
    public List<Menu> findUserMenus(String userId) throws ServiceException {
        return List.copyOf(menus.values());
    }

    // AdminPermissionService
    @Override
    public Permission createPermission(Permission permission) throws ServiceException {
        String id = nextId();
        Permission p = new Permission(id, permission.code(), permission.name(), permission.description(), permission.attributes(), false, 0, Instant.now(), Instant.now());
        permissions.put(id, p);
        return p;
    }

    @Override
    public Permission updatePermission(Permission permission) throws ServiceException {
        permissions.put(permission.id(), permission);
        return permission;
    }

    @Override
    public void deletePermission(String permissionId) throws ServiceException {
        permissions.remove(permissionId);
    }

    @Override
    public boolean checkPermission(String userId, String permissionCode) throws ServiceException {
        return true;
    }
}

