package com.qoobot.openadmin.core.service;

import com.qoobot.openadmin.core.model.User;
import com.qoobot.openadmin.core.paging.PageResult;
import com.qoobot.openadmin.core.paging.Pageable;
import com.qoobot.openadmin.core.service.exceptions.ServiceException;
import com.qoobot.openadmin.core.service.exceptions.NotFoundException;
import com.qoobot.openadmin.core.service.exceptions.OptimisticLockException;

/**
 * Service interface for administrative user management.
 * <p>
 * Design principles:
 * - Non-invasive: this service is a boundary contract; business code should adapt to it without modification.
 * - Extensible: User record contains attributes map that can be used for custom models.
 * - Thread-safety: implementations must be thread-safe; this is a stateless contract.
 * </p>
 */
public interface AdminUserService {

    /**
     * Create a new administrative user.
     *
     * @param user user information to create (id may be null/ignored)
     * @return created user with id, version and timestamps populated
     * @throws IllegalArgumentException when required fields are missing or invalid
     * @throws ServiceException when creation fails due to system errors
     */
    User createUser(User user) throws ServiceException;

    /**
     * Update an existing user. Implementations should perform optimistic lock check using {@code user.version}.
     *
     * @param user user information with id and expected version
     * @return updated user
     * @throws NotFoundException if user not found
     * @throws OptimisticLockException if version conflict detected
     * @throws ServiceException on other failures
     */
    User updateUser(User user) throws ServiceException;

    /**
     * Soft-delete a user by id.
     *
     * @param userId id of user to delete
     * @throws NotFoundException if user not found
     * @throws ServiceException on system failure
     */
    void deleteUser(String userId) throws ServiceException;

    /**
     * Find a user by id.
     *
     * @param userId user id
     * @return user or null if not found
     * @throws ServiceException on system failure
     */
    User findUserById(String userId) throws ServiceException;

    /**
     * Find users using pagination.
     *
     * @param pageable paging and sorting parameters
     * @return paginated result
     * @throws ServiceException on failure
     */
    PageResult<User> findUsersByPage(Pageable pageable) throws ServiceException;

    /**
     * Change a user's password. Implementations should validate oldPassword and update hashedPassword.
     *
     * @param userId id of the user
     * @param oldPassword existing raw password (implementation should handle hashing/verification)
     * @param newPassword new raw password
     * @return true if password changed successfully
     * @throws NotFoundException if user not found
     * @throws ServiceException on failure
     */
    boolean changePassword(String userId, String oldPassword, String newPassword) throws ServiceException;
}
