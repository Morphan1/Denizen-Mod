package com.morphanone.denizenmod.objects;

import com.morphanone.denizenmod.DenizenMod;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.UUID;

public class EntityTag extends AbstractEntityTag<Entity> {
    public EntityTag(UUID uuid) {
        super(uuid);
    }

    @Override
    public Optional<Entity> value() {
        return Optional.ofNullable(DenizenMod.instance.findEntity(uuid));
    }
}
