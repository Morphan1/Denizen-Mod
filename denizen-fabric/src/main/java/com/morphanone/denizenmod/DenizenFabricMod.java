package com.morphanone.denizenmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.commands.CommandSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.StreamSupport;

public class DenizenFabricMod extends DenizenFabricBase implements ModInitializer {
    public MinecraftServer server;

    @Override
    public void onInitialize() {
        super.onInitialize();
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            coreBridge.registerCommands(dispatcher);
        }));
        coreBridge.debugLog("Waiting for server to start...");
        coreBridge.setMainThread(null);
        ServerLifecycleEvents.SERVER_STARTING.register((server) -> {
            this.server = server;
            coreBridge.onServerStart();
        });
        ServerLifecycleEvents.SERVER_STOPPING.register((server) -> {
            coreBridge.onServerShutdown();
            this.server = null;
        });
        ServerTickEvents.START_SERVER_TICK.register((server) -> coreBridge.tick(50));
    }

    @Override
    public Player findPlayer(UUID uuid) {
        return server.getPlayerList().getPlayer(uuid);
    }

    @Override
    public Player findPlayerByName(String name) {
        return server.getPlayerList().getPlayerByName(name);
    }

    @Override
    public Entity findEntity(UUID uuid) {
        return StreamSupport.stream(server.getAllLevels().spliterator(), false)
                .map((level) -> level.getEntity(uuid))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Level getLevel(ResourceKey<Level> dimension) {
        return server.getLevel(dimension);
    }

    @Override
    public CommandSource getMainCommandSource() {
        return server;
    }

    @Override
    public String getImplementationName() {
        return "Fabric";
    }
}
