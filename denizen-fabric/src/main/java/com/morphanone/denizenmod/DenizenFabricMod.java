package com.morphanone.denizenmod;

import com.denizenscript.denizencore.DenizenCore;
import com.denizenscript.denizencore.DenizenImplementation;
import com.denizenscript.denizencore.flags.FlaggableObject;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.queues.ScriptQueue;
import com.denizenscript.denizencore.utilities.ExCommandHelper;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.morphanone.denizenmod.minecraft.commands.ExCommand;
import com.morphanone.denizenmod.minecraft.commands.ExsCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.commands.CommandSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

public class DenizenFabricMod implements ModInitializer, DenizenImplementation, DenizenModImplementation {
    public static final String MOD_ID = "denizen";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static MinecraftServer SERVER;

    static {
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            SERVER = server;
            ExCommandHelper.sustainedQueues.clear();
            DenizenCore.scheduled.clear();
            DenizenCore.MAIN_THREAD = Thread.currentThread();
            Debug.log("Server started! Loading saved data and allowing commands...");
            DenizenCore.reloadSaves();
        });
        ServerLifecycleEvents.SERVER_STOPPING.register((server) -> {
            Debug.log("Server stopping! Saving data and disabling commands...");
            DenizenCore.shutdown();
            DenizenCore.MAIN_THREAD = null;
            ExCommandHelper.sustainedQueues.clear();
            DenizenCore.scheduled.clear();
            SERVER = null;
        });
    }

    public static DenizenFabricMod instance;

    public ModContainer container;

    public Path configDir;

    public Path scriptsDir;

    public Path dataDir;

    public Path configFile;

    public DenizenFabricMod() {
        instance = this;
    }

    @Override
    public void onInitialize() {
        container = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow(() -> new IllegalStateException("Failed to find Denizen mod"));
        try {
            configDir = Files.createDirectories(FabricLoader.getInstance().getConfigDir().resolve(MOD_ID));
            scriptsDir = Files.createDirectories(configDir.resolve("scripts"));
            dataDir = Files.createDirectories(configDir.resolve("data"));
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        configFile = configDir.resolve("config.yml");
        DenizenMod.init(this);
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            ExCommand.register(dispatcher);
            ExsCommand.register(dispatcher);
        }));
        ServerTickEvents.START_SERVER_TICK.register((server) -> DenizenCore.tick(50));
        Debug.log("Waiting for server to start...");
        DenizenCore.MAIN_THREAD = null;
    }

    public static ResourceLocation resource(String id) {
        return new ResourceLocation(MOD_ID, id);
    }

    public static CommandSource getDebugTarget() {
        return SERVER;
    }

    @Override
    public Player findPlayer(UUID uuid) {
        return SERVER.getPlayerList().getPlayer(uuid);
    }

    @Override
    public Player findPlayerByName(String name) {
        return SERVER.getPlayerList().getPlayerByName(name);
    }

    @Override
    public CommandSource getMainCommandSource() {
        return SERVER;
    }

    private void saveDefaultConfig() {
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

    //region Core Implementation
    @Override
    public File getScriptFolder() {
        return scriptsDir.toFile();
    }

    @Override
    public File getDataFolder() {
        return dataDir.toFile();
    }

    @Override
    public String getImplementationVersion() {
        return container.getMetadata().getVersion().getFriendlyString();
    }

    @Override
    public String getImplementationName() {
        return "Fabric";
    }

    @Override
    public void preScriptReload() {
    }

    @Override
    public void onScriptReload() {
    }

    @Override
    public void refreshScriptContainers() {
    }

    @Override
    public void preTagExecute() {
    }

    @Override
    public void postTagExecute() {
    }

    @Override
    public boolean canWriteToFile(File file) {
        return false;
    }

    @Override
    public boolean canReadFile(File file) {
        return false;
    }

    @Override
    public void addExtraErrorHeaders(StringBuilder headerBuilder, ScriptEntry source) {
    }

    @Override
    public FlaggableObject simpleWordToFlaggable(String word, ScriptEntry entry) {
        return null;
    }

    @Override
    public ObjectTag getSpecialDef(String def, ScriptQueue queue) {
        return null;
    }

    @Override
    public boolean setSpecialDef(String def, ScriptQueue queue, ObjectTag value) {
        return false;
    }

    @Override
    public void doFinalDebugOutput(String rawText) {
        //                                         This can change slightly, just do our best here
        //                                            [HH:mm:ss] [Render thread/INFO] (denizen)
        LOGGER.info(rawText.replace("<FORCE_ALIGN>", "                                          "));
    }
    //endregion
}
