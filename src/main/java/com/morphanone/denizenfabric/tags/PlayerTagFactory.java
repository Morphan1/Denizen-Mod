package com.morphanone.denizenfabric.tags;

import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.denizenscript.denizencore.utilities.debugging.StrongWarning;
import com.denizenscript.denizencore.utilities.debugging.Warning;
import com.morphanone.denizenfabric.Denizen;
import com.morphanone.denizenfabric.objects.PlayerTag;
import com.morphanone.denizenfabric.utilities.Context;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class PlayerTagFactory extends ObjectReferenceTagFactory<PlayerTag, Player> {
    public PlayerTagFactory() {
        super(PlayerTag.class);
    }

    @Override
    public String getName() {
        return "player";
    }

    @Override
    public String getObjectIdentifier() {
        return "p";
    }

    @Override
    public void registerTags() {
        tagProcessor.registerTag(ElementTag.class, "name", (attribute, player) -> new ElementTag(player.getRawNameString()));
    }

    @Override
    public PlayerTag getDefault(TagContext context) {
        return Context.getPlayer(context).orElseGet(() -> {
            Debug.echoError(context, "No player attached to current context! <player> returning null.");
            return null;
        });
    }

    public static final Warning ILLEGAL_PLAYER_BY_NAME = new StrongWarning("playerByName", "Players should not be referenced by name - use their UUID instead!");

    public UUID parseIdentity(String input) {
        if (input == null) {
            return null;
        }
        input = CoreUtilities.toLowerCase(input);
        if (input.startsWith("p@")) {
            input = input.substring("p@".length());
        }
        if (input.length() == 36 && CoreUtilities.contains(input, '-')) {
            try {
                UUID uuid = UUID.fromString(input);
                if (Denizen.instance.findPlayer(uuid) != null) {
                    return uuid;
                }
            }
            catch (IllegalArgumentException ignored) {
            }
        }
        return null;
    }

    @Override
    public PlayerTag valueOf(String input, TagContext context) {
        UUID uuid = parseIdentity(input);
        if (uuid != null) {
            return new PlayerTag(uuid);
        }
        Player byName = Denizen.instance.findPlayerByName(input);
        if (byName != null) {
            if (context.script != null) {
                ILLEGAL_PLAYER_BY_NAME.warn(context);
            }
            return new PlayerTag(byName.getUUID());
        }
        return null;
    }

    @Override
    public boolean matches(String input) {
        return parseIdentity(input) != null;
    }

    @Override
    public PlayerTag of(Player player) {
        return player != null ? new PlayerTag(player.getUUID()) : null;
    }
}
