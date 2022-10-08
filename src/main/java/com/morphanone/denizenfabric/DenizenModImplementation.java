package com.morphanone.denizenfabric;

import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public interface DenizenModImplementation {
    Player findPlayer(UUID uuid);

    Player findPlayerByName(String name);
}
