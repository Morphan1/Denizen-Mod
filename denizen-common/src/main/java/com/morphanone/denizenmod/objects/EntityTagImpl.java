package com.morphanone.denizenmod.objects;

import com.morphanone.denizenmod.DenizenMod;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.UUID;

public class EntityTagImpl extends EntityTag {
    public Entity entity;

    public EntityTagImpl(UUID uuid) {
        super(uuid);
    }

    public EntityTagImpl(Entity entity) {
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
