package com.morphanone.denizenmod.objects;

import com.morphanone.denizenmod.DenizenMod;
import com.morphanone.denizenmod.tags.annotations.Tag;
import com.morphanone.denizenmod.tags.TagFactories;
import com.morphanone.denizenmod.utilities.RayTrace;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.UUID;

public class PlayerTag extends AbstractEntityTag {
    public Reference<Player> player;

    public PlayerTag(UUID uuid) {
        super(uuid);
        this.player = new WeakReference<>(null);
    }

    public PlayerTag(Player player) {
        super(player.getUUID());
        this.player = new WeakReference<>(player);
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

    @Tag // this is mostly for testing purposes
    public AbstractEntityTag selfTag() {
        return this;
    }
}
