package com.morphanone.denizenmod.config;

import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.morphanone.denizenmod.DenizenMod;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class YamlDenizenModConfig implements DenizenModConfig {
    public Path configDir;

    public Path configFile;

    public Path scriptsDir;

    public Path dataDir;

    private YamlConfiguration config;

    public YamlDenizenModConfig() {
        try {
            this.configDir = Files.createDirectories(DenizenMod.instance.getConfigDirectory());
            this.scriptsDir = Files.createDirectories(configDir.resolve("scripts"));
            this.dataDir = Files.createDirectories(configDir.resolve("data"));
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        this.configFile = configDir.resolve("config.yml");
    }

    public void saveDefaultConfig() {
        if (Files.exists(configFile)) {
            return;
        }
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("default_config.yml")) {
            Files.copy(Objects.requireNonNull(input), configFile);
        }
        catch (IOException e) {
            Debug.echoError(e);
        }
    }

    public void loadConfig() {
        saveDefaultConfig();
        try {
            try (InputStream input = Files.newInputStream(configFile)) {
                this.config = YamlConfiguration.load(input);
            }
        }
        catch (IOException e) {
            Debug.echoError(e);
        }
    }

    public boolean getBoolean(String path, boolean def) {
        return CoreUtilities.toLowerCase(config.getString(path, def ? "true" : "false")).equals("true");
    }
}
