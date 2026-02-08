package com.qoobot.openadmin.admin.service;

import com.qoobot.openadmin.admin.dto.UserDTO;
import com.qoobot.openadmin.core.paging.PageResult;
import com.qoobot.openadmin.core.paging.Pageable;

import java.util.List;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(UserDTO userDTO);
    void deleteUser(String userId);
    UserDTO findUserById(String userId);
    PageResult<UserDTO> findUsersByPage(Pageable pageable);
    void assignRoles(String userId, List<String> roleIds);
    void assignPermissions(String userId, List<String> permissionIds);
    void enableUser(String userId);
    void disableUser(String userId);
    boolean changePassword(String userId, String oldPassword, String newPassword);
    void recordFailedLogin(String userId);
    void resetFailedAttempts(String userId);
}
