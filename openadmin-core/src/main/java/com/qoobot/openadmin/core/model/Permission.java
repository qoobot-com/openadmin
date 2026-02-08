package com.qoobot.openadmin.core.model;

import java.time.Instant;
import java.util.Map;

/**
 * Permission entity representing an atomic permission (action/resource) in RBAC.
 * Designed to be extensible via attributes map so custom permission models are supported.
 */
public record Permission(
        String id,
        String code,
        String name,
        String description,
        Map<String, Object> attributes,
        boolean deleted,
        long version,
        Instant createdAt,
        Instant updatedAt
) {
}

