package com.qoobot.openadmin.admin.dto;

import java.time.Instant;
import java.util.List;

public class RoleDTO {
    private String id;
    private String name;
    private String description;
    private String parentRoleId;
    private boolean enabled = true;
    private String organizationId;
    private List<String> permissionIds;
    private Instant createdAt;
    private Instant updatedAt;

    public RoleDTO() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getParentRoleId() { return parentRoleId; }
    public void setParentRoleId(String parentRoleId) { this.parentRoleId = parentRoleId; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getOrganizationId() { return organizationId; }
    public void setOrganizationId(String organizationId) { this.organizationId = organizationId; }

    public List<String> getPermissionIds() { return permissionIds; }
    public void setPermissionIds(List<String> permissionIds) { this.permissionIds = permissionIds; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}

