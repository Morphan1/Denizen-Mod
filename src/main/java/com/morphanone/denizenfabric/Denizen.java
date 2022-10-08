package com.morphanone.denizenfabric;

import com.denizenscript.denizencore.DenizenCore;
import com.denizenscript.denizencore.DenizenImplementation;
import com.denizenscript.denizencore.flags.FlaggableObject;
import com.denizenscript.denizencore.objects.Argument;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ScriptTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.denizenscript.denizencore.scripts.containers.ScriptContainer;
import com.denizenscript.denizencore.scripts.queues.ScriptQueue;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.tags.TagManager;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.ExCommandHelper;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.denizenscript.denizencore.utilities.debugging.StrongWarning;
import com.morphanone.denizenfabric.commands.Commands;
import com.morphanone.denizenfabric.minecraft.commands.ExCommand;
import com.morphanone.denizenfabric.minecraft.commands.ExsCommand;
import com.morphanone.denizenfabric.scripts.FabricScriptEntryData;
import com.morphanone.denizenfabric.tags.FabricTagContext;
import com.morphanone.denizenfabric.tags.TagFactories;
import com.morphanone.denizenfabric.utilities.AnsiChatFormatting;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.ChatFormatting;
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
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class Denizen implements ModInitializer, DenizenImplementation, DenizenModImplementation {
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

    public static Denizen instance;

    public ModContainer container;

    public Path configDir;

    public Path scriptsDir;

    public Path dataDir;

    public Path configFile;

    public YamlConfiguration config;

    public Denizen() {
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
        loadConfig();
        if (Settings.enforceLocale()) {
            Locale.setDefault(Locale.US);
        }
        initCore(this);
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

    private static void initCore(Denizen instance) {
        CoreUtilities.basicContext = new FabricTagContext(Denizen::getDebugTarget);
        CoreUtilities.errorButNoDebugContext = new FabricTagContext(Denizen::getDebugTarget).debug(false);
        CoreUtilities.noDebugContext = new FabricTagContext(Denizen::getDebugTarget).debug(false).showErrors(() -> false);
        DenizenCore.init(instance);
        bootstrapRegistries();
    }

    private static final Map<String, Supplier<?>> REGISTRY_LOADERS = Map.of(
            "Command", Commands::bootstrap,
            "TagFactory", TagFactories::bootstrap
    );

    private static void bootstrapRegistries() {
        REGISTRY_LOADERS.forEach((name, supplier) -> {
            if (supplier.get() == null) {
                Debug.echoError("Failed to load " + supplier + " registry!");
            }
        });
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

    private void loadConfig() {
        saveDefaultConfig();
        try (InputStream input = Files.newInputStream(configFile)) {
            config = YamlConfiguration.load(input);
        }
        catch (IOException e) {
            Debug.echoError(e);
        }
    }

    //region Core Implementation
    @Override
    public File getScriptFolder() {
        return Denizen.instance.scriptsDir.toFile();
    }

    @Override
    public File getDataFolder() {
        return Denizen.instance.dataDir.toFile();
    }

    @Override
    public String getImplementationVersion() {
        return Denizen.instance.container.getMetadata().getVersion().getFriendlyString();
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
    public ScriptEntryData getEmptyScriptEntryData() {
        return new FabricScriptEntryData(new FabricTagContext(Denizen::getDebugTarget));
    }

    @Override
    public boolean needsHandleArgPrefix(String prefix) {
        return prefix.equals("player");
    }

    public static final StrongWarning INVALID_PLAYER_ARG = new StrongWarning("invalidPlayerArg", "The 'player:' arg should not be used in commands like define/flag/yaml/... input the player directly instead.");
    public static final Set<String> INVALID_PLAYER_ARG_COMMANDS = Set.of("DEFINE", "FLAG", "YAML");

    @Override
    public boolean handleCustomArgs(ScriptEntry entry, Argument arg) {
        if (arg.matchesPrefix("player")) {
            if (INVALID_PLAYER_ARG_COMMANDS.contains(entry.getCommandName())) {
                INVALID_PLAYER_ARG.warn(entry);
            }
            Debug.echoDebug(entry, "Setting this queue's current player to: " + arg.getValue());
            String value = TagManager.tag(arg.getValue(), entry.getContext());
            ((FabricTagContext) entry.getContext()).player(TagFactories.PLAYER.valueOf(value, entry.getContext()));
            return true;
        }
        return false;
    }

    @Override
    public void refreshScriptContainers() {
    }

    @Override
    public TagContext getTagContext(ScriptContainer container) {
        return new FabricTagContext(Denizen::getDebugTarget).script(new ScriptTag(Objects.requireNonNull(container)));
    }

    @Override
    public TagContext getTagContext(ScriptEntry entry) {
        return entry.entryData.getTagContext().clone();
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
    
    public static String[] DEBUG_FRIENDLY_COLORS = new String[] {
            AnsiChatFormatting.AQUA, AnsiChatFormatting.BLUE, AnsiChatFormatting.DARK_AQUA, AnsiChatFormatting.DARK_BLUE,
            AnsiChatFormatting.DARK_GREEN, AnsiChatFormatting.DARK_PURPLE, AnsiChatFormatting.GOLD, AnsiChatFormatting.GRAY,
            AnsiChatFormatting.GREEN, AnsiChatFormatting.LIGHT_PURPLE, AnsiChatFormatting.WHITE, AnsiChatFormatting.YELLOW
    };

    @Override
    public String getRandomColor() {
        return DEBUG_FRIENDLY_COLORS[CoreUtilities.getRandom().nextInt(DEBUG_FRIENDLY_COLORS.length)];
    }

    @Override
    public boolean canReadFile(File file) {
        return false;
    }

    @Override
    public String queueHeaderInfo(ScriptEntry entry) {
        return " " + entry.entryData.toString();
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
    public String applyDebugColors(String uncolored) {
        if (!CoreUtilities.contains(uncolored, '<')) {
            return uncolored;
        }
        return uncolored
                .replace("<Y>", AnsiChatFormatting.YELLOW)
                .replace("<O>", AnsiChatFormatting.GOLD)
                .replace("<G>", AnsiChatFormatting.DARK_GRAY)
                .replace("<LG>", AnsiChatFormatting.GRAY)
                .replace("<GR>", AnsiChatFormatting.GREEN)
                .replace("<A>", AnsiChatFormatting.AQUA)
                .replace("<R>", AnsiChatFormatting.DARK_RED)
                .replace("<LR>", AnsiChatFormatting.RED)
                .replace("<LP>", AnsiChatFormatting.LIGHT_PURPLE)
                .replace("<W>", AnsiChatFormatting.WHITE);
    }

    @Override
    public String cleanseLogString(String str) {
        if (!CoreUtilities.contains(str, AnsiChatFormatting.ESC_CHAR)) {
            return str;
        }
        str = str
                .replace(AnsiChatFormatting.BLACK, ChatFormatting.BLACK.toString())
                .replace(AnsiChatFormatting.DARK_BLUE, ChatFormatting.DARK_BLUE.toString())
                .replace(AnsiChatFormatting.DARK_GREEN, ChatFormatting.DARK_GREEN.toString())
                .replace(AnsiChatFormatting.DARK_AQUA, ChatFormatting.DARK_AQUA.toString())
                .replace(AnsiChatFormatting.DARK_RED, ChatFormatting.DARK_RED.toString())
                .replace(AnsiChatFormatting.DARK_PURPLE, ChatFormatting.DARK_PURPLE.toString())
                .replace(AnsiChatFormatting.GOLD, ChatFormatting.GOLD.toString())
                .replace(AnsiChatFormatting.GRAY, ChatFormatting.GRAY.toString())
                .replace(AnsiChatFormatting.DARK_GRAY, ChatFormatting.DARK_GRAY.toString())
                .replace(AnsiChatFormatting.BLUE, ChatFormatting.BLUE.toString())
                .replace(AnsiChatFormatting.GREEN, ChatFormatting.GREEN.toString())
                .replace(AnsiChatFormatting.AQUA, ChatFormatting.AQUA.toString())
                .replace(AnsiChatFormatting.RED, ChatFormatting.RED.toString())
                .replace(AnsiChatFormatting.LIGHT_PURPLE, ChatFormatting.LIGHT_PURPLE.toString())
                .replace(AnsiChatFormatting.YELLOW, ChatFormatting.YELLOW.toString())
                .replace(AnsiChatFormatting.WHITE, ChatFormatting.WHITE.toString())
                .replace(AnsiChatFormatting.OBFUSCATED, ChatFormatting.OBFUSCATED.toString())
                .replace(AnsiChatFormatting.BOLD, ChatFormatting.BOLD.toString())
                .replace(AnsiChatFormatting.STRIKETHROUGH, ChatFormatting.STRIKETHROUGH.toString())
                .replace(AnsiChatFormatting.UNDERLINE, ChatFormatting.UNDERLINE.toString())
                .replace(AnsiChatFormatting.ITALIC, ChatFormatting.ITALIC.toString())
                .replace(AnsiChatFormatting.RESET, ChatFormatting.RESET.toString());
        return AnsiChatFormatting.stripFormatting(str);
    }

    @Override
    public String stripColor(String message) {
        return AnsiChatFormatting.stripFormatting(message);
    }

    @Override
    public void doFinalDebugOutput(String rawText) {
        //                                                 This can change slightly, just do our best here
        //                                                    [HH:mm:ss] [Render thread/INFO] (denizen)
        Denizen.LOGGER.info(rawText.replace("<FORCE_ALIGN>", "                                          "));
    }
    //endregion
}
