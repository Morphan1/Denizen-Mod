package com.morphanone.denizenmod;

import net.minecraft.commands.CommandSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.nio.file.Path;
import java.util.UUID;

public interface DenizenModImplementation {
    Path getConfigDirectory();

    Player findPlayer(UUID uuid);

    Player findPlayerByName(String name);

    Entity findEntity(UUID uuid);

    Level getLevel(ResourceKey<Level> dimension);

    CommandSource getMainCommandSource();

    String getImplementationVersion();

    String getImplementationName();

    void doFinalDebugOutput(String rawText);
}
