package com.qoobot.openadmin.core.model;

import java.time.Instant;
import java.util.List;

/**
 * Represents a Role in RBAC model.
 * Immutable record; includes soft-delete and optimistic-locking fields.
 * Thread-safety: immutable.
 */
public record Role(
        String id,
        String name,
        String description,
        List<String> permissionIds,
        boolean deleted,
        long version,
        Instant createdAt,
        Instant updatedAt
) {
}

