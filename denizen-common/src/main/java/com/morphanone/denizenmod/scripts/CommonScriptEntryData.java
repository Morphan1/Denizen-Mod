package com.morphanone.denizenmod.scripts;

import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.morphanone.denizenmod.tags.CommonTagContext;

import java.util.Objects;

public class CommonScriptEntryData extends ScriptEntryData {
    public CommonTagContext context;

    public CommonScriptEntryData(CommonTagContext context) {
        this.context = Objects.requireNonNull(context);
    }

    @Override
    public void transferDataFrom(ScriptEntryData scriptEntryData) {
        if (scriptEntryData == null) {
            return;
        }
        CommonScriptEntryData fabricEntryData = (CommonScriptEntryData) scriptEntryData;
        this.scriptEntry = fabricEntryData.scriptEntry;
        this.context = ((CommonTagContext) fabricEntryData.context.clone()).scriptEntry(scriptEntry);
    }

    @Override
    public TagContext getTagContext() {
        return context.scriptEntry(scriptEntry);
    }

    @Override
    public YamlConfiguration save() {
        return null;
    }

    @Override
    public void load(YamlConfiguration yamlConfiguration) {
    }

    @Override
    public String toString() {
        return context.toString();
    }
}
