package com.morphanone.denizenmod.config;

public interface DenizenModConfig {
    void saveDefaultConfig();

    void loadConfig();

    boolean getBoolean(String path, boolean def);
}
