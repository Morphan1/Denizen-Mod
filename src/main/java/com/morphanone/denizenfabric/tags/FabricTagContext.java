package com.morphanone.denizenfabric.tags;

import com.denizenscript.denizencore.objects.core.ScriptTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.denizenscript.denizencore.tags.TagContext;
import com.morphanone.denizenfabric.objects.PlayerTag;
import com.morphanone.denizenfabric.scripts.FabricScriptEntryData;
import net.minecraft.commands.CommandSource;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class FabricTagContext extends TagContext {
    public PlayerTag player;

    public Supplier<CommandSource> debugTarget;

    public boolean isDebugManual;

    public FabricTagContext(Supplier<CommandSource> debugTarget) {
        super(null);
        this.debugTarget = Objects.requireNonNull(debugTarget);
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new FabricScriptEntryData(this);
    }

    private static <T> String format(T object, Function<T, String> getIdentifier) {
        if (object != null) {
            String identifier = getIdentifier.apply(object);
            if (identifier != null) {
                return "'" + identifier + "'";
            }
        }
        return "null";
    }

    @Override
    public String toString() {
        return '[' +
                "player=" + format(player, PlayerTag::getRawNameString) +
                ']';
    }

    private void setShouldDebug(boolean debug) {
        if (!isDebugManual) {
            this.debug = debug;
        }
    }

    public FabricTagContext player(PlayerTag player) {
        this.player = player;
        return this;
    }

    public FabricTagContext scriptEntry(ScriptEntry entry) {
        if (entry != null && entry != this.entry) {
            this.script = entry.getScript();
            setShouldDebug(entry.shouldDebug());
            if (entry.queue != null) {
                this.definitionProvider = entry.queue;
                this.contextSource = entry.queue.contextSource;
            }
        }
        this.entry = entry;
        return this;
    }

    public FabricTagContext script(ScriptTag script) {
        this.script = script;
        setShouldDebug(script == null || script.getContainer().shouldDebug());
        return this;
    }

    public FabricTagContext debug(boolean debug) {
        this.debug = debug;
        this.isDebugManual = true;
        return this;
    }

    public FabricTagContext showErrors(ShowErrorsMethod showErrors) {
        this.showErrors = showErrors;
        return this;
    }
}
