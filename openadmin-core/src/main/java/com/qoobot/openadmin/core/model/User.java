package com.qoobot.openadmin.core.model;

import java.time.Instant;
import java.util.Map;

/**
 * Represents an administrative user in the OpenAdmin system.
 * <p>
 * This is designed as a Java 25 record for immutability. Soft-delete and optimistic
 * locking fields are included to support common enterprise patterns.
 * </p>
 *
 * Thread-safety: immutable value type; safe to share between threads.
 */
public record User(
        String id,
        String username,
        String displayName,
        String email,
        String mobile,
        String hashedPassword,
        Map<String, Object> attributes,
        boolean deleted,
        long version,
        Instant createdAt,
        Instant updatedAt
) {
}

