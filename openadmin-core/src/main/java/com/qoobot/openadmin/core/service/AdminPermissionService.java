package com.qoobot.openadmin.core.service;

import com.qoobot.openadmin.core.model.Permission;
import com.qoobot.openadmin.core.service.exceptions.ServiceException;
import com.qoobot.openadmin.core.service.exceptions.NotFoundException;
import com.qoobot.openadmin.core.service.exceptions.OptimisticLockException;

/**
 * Service interface for permission management and checking.
 * Implementations must be thread-safe and support extensible permission models.
 */
public interface AdminPermissionService {

    /**
     * Create a permission.
     *
     * @param permission permission to create
     * @return created permission
     * @throws ServiceException on failure
     */
    Permission createPermission(Permission permission) throws ServiceException;

    /**
     * Update a permission.
     *
     * @param permission permission with id and version
     * @return updated permission
     * @throws NotFoundException when permission not found
     * @throws OptimisticLockException on version conflict
     * @throws ServiceException on other failures
     */
    Permission updatePermission(Permission permission) throws ServiceException;

    /**
     * Soft-delete a permission.
     *
     * @param permissionId permission id
     * @throws NotFoundException when permission not found
     * @throws ServiceException on failure
     */
    void deletePermission(String permissionId) throws ServiceException;

    /**
     * Check whether a user has the permission specified by permissionCode.
     * Implementations should consider role membership and direct permission grants.
     *
     * @param userId user id
     * @param permissionCode permission code to check
     * @return true if user has permission
     * @throws ServiceException on failure
     */
    boolean checkPermission(String userId, String permissionCode) throws ServiceException;
}
