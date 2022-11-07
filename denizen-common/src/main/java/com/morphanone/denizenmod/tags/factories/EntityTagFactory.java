package com.morphanone.denizenmod.tags.factories;

import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.morphanone.denizenmod.objects.EntityTag;
import com.morphanone.denizenmod.objects.EntityTagImpl;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

public abstract class EntityTagFactory<T extends EntityTag, E extends Entity> extends ObjectReferenceTagFactory<T, E> {
    public EntityTagFactory(Class<T> tagClass, Class<E> entityClass) {
        super(tagClass, entityClass);
    }

    @Override
    public T getDefault(TagContext context) {
        return null;
    }

    public T fromIdentity(String input) {
        if (input.length() == 36 && CoreUtilities.contains(input, '-')) {
            try {
                UUID uuid = UUID.fromString(input);
                T byUUID = from(uuid);
                if (byUUID.value().isPresent()) {
                    return byUUID;
                }
            }
            catch (IllegalArgumentException ignored) {
            }
        }
        return null;
    }

    @Override
    public T valueOf(String input, TagContext context) {
        return fromIdentity(input);
    }

    @Override
    public boolean matches(String input) {
        return fromIdentity(input) != null;
    }

    public abstract T from(UUID uuid);

    public static class EntityImpl extends EntityTagFactory<EntityTagImpl, Entity> {
        public EntityImpl() {
            super(EntityTagImpl.class, Entity.class);
        }

        @Override
        public EntityTagImpl from(UUID uuid) {
            return new EntityTagImpl(uuid);
        }

        @Override
        public EntityTagImpl of(Entity entity) {
            return entity != null ? new EntityTagImpl(entity) : null;
        }

        @Override
        public String name() {
            return "entity";
        }

        @Override
        public String objectIdentifier() {
            return "e";
        }
    }
}
