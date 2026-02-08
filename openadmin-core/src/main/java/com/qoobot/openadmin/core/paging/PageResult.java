package com.qoobot.openadmin.core.paging;

import java.util.List;

/**
 * Page result contract used by service layer to return paginated results.
 */
public record PageResult<T>(List<T> items, long total, int page, int size) {
    public static <T> PageResult<T> of(List<T> items, long total, int page, int size) {
        return new PageResult<>(items, total, page, size);
    }
}

