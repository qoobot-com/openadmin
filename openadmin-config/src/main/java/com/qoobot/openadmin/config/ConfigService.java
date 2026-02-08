package com.qoobot.openadmin.config;

public class ConfigService {
    public String getConfig(String key) {
        return "${" + key + "}"; // placeholder
    }
}

