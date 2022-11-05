package com.morphanone.denizenmod.objects;

import com.morphanone.denizenmod.DenizenMod;
import com.morphanone.denizenmod.tags.TagFactories;
import com.morphanone.denizenmod.utilities.RayTrace;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.UUID;

public class PlayerTag extends EntityTag {
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
        Player player = this.player.get();
        if (player == null) {
            player = DenizenMod.instance.findPlayer(uuid);
            if (player != null) {
                this.player = new WeakReference<>(player);
            }
        }
        return Optional.ofNullable(player);
    }

    public void sendSystemMessage(String message) {
        value().ifPresent((player) -> player.sendSystemMessage(Component.literal(message)));
    }

    /**
     * {@return the entity the player is currently looking at}
     */
    @Override
    public EntityTag targetTag() {
        return value().map((player) ->
                RayTrace.any(player.level, player.getEyePosition(), player.getLookAngle(), 50.0, 0.0, true, ClipContext.Fluid.NONE, null).entity
        ).map(TagFactories.ENTITY_ANY::of).orElse(null);
    }
}
