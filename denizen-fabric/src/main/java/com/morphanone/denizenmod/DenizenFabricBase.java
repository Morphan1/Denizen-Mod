package com.morphanone.denizenmod;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public abstract class DenizenFabricBase implements DenizenModImplementation {
    public final Logger logger = LoggerFactory.getLogger(DenizenMod.MOD_ID);

    public ModContainer container;

    public DenizenCoreBridge coreBridge;

    public void onInitialize() {
        container = FabricLoader.getInstance().getModContainer(DenizenMod.MOD_ID).orElseThrow(() -> new IllegalStateException("Failed to find Denizen mod"));
        coreBridge = DenizenMod.newCore(this);
    }

    @Override
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir().resolve(DenizenMod.MOD_ID);
    }

    @Override
    public String getImplementationVersion() {
        return container.getMetadata().getVersion().getFriendlyString();
    }

    @Override
    public void doFinalDebugOutput(String rawText) {
        //                                         This can change slightly, just do our best here
        //                                            [HH:mm:ss] [Render thread/INFO] (denizen)
        logger.info(rawText.replace("<FORCE_ALIGN>", "                                          "));
    }
}
