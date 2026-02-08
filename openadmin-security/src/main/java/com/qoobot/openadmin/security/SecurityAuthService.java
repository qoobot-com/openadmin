package com.qoobot.openadmin.security;

public class SecurityAuthService {
    public boolean authenticate(String user, String password) {
        // Placeholder simple authentication
        return "admin".equals(user) && "password".equals(password);
    }
}

