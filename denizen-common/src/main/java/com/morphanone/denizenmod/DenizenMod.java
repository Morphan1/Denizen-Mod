package com.morphanone.denizenmod;

import com.denizenscript.denizencore.DenizenCore;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.morphanone.denizenmod.commands.Commands;
import com.morphanone.denizenmod.tags.TagFactories;

import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public final class DenizenMod {
    public static DenizenModImplementation instance;

    public static YamlConfiguration config;

    public static void init(DenizenModImplementation instance) {
        DenizenMod.instance = instance;
        DenizenMod.config = instance.loadConfig();
        if (CommonSettings.enforceLocale()) {
            Locale.setDefault(Locale.US);
        }
        initCore();
    }

    private static void initCore() {
        CoreUtilities.basicContext = instance.createBasicContext();
        CoreUtilities.errorButNoDebugContext = instance.createErrorNoDebugContext();
        CoreUtilities.noDebugContext = instance.createNoDebugContext();
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
}
