package com.morphanone.denizenfabric.objects;

import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.tags.Attribute;
import com.morphanone.denizenfabric.Denizen;
import com.morphanone.denizenfabric.tags.TagFactories;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;
import java.util.UUID;

public class PlayerTag extends ObjectReferenceTag<Player> {
    public UUID uuid;

    public PlayerTag(UUID uuid) {
        this.uuid = uuid;
    }

    private String prefix = "Player";

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public boolean isUnique() {
        return false;
    }

    @Override
    public String identify() {
        return "p@" + uuid.toString();
    }

    @Override
    public String identifySimple() {
        return identify();
    }

    @Override
    public String toString() {
        return identify();
    }

    @Override
    public ObjectTag setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    @Override
    public ObjectTag getObjectAttribute(Attribute attribute) {
        return TagFactories.PLAYER.tagProcessor.getObjectAttribute(this, attribute);
    }

    @Override
    public Optional<Player> value() {
        return Optional.ofNullable(Denizen.instance.findPlayer(uuid));
    }

    public Optional<Component> getName() {
        return value().map(Player::getName);
    }

    public Optional<String> getNameString() {
        return getName().map(Component::getString);
    }

    public String getRawNameString() {
        return getNameString().orElse(null);
    }

    public void sendSystemMessage(String message) {
        value().ifPresent((player) -> player.sendSystemMessage(Component.literal(message)));
    }
}
