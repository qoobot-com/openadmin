package com.qoobot.openadmin.core.service;

public interface PermissionService {
    boolean hasPermission(String user, String permission);
}

