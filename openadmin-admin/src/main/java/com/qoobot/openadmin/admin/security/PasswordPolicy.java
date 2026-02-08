package com.qoobot.openadmin.admin.security;

import java.util.regex.Pattern;

/**
 * Simple password policy enforcement.
 */
public final class PasswordPolicy {
    private static final Pattern STRONG = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$");

    private PasswordPolicy() {}

    public static boolean isStrong(String password) {
        if (password == null) return false;
        return STRONG.matcher(password).matches();
    }
}

