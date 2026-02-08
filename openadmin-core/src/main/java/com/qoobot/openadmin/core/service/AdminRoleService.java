package com.qoobot.openadmin.core.service;

import com.qoobot.openadmin.core.model.Role;
import com.qoobot.openadmin.core.service.exceptions.ServiceException;
import com.qoobot.openadmin.core.service.exceptions.NotFoundException;
import com.qoobot.openadmin.core.service.exceptions.OptimisticLockException;
import java.util.List;

/**
 * Service interface for role management in an RBAC model.
 * Implementations must be thread-safe.
 */
public interface AdminRoleService {

    /**
     * Create a new role.
     *
     * @param role role to create
     * @return created role with id/version populated
     * @throws ServiceException on failure
     */
    Role createRole(Role role) throws ServiceException;

    /**
     * Update an existing role.
     *
     * @param role role with id and expected version
     * @return updated role
     * @throws NotFoundException if role not found
     * @throws OptimisticLockException if version mismatch
     * @throws ServiceException on failure
     */
    Role updateRole(Role role) throws ServiceException;

    /**
     * Soft-delete a role by id.
     *
     * @param roleId id of the role
     * @throws NotFoundException if role not found
     * @throws ServiceException on failure
     */
    void deleteRole(String roleId) throws ServiceException;

    /**
     * Assign a list of permission ids to the role (replace existing assignment).
     *
     * @param roleId id of role
     * @param permissionIds permission identifiers
     * @throws NotFoundException if role not found
     * @throws ServiceException on failure
     */
    void assignPermissionsToRole(String roleId, List<String> permissionIds) throws ServiceException;

    /**
     * Find roles assigned to a user.
     *
     * @param userId user id
     * @return list of roles (may be empty)
     * @throws ServiceException on failure
     */
    List<Role> findRolesByUserId(String userId) throws ServiceException;
}
