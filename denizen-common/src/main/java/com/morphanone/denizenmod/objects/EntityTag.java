package com.morphanone.denizenmod.objects;

import com.morphanone.denizenmod.DenizenMod;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.UUID;

public class EntityTag extends AbstractEntityTag {
    public Entity handle;

    public EntityTag(UUID uuid) {
        super(uuid);
    }

    public EntityTag(Entity entity) {
        this(entity.getUUID());
        this.handle = entity;
    }

    @Override
    public Optional<Entity> value() {
        if (handle == null) {
            handle = DenizenMod.instance.findEntity(uuid);
        }
        return Optional.ofNullable(handle);
    }
}
