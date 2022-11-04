package com.morphanone.denizenmod.objects;

import com.morphanone.denizenmod.DenizenMod;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.UUID;

public class EntityTag extends AbstractEntityTag {
    public Entity entity;

    public EntityTag(UUID uuid) {
        super(uuid);
    }

    public EntityTag(Entity entity) {
        this(entity.getUUID());
        this.entity = entity;
    }

    @Override
    public Optional<Entity> value() {
        if (entity == null) {
            entity = DenizenMod.instance.findEntity(uuid);
        }
        return Optional.ofNullable(entity);
    }
}
