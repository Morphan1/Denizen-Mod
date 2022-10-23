package com.morphanone.denizenmod.objects;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.UUID;

public abstract class AbstractEntityTag<T extends Entity> extends AbstractObjectTag implements ObjectReferenceTag<T> {
    public UUID uuid;

    public AbstractEntityTag(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String rawSimpleIdentity() {
        return uuid.toString();
    }

    public Optional<Component> getName() {
        return value().map(Entity::getName);
    }

    public Optional<String> getNameString() {
        return getName().map(Component::getString);
    }

    public String getRawNameString() {
        return getNameString().orElse(null);
    }
}
