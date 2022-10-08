package com.morphanone.denizenfabric.scripts;

import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.morphanone.denizenfabric.tags.FabricTagContext;

import java.util.Objects;

public class FabricScriptEntryData extends ScriptEntryData {
    public FabricTagContext context;

    public FabricScriptEntryData(FabricTagContext context) {
        this.context = Objects.requireNonNull(context);
    }

    @Override
    public void transferDataFrom(ScriptEntryData scriptEntryData) {
        if (scriptEntryData == null) {
            return;
        }
        FabricScriptEntryData fabricEntryData = (FabricScriptEntryData) scriptEntryData;
        this.scriptEntry = fabricEntryData.scriptEntry;
        this.context = ((FabricTagContext) fabricEntryData.context.clone()).scriptEntry(scriptEntry);
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
