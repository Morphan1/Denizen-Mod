package com.morphanone.denizenmod.tags.factories;

import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.tags.TagRunnable;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.morphanone.denizenmod.objects.AbstractEntityTag;
import com.morphanone.denizenmod.objects.EntityTag;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

public abstract class EntityTagFactory<T extends AbstractEntityTag, E extends Entity> extends ObjectReferenceTagFactory<T, E> {
    public EntityTagFactory(Class<T> tagClass, Class<E> entityClass) {
        super(tagClass, entityClass);
    }

    public <R extends ObjectTag> void register(String name, Class<R> returnType, TagRunnable.ObjectInterface<T, R> runnable) {
        tagProcessor.registerTag(returnType, name, runnable);
    }

    @Override
    public void registerTags() {
        super.registerTags();
    }

    @Override
    public T getDefault(TagContext context) {
        return null;
    }

    @SuppressWarnings("unchecked")
    public E fromIdentity(String input) {
        if (input.length() == 36 && CoreUtilities.contains(input, '-')) {
            try {
                UUID uuid = UUID.fromString(input);
                T byUUID = from(uuid);
                if (byUUID != null) {
                    return (E) byUUID.value().orElse(null);
                }
            }
            catch (IllegalArgumentException ignored) {
            }
        }
        return null;
    }

    @Override
    public T valueOf(String input, TagContext context) {
        return of(fromIdentity(input));
    }

    @Override
    public boolean matches(String input) {
        return fromIdentity(input) != null;
    }

    public abstract T from(UUID uuid);

    public static class Entity extends EntityTagFactory<EntityTag, net.minecraft.world.entity.Entity> {
        public Entity() {
            super(EntityTag.class, net.minecraft.world.entity.Entity.class);
        }

        @Override
        public EntityTag from(UUID uuid) {
            return new EntityTag(uuid);
        }

        @Override
        public EntityTag of(net.minecraft.world.entity.Entity entity) {
            return entity != null ? new EntityTag(entity) : null;
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
