package com.morphanone.denizenmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.commands.CommandSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

public class DenizenFabricClientMod extends DenizenFabricBase implements ClientModInitializer {
    public static DenizenFabricClientMod instance;

    public DenizenFabricClientMod() {
        instance = this;
    }

    @Override
    public void onInitializeClient() {
        super.onInitialize();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            coreBridge.registerClientCommands(dispatcher, () -> Minecraft.getInstance().player);
        });
        coreBridge.debugLog("Denizen clientside ready to go");
        ClientTickEvents.START_CLIENT_TICK.register((client) -> {
            coreBridge.tick(50);
        });
    }

    @Override
    public Player findPlayer(UUID uuid) {
        return findEntity(uuid) instanceof Player player ? player : null;
    }

    @Override
    public Player findPlayerByName(String name) {
        return currentLevel().map(ClientLevel::entitiesForRendering)
                .flatMap((entities) -> StreamSupport.stream(entities.spliterator(), false)
                        .filter(Player.class::isInstance)
                        .map(Player.class::cast)
                        .filter((player) -> name.equals(player.getName().getString()))
                        .findFirst())
                .orElse(null);
    }

    public Optional<ClientLevel> currentLevel() {
        return Optional.ofNullable(Minecraft.getInstance().level);
    }

    @Override
    public Entity findEntity(UUID uuid) {
        return currentLevel().map(ClientLevel::entitiesForRendering)
                .flatMap((entities) -> StreamSupport.stream(entities.spliterator(), false)
                        .filter((entity) -> uuid.equals(entity.getUUID()))
                        .findFirst())
                .orElse(null);
    }

    @Override
    public Level getLevel(ResourceKey<Level> dimension) {
        return currentLevel().filter((level) -> level.dimension().equals(dimension)).orElse(null);
    }

    @Override
    public CommandSource getMainCommandSource() {
        return Minecraft.getInstance().player;
    }

    @Override
    public String getImplementationName() {
        return "Fabric-Client";
    }
}
