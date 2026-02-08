package com.qoobot.openadmin.admin.entity;

import java.time.Instant;

/**
 * Persistent user entity for admin module (plain POJO for compile safety).
 */
public class User {
    private String id;
    private String username;
    private String displayName;
    private String email;
    private String mobile;
    private String passwordHash;
    private boolean enabled = true;
    private boolean deleted = false;
    private Long version;
    private Instant createdAt;
    private Instant updatedAt;
    private String departmentId;
    // Security related fields
    private Instant passwordExpiry;
    private int failedLoginAttempts;
    private Instant lockedUntil;

    public User() {}

    // getters and setters
    public Instant getPasswordExpiry() { return passwordExpiry; }
    public void setPasswordExpiry(Instant passwordExpiry) { this.passwordExpiry = passwordExpiry; }

    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(int failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }

    public Instant getLockedUntil() { return lockedUntil; }
    public void setLockedUntil(Instant lockedUntil) { this.lockedUntil = lockedUntil; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
}
