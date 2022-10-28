package com.morphanone.denizenmod.objects;

import net.minecraft.world.entity.Entity;

import java.util.UUID;

public abstract class AbstractEntityTag<T extends Entity> extends AbstractObjectTag implements AnyEntityTag, ObjectReferenceTag<T> {
    public UUID uuid;

    public AbstractEntityTag(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String rawSimpleIdentity() {
        return uuid.toString();
    }
}
