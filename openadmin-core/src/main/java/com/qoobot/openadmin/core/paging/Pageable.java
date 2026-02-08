package com.qoobot.openadmin.core.paging;

/**
 * Simple pageable contract for service layer boundaries.
 */
public record Pageable(int page, int size, String sort) {
    public static Pageable of(int page, int size, String sort) {
        return new Pageable(page, size, sort);
    }
}

