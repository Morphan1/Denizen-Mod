package com.morphanone.denizenmod;

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
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.denizenscript.denizencore.utilities.debugging.StrongWarning;
import com.morphanone.denizenmod.scripts.CommonScriptEntryData;
import com.morphanone.denizenmod.tags.CommonTagContext;
import com.morphanone.denizenmod.tags.TagFactories;
import com.morphanone.denizenmod.utilities.AnsiChatFormatting;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.io.File;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public interface DenizenModImplementation extends DenizenImplementation {
    YamlConfiguration loadConfig();

    Player findPlayer(UUID uuid);

    Player findPlayerByName(String name);

    Entity findEntity(UUID uuid);

    Level getLevel(ResourceKey<Level> dimension);

    CommandSource getMainCommandSource();

    default TagContext createBasicContext() {
        return new CommonTagContext();
    }

    default TagContext createErrorNoDebugContext() {
        return new CommonTagContext().debug(false);
    }

    default TagContext createNoDebugContext() {
        return new CommonTagContext().debug(false).showErrors(() -> false);
    }

    //region Core Implementation
    @Override
    default TagContext getTagContext(ScriptContainer container) {
        return new CommonTagContext().script(new ScriptTag(Objects.requireNonNull(container)));
    }

    @Override
    default ScriptEntryData getEmptyScriptEntryData() {
        return new CommonScriptEntryData(new CommonTagContext());
    }

    @Override
    default boolean needsHandleArgPrefix(String prefix) {
        return prefix.equals("player");
    }

    StrongWarning INVALID_PLAYER_ARG = new StrongWarning("invalidPlayerArg", "The 'player:' arg should not be used in commands like define/flag/yaml/... input the player directly instead.");
    Set<String> INVALID_PLAYER_ARG_COMMANDS = Set.of("DEFINE", "FLAG", "YAML");

    @Override
    default boolean handleCustomArgs(ScriptEntry entry, Argument arg) {
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
    default TagContext getTagContext(ScriptEntry entry) {
        return entry.entryData.getTagContext().clone();
    }

    String[] DEBUG_FRIENDLY_COLORS = new String[] {
            AnsiChatFormatting.AQUA, AnsiChatFormatting.BLUE, AnsiChatFormatting.DARK_AQUA, AnsiChatFormatting.DARK_BLUE,
            AnsiChatFormatting.DARK_GREEN, AnsiChatFormatting.DARK_PURPLE, AnsiChatFormatting.GOLD, AnsiChatFormatting.GRAY,
            AnsiChatFormatting.GREEN, AnsiChatFormatting.LIGHT_PURPLE, AnsiChatFormatting.WHITE, AnsiChatFormatting.YELLOW
    };

    @Override
    default String getRandomColor() {
        return DEBUG_FRIENDLY_COLORS[CoreUtilities.getRandom().nextInt(DEBUG_FRIENDLY_COLORS.length)];
    }

    @Override
    default String queueHeaderInfo(ScriptEntry entry) {
        return " " + entry.entryData.toString();
    }

    @Override
    default String applyDebugColors(String uncolored) {
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
    default String cleanseLogString(String str) {
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
    default String stripColor(String message) {
        return AnsiChatFormatting.stripFormatting(message);
    }

    @Override
    default void preScriptReload() {
    }

    @Override
    default void onScriptReload() {
    }

    @Override
    default void refreshScriptContainers() {
    }

    @Override
    default void preTagExecute() {
    }

    @Override
    default void postTagExecute() {
    }

    @Override
    default boolean canWriteToFile(File file) {
        return false;
    }

    @Override
    default boolean canReadFile(File file) {
        return false;
    }

    @Override
    default void addExtraErrorHeaders(StringBuilder headerBuilder, ScriptEntry source) {
    }

    @Override
    default FlaggableObject simpleWordToFlaggable(String word, ScriptEntry entry) {
        return null;
    }

    @Override
    default ObjectTag getSpecialDef(String def, ScriptQueue queue) {
        return null;
    }

    @Override
    default boolean setSpecialDef(String def, ScriptQueue queue, ObjectTag value) {
        return false;
    }
    //endregion
}
