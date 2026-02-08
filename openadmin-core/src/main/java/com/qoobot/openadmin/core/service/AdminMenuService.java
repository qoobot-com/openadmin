package com.qoobot.openadmin.core.service;

import com.qoobot.openadmin.core.model.Menu;
import com.qoobot.openadmin.core.service.exceptions.ServiceException;
import com.qoobot.openadmin.core.service.exceptions.NotFoundException;
import com.qoobot.openadmin.core.service.exceptions.OptimisticLockException;
import java.util.List;

/**
 * Service interface for menu management. Supports tree-structured menus.
 * Implementations must ensure thread-safety and use optimistic locking where appropriate.
 */
public interface AdminMenuService {

    /**
     * Create a menu entry.
     *
     * @param menu menu to create
     * @return created menu
     * @throws ServiceException on failure
     */
    Menu createMenu(Menu menu) throws ServiceException;

    /**
     * Update a menu entry.
     *
     * @param menu menu with id and version
     * @return updated menu
     * @throws NotFoundException if menu not found
     * @throws OptimisticLockException if version mismatch
     * @throws ServiceException on failure
     */
    Menu updateMenu(Menu menu) throws ServiceException;

    /**
     * Soft-delete a menu by id.
     *
     * @param menuId menu id
     * @throws NotFoundException if menu not found
     * @throws ServiceException on failure
     */
    void deleteMenu(String menuId) throws ServiceException;

    /**
     * Find menus available to a role (flattened list). Implementations should consider caching.
     *
     * @param roleId role id
     * @return list of menu entries
     * @throws ServiceException on failure
     */
    List<Menu> findMenusByRoleId(String roleId) throws ServiceException;

    /**
     * Find menus for a user; should return menu tree entries appropriate for the user's roles/permissions.
     *
     * @param userId user id
     * @return list of menu entries (top-level entries with childrenIds populated)
     * @throws ServiceException on failure
     */
    List<Menu> findUserMenus(String userId) throws ServiceException;
}
