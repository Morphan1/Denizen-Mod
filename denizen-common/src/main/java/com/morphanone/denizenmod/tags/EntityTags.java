package com.morphanone.denizenmod.tags;

import com.morphanone.denizenmod.objects.AbstractEntityTag;
import com.morphanone.denizenmod.tags.factories.EntityTagFactory;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class EntityTags {
    public static final List<EntityTagFactory<?, ?>> FACTORIES = new ArrayList<>();

    public static void register(EntityTagFactory<?, ?> tagFactory) {
        FACTORIES.add(0, tagFactory);
    }

    public static AbstractEntityTag<?> bestOf(Entity entity) {
        for (EntityTagFactory<?, ?> factory : FACTORIES) {
            AbstractEntityTag<?> of = factory.tryOf(entity);
            if (of != null) {
                return of;
            }
        }
        return null;
    }
}
