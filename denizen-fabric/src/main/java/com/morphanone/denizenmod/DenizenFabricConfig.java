package com.morphanone.denizenmod;

import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class DenizenFabricConfig {
    public Path configDir;

    public Path configFile;

    public Path scriptsDir;

    public Path dataDir;

    public DenizenFabricConfig() {
        try {
            this.configDir = Files.createDirectories(FabricLoader.getInstance().getConfigDir().resolve(DenizenMod.MOD_ID));
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

    public YamlConfiguration loadConfig() {
        saveDefaultConfig();
        try (InputStream input = Files.newInputStream(configFile)) {
            return YamlConfiguration.load(input);
        }
        catch (IOException e) {
            Debug.echoError(e);
        }
        return null;
    }
}
