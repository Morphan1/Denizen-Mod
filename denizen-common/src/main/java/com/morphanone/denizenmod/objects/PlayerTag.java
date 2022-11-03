package com.morphanone.denizenmod.objects;

import com.morphanone.denizenmod.DenizenMod;
import com.morphanone.denizenmod.tags.Tag;
import com.morphanone.denizenmod.tags.TagFactories;
import com.morphanone.denizenmod.utilities.RayTrace;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;

import java.util.Optional;
import java.util.UUID;

public class PlayerTag extends AbstractEntityTag {
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

    @Override
    public AbstractEntityTag targetTag() {
        return value().map((player) ->
                RayTrace.any(player.level, player.getEyePosition(), player.getLookAngle(), 50.0, 0.0, true, ClipContext.Fluid.NONE, null).entity
        ).map(TagFactories.ENTITY_ANY::of).orElse(null);
    }

    @Tag("self") // this is mostly for testing purposes
    public AbstractEntityTag selfTag() {
        return this;
    }
}
