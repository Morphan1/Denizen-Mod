package com.morphanone.denizenmod;

import com.mojang.brigadier.CommandDispatcher;
import com.morphanone.denizenmod.config.DenizenModConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.player.Player;

import java.util.function.Supplier;

public interface DenizenCoreBridge {
    void debugLog(String message);

    void setMainThread(Thread thread);

    void init(DenizenModImplementation implementation);

    DenizenModConfig config();

    void tick(int deltaMilliseconds);

    void shutdown();

    void reloadSaves();

    void clearQueued();

    void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher);

    <T> void registerClientCommands(CommandDispatcher<T> dispatcher, Supplier<Player> clientPlayer);

    void onServerStart();

    void onServerShutdown();

    void registerTagExtension(String objectTag, String extension);

    void registerTagFactory(String factory);

    String cleanseLogString(String str);
}
