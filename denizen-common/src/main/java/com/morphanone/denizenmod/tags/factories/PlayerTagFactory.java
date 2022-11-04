package com.morphanone.denizenmod.tags.factories;

import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.denizenscript.denizencore.utilities.debugging.StrongWarning;
import com.denizenscript.denizencore.utilities.debugging.Warning;
import com.morphanone.denizenmod.DenizenMod;
import com.morphanone.denizenmod.objects.PlayerTag;
import com.morphanone.denizenmod.utilities.Context;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class PlayerTagFactory extends EntityTagFactory<PlayerTag, Player> {
    public PlayerTagFactory() {
        super(PlayerTag.class, Player.class);
    }

    @Override
    public String name() {
        return "player";
    }

    @Override
    public String objectIdentifier() {
        return "p";
    }

    @Override
    public PlayerTag getDefault(TagContext context) {
        return Context.getPlayer(context).orElseGet(() -> {
            Debug.echoError(context, "No player attached to current context! <player> returning null.");
            return null;
        });
    }

    public static final Warning ILLEGAL_PLAYER_BY_NAME = new StrongWarning("playerByName", "Players should not be referenced by name - use their UUID instead!");

    @Override
    public PlayerTag valueOf(String input, TagContext context) {
        PlayerTag value = super.valueOf(input, context);
        if (value != null) {
            return value;
        }
        Player byName = DenizenMod.instance.findPlayerByName(input);
        if (byName != null) {
            if (context.script != null) {
                ILLEGAL_PLAYER_BY_NAME.warn(context);
            }
            return from(byName.getUUID());
        }
        return null;
    }

    @Override
    public PlayerTag from(UUID uuid) {
        return new PlayerTag(uuid);
    }

    @Override
    public PlayerTag of(Player player) {
        return player != null ? new PlayerTag(player) : null;
    }
}
