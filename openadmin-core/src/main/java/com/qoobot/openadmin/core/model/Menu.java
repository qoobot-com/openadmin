package com.qoobot.openadmin.core.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Menu entry used to build the UI navigation tree.
 * Supports hierarchical relationships via parentId and an ordered list of children IDs.
 * Immutable record; thread-safe.
 */
public record Menu(
        String id,
        String parentId,
        String name,
        String path,
        String icon,
        int order,
        Map<String, Object> meta,
        List<String> childrenIds,
        boolean deleted,
        long version,
        Instant createdAt,
        Instant updatedAt
) {
}

