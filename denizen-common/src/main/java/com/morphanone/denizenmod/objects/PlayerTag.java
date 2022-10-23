package com.morphanone.denizenmod.objects;

import com.morphanone.denizenmod.DenizenMod;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;
import java.util.UUID;

public class PlayerTag extends AbstractEntityTag<Player> {
    public PlayerTag(UUID uuid) {
        super(uuid);
    }

    @Override
    public Optional<Player> value() {
        return Optional.ofNullable(DenizenMod.instance.findPlayer(uuid));
    }

    public void sendSystemMessage(String message) {
        value().ifPresent((player) -> player.sendSystemMessage(Component.literal(message)));
    }
}
