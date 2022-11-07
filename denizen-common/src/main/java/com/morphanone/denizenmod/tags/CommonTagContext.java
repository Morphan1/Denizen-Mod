package com.morphanone.denizenmod.tags;

import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ScriptTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.denizenscript.denizencore.tags.TagContext;
import com.morphanone.denizenmod.objects.PlayerTag;
import com.morphanone.denizenmod.scripts.CommonScriptEntryData;

import java.util.function.Function;

// TODO: builder?
public class CommonTagContext extends TagContext {
    public PlayerTag player;

    public boolean isDebugManual;

    public CommonTagContext() {
        super(null);
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new CommonScriptEntryData(this);
    }

    private static <T extends ObjectTag> String format(T object, Function<T, String> getIdentifier) {
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
                "player=" + player.rawSimpleIdentity() +
                ']';
    }

    private void setShouldDebug(boolean debug) {
        if (!isDebugManual) {
            this.debug = debug;
        }
    }

    public CommonTagContext player(PlayerTag player) {
        this.player = player;
        return this;
    }

    public CommonTagContext scriptEntry(ScriptEntry entry) {
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

    public CommonTagContext script(ScriptTag script) {
        this.script = script;
        setShouldDebug(script == null || script.getContainer().shouldDebug());
        return this;
    }

    public CommonTagContext debug(boolean debug) {
        this.debug = debug;
        this.isDebugManual = true;
        return this;
    }

    public CommonTagContext showErrors(ShowErrorsMethod showErrors) {
        this.showErrors = showErrors;
        return this;
    }
}
