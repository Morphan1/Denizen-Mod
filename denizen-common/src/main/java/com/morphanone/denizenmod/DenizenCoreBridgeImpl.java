package com.morphanone.denizenmod;

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
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.denizenscript.denizencore.utilities.debugging.DebugInternals;
import com.denizenscript.denizencore.utilities.debugging.StrongWarning;
import com.mojang.brigadier.CommandDispatcher;
import com.morphanone.denizenmod.commands.Commands;
import com.morphanone.denizenmod.config.YamlDenizenModConfig;
import com.morphanone.denizenmod.minecraft.commands.ExCommand;
import com.morphanone.denizenmod.minecraft.commands.ExsCommand;
import com.morphanone.denizenmod.scripts.CommonScriptEntryData;
import com.morphanone.denizenmod.tags.CommonTagContext;
import com.morphanone.denizenmod.tags.TagFactories;
import com.morphanone.denizenmod.tags.factories.ObjectTagFactory;
import com.morphanone.denizenmod.utilities.AnsiChatFormatting;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public class DenizenCoreBridgeImpl implements DenizenCoreBridge, DenizenImplementation {
    public String instanceCaller;

    public YamlDenizenModConfig config;

    @Override
    public void debugLog(String message) {
        Debug.log(instanceCaller, message);
    }

    @Override
    public void setMainThread(Thread thread) {
        DenizenCore.MAIN_THREAD = thread;
    }

    @Override
    public void init(DenizenModImplementation implementation) {
        DenizenMod.instance = implementation;
        DenizenMod.coreBridge = this;
        this.instanceCaller = DebugInternals.getClassNameOpti(DenizenMod.instance.getClass());
        if (instanceCaller.endsWith("Mod")) {
            instanceCaller = instanceCaller.substring(0, instanceCaller.length() - "Mod".length());
        }
        this.config = new YamlDenizenModConfig();
        this.config.loadConfig();
        if (CommonSettings.enforceLocale()) {
            Locale.setDefault(Locale.US);
        }
        CoreUtilities.basicContext = createBasicContext();
        CoreUtilities.errorButNoDebugContext = createErrorNoDebugContext();
        CoreUtilities.noDebugContext = createNoDebugContext();
        DenizenCore.init(this);
        bootstrapRegistries();
    }

    @Override
    public YamlDenizenModConfig config() {
        return config;
    }

    @Override
    public void tick(int deltaMilliseconds) {
        DenizenCore.tick(deltaMilliseconds);
    }

    @Override
    public void shutdown() {
        DenizenCore.shutdown();
    }

    @Override
    public void reloadSaves() {
        DenizenCore.reloadSaves();
    }

    @Override
    public void clearQueued() {
        ExCommandHelper.sustainedQueues.clear();
        DenizenCore.scheduled.clear();
    }

    @Override
    public void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        ExCommand.register(dispatcher);
        ExsCommand.register(dispatcher);
    }

    @Override
    public <T> void registerClientCommands(CommandDispatcher<T> dispatcher, Supplier<Player> clientPlayer) {
        dispatcher.register(ExCommand.generate("cex", (source) -> new CommonTagContext().player(TagFactories.PLAYER.of(clientPlayer.get()))));
        dispatcher.register(ExsCommand.generate("cexs", (source) -> new CommonTagContext().player(TagFactories.PLAYER.of(clientPlayer.get()))));
    }

    public void onServerStart() {
        clearQueued();
        setMainThread(Thread.currentThread());
        debugLog("Server started! Loading saved data and allowing commands...");
        reloadSaves();
    }

    public void onServerShutdown() {
        debugLog("Server stopping! Saving data and disabling commands...");
        shutdown();
        setMainThread(null);
        clearQueued();
    }

    @Override
    public void registerTagExtension(String objectTag, String extension) {
        try {
            ObjectTagFactory.registerTags(TagFactories.BY_OBJECT_TYPE.get(Class.forName(objectTag)), Class.forName(extension).getMethods());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void registerTagFactory(String factory) {
        try {
            TagFactories.registerTagFactory((ObjectTagFactory<?>) Class.forName(factory).getDeclaredConstructor().newInstance());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    public TagContext createBasicContext() {
        return new CommonTagContext();
    }

    public TagContext createErrorNoDebugContext() {
        return new CommonTagContext().debug(false);
    }

    public TagContext createNoDebugContext() {
        return new CommonTagContext().debug(false).showErrors(() -> false);
    }

    //region Core Implementation
    @Override
    public TagContext getTagContext(ScriptContainer container) {
        return new CommonTagContext().script(new ScriptTag(Objects.requireNonNull(container)));
    }

    @Override
    public ScriptEntryData getEmptyScriptEntryData() {
        return new CommonScriptEntryData(new CommonTagContext());
    }

    @Override
    public boolean needsHandleArgPrefix(String prefix) {
        return prefix.equals("player");
    }

    StrongWarning INVALID_PLAYER_ARG = new StrongWarning("invalidPlayerArg", "The 'player:' arg should not be used in commands like define/flag/yaml/... input the player directly instead.");
    Set<String> INVALID_PLAYER_ARG_COMMANDS = Set.of("DEFINE", "FLAG", "YAML");

    @Override
    public boolean handleCustomArgs(ScriptEntry entry, Argument arg) {
        if (arg.matchesPrefix("player")) {
            if (INVALID_PLAYER_ARG_COMMANDS.contains(entry.getCommandName())) {
                INVALID_PLAYER_ARG.warn(entry);
            }
            Debug.echoDebug(entry, "Setting this queue's current player to: " + arg.getValue());
            String value = TagManager.tag(arg.getValue(), entry.getContext());
            ((CommonTagContext) entry.getContext()).player(TagFactories.PLAYER.valueOf(value, entry.getContext()));
            return true;
        }
        return false;
    }

    @Override
    public TagContext getTagContext(ScriptEntry entry) {
        return entry.entryData.getTagContext().clone();
    }

    String[] DEBUG_FRIENDLY_COLORS = new String[] {
            AnsiChatFormatting.AQUA, AnsiChatFormatting.BLUE, AnsiChatFormatting.DARK_AQUA, AnsiChatFormatting.DARK_BLUE,
            AnsiChatFormatting.DARK_GREEN, AnsiChatFormatting.DARK_PURPLE, AnsiChatFormatting.GOLD, AnsiChatFormatting.GRAY,
            AnsiChatFormatting.GREEN, AnsiChatFormatting.LIGHT_PURPLE, AnsiChatFormatting.WHITE, AnsiChatFormatting.YELLOW
    };

    @Override
    public String getRandomColor() {
        return DEBUG_FRIENDLY_COLORS[CoreUtilities.getRandom().nextInt(DEBUG_FRIENDLY_COLORS.length)];
    }

    @Override
    public String queueHeaderInfo(ScriptEntry entry) {
        return " " + entry.entryData.toString();
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
    public void doFinalDebugOutput(String rawText) {
        DenizenMod.instance.doFinalDebugOutput(rawText);
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
    public File getScriptFolder() {
        return config.scriptsDir.toFile();
    }

    @Override
    public File getDataFolder() {
        return config.dataDir.toFile();
    }

    @Override
    public String getImplementationVersion() {
        return DenizenMod.instance.getImplementationVersion();
    }

    @Override
    public String getImplementationName() {
        return DenizenMod.instance.getImplementationName();
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
    //endregion
}
