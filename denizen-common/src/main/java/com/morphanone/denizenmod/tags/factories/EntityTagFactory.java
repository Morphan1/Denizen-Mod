package com.morphanone.denizenmod.tags.factories;

import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.tags.TagRunnable;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.morphanone.denizenmod.objects.AbstractEntityTag;
import com.morphanone.denizenmod.objects.EntityTag;
import com.morphanone.denizenmod.tags.EntityTags;
import com.morphanone.denizenmod.tags.TagFactories;
import com.morphanone.denizenmod.utilities.RayTrace;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public abstract class EntityTagFactory<T extends AbstractEntityTag<E>, E extends Entity> extends ObjectReferenceTagFactory<T, E> {
    public Class<E> entityClass;

    public EntityTagFactory(Class<T> tagClass, Class<E> entityClass) {
        super(tagClass);
        this.entityClass = entityClass;
    }

    @Override
    public String name() {
        return "entity";
    }

    @Override
    public String objectIdentifier() {
        return "e";
    }

    @Override
    public String defaultArgPrefix() {
        return "Entity";
    }

    public <R extends ObjectTag> void register(String name, Class<R> returnType, TagRunnable.ObjectInterface<T, R> runnable) {
        tagProcessor.registerTag(returnType, name, runnable);
    }

    @Override
    public void registerTags() {
        register("name", ElementTag.class, (attribute, entity) -> entity.value().map(Entity::getName).map(Component::getString).map(ElementTag::new).orElse(null));
        register("target", AbstractEntityTag.class, (attribute, entity) -> entity.value().map((handle) -> {
            if (handle instanceof Mob mob) {
                return mob.getTarget();
            }
            else if (handle instanceof Player player) {
                return RayTrace.entities(player.level, player.getEyePosition(), player.getLookAngle(), 50.0, 0.0, null).entity;
            }
            return null;
        }).map(EntityTags::bestOf).orElse(null));
    }

    @Override
    public T getDefault(TagContext context) {
        return null;
    }

    private final String fullIdentifier = objectIdentifier() + "@";

    public T fromIdentity(String input) {
        if (input == null) {
            return null;
        }
        input = CoreUtilities.toLowerCase(input);
        if (input.startsWith(fullIdentifier)) {
            input = input.substring(fullIdentifier.length());
        }
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

    @SuppressWarnings("unchecked")
    public T tryOf(Entity entity) {
        if (entityClass.isInstance(entity)) {
            return of((E) entity);
        }
        return null;
    }

    public static class Base extends EntityTagFactory<EntityTag, Entity> {
        public Base() {
            super(EntityTag.class, Entity.class);
        }

        @Override
        public EntityTag from(UUID uuid) {
            return new EntityTag(uuid);
        }

        @Override
        public EntityTag of(Entity obj) {
            return from(obj.getUUID());
        }
    }
}
