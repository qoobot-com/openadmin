package com.qoobot.openadmin.admin.service.impl;

import com.qoobot.openadmin.admin.dto.UserDTO;
import com.qoobot.openadmin.admin.entity.User;
import com.qoobot.openadmin.admin.mapper.UserMapper;
import com.qoobot.openadmin.admin.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(userDTO.getUsername());
        user.setDisplayName(userDTO.getDisplayName());
        user.setEmail(userDTO.getEmail());
        user.setMobile(userDTO.getMobile());
        user.setPasswordHash(passwordEncoder.encode("defaultPassword"));
        user.setEnabled(userDTO.isEnabled());
        user.setDeleted(false);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        user.setDepartmentId(userDTO.getDepartmentId());
        userMapper.insert(user);
        UserDTO dto = new UserDTO();
        // copy properties
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setDisplayName(user.getDisplayName());
        dto.setEmail(user.getEmail());
        dto.setMobile(user.getMobile());
        dto.setEnabled(user.isEnabled());
        dto.setDepartmentId(user.getDepartmentId());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO) {
        User user = userMapper.findById(userDTO.getId());
        if (user == null) throw new RuntimeException("User not found");
        user.setDisplayName(userDTO.getDisplayName());
        user.setEmail(userDTO.getEmail());
        user.setMobile(userDTO.getMobile());
        user.setUpdatedAt(Instant.now());
        userMapper.update(user);
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setDisplayName(user.getDisplayName());
        dto.setEmail(user.getEmail());
        dto.setMobile(user.getMobile());
        dto.setEnabled(user.isEnabled());
        dto.setDepartmentId(user.getDepartmentId());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    @Override
    public void deleteUser(String userId) {
        User user = userMapper.findById(userId);
        if (user == null) throw new RuntimeException("User not found");
        user.setDeleted(true);
        userMapper.update(user);
        userMapper.delete(userId);
    }

    @Override
    public UserDTO findUserById(String userId) {
        User user = userMapper.findById(userId);
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setDisplayName(user.getDisplayName());
        dto.setEmail(user.getEmail());
        dto.setMobile(user.getMobile());
        dto.setEnabled(user.isEnabled());
        dto.setDepartmentId(user.getDepartmentId());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    @Override
    public com.qoobot.openadmin.core.paging.PageResult<UserDTO> findUsersByPage(com.qoobot.openadmin.core.paging.Pageable pageable) {
        int offset = pageable.page()*pageable.size();
        // if sort field is used as departmentId (controller passes departmentId in sort param), honor it
        String dept = pageable.sort();
        var list = (dept == null || dept.isBlank()) ? userMapper.findPage(offset, pageable.size()) : userMapper.findPageByDepartment(dept, offset, pageable.size());
        var items = list.stream().map(u -> {
            UserDTO dto = new UserDTO();
            dto.setId(u.getId());
            dto.setUsername(u.getUsername());
            dto.setDisplayName(u.getDisplayName());
            dto.setEmail(u.getEmail());
            dto.setMobile(u.getMobile());
            dto.setEnabled(u.isEnabled());
            dto.setDepartmentId(u.getDepartmentId());
            dto.setCreatedAt(u.getCreatedAt());
            dto.setUpdatedAt(u.getUpdatedAt());
            return dto;
        }).toList();
        return com.qoobot.openadmin.core.paging.PageResult.of(items, items.size(), pageable.page(), pageable.size());
    }

    @Override
    public void assignRoles(String userId, List<String> roleIds) {
        // placeholder: record the role assignments via a join table or external service
    }

    @Override
    public void assignPermissions(String userId, List<String> permissionIds) {
        // placeholder
    }

    @Override
    public void enableUser(String userId) {
        User u = userMapper.findById(userId);
        if (u == null) throw new RuntimeException("User not found");
        u.setEnabled(true);
        userMapper.update(u);
    }

    @Override
    public void disableUser(String userId) {
        User u = userMapper.findById(userId);
        if (u == null) throw new RuntimeException("User not found");
        u.setEnabled(false);
        userMapper.update(u);
    }

    @Override
    public boolean changePassword(String userId, String oldPassword, String newPassword) {
        User u = userMapper.findById(userId);
        if (u == null) throw new RuntimeException("User not found");
        // check lock
        if (u.getLockedUntil() != null && u.getLockedUntil().isAfter(Instant.now())) return false;
        // verify old
        if (!passwordEncoder.matches(oldPassword, u.getPasswordHash())) return false;
        // validate strength
        if (!com.qoobot.openadmin.admin.security.PasswordPolicy.isStrong(newPassword)) return false;
        u.setPasswordHash(passwordEncoder.encode(newPassword));
        u.setPasswordExpiry(Instant.now().plusSeconds(60L*60*24*90)); // 90 days
        u.setFailedLoginAttempts(0);
        u.setLockedUntil(null);
        userMapper.update(u);
        return true;
    }

    @Override
    public void recordFailedLogin(String userId) {
        User u = userMapper.findById(userId);
        if (u == null) return;
        int attempts = u.getFailedLoginAttempts() + 1;
        u.setFailedLoginAttempts(attempts);
        if (attempts >= 5) {
            u.setLockedUntil(Instant.now().plusSeconds(60L*15)); // 15 minute lock
        }
        userMapper.update(u);
    }

    @Override
    public void resetFailedAttempts(String userId) {
        User u = userMapper.findById(userId);
        if (u == null) return;
        u.setFailedLoginAttempts(0);
        u.setLockedUntil(null);
        userMapper.update(u);
    }
}
