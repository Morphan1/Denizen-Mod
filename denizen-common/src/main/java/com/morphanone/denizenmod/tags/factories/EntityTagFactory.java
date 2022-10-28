package com.morphanone.denizenmod.tags.factories;

import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.tags.TagRunnable;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.morphanone.denizenmod.objects.AnyEntityTag;
import com.morphanone.denizenmod.objects.EntityTag;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public abstract class EntityTagFactory<T extends AnyEntityTag, E extends Entity> extends ObjectReferenceTagFactory<T, E> {
    public EntityTagFactory(Class<T> tagClass, Class<E> entityClass) {
        super(tagClass, entityClass);
    }

    public <R extends ObjectTag> void register(String name, Class<R> returnType, TagRunnable.ObjectInterface<T, R> runnable) {
        tagProcessor.registerTag(returnType, name, runnable);
    }

    @Override
    public void registerTags() {
        super.registerTags();
        /*register("name", ElementTag.class, (attribute, entity) -> entity.value().map(Entity::getName).map(Component::getString).map(ElementTag::new).orElse(null));
        register("target", AnyEntityTag.class, (attribute, entity) -> entity.value().map((handle) -> {
            if (handle instanceof Mob mob) {
                return mob.getTarget();
            }
            return null;
        }).map(TagFactories.ENTITY_ANY::of).orElse(null));*/
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
                if (byUUID != null && byUUID.value().isPresent()) {
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

    public static class Any extends EntityTagFactory<AnyEntityTag, Entity> {
        public static final List<EntityTagFactory<? extends AnyEntityTag, ? extends Entity>> FACTORIES = new ArrayList<>();

        public static void registerFactory(EntityTagFactory<?, ?> tagFactory) {
            FACTORIES.add(0, tagFactory);
        }

        public Any() {
            super(AnyEntityTag.class, Entity.class);
        }

        @Override
        public String name() {
            return "entity";
        }

        @Override
        public String objectIdentifier() {
            return null;
        }

        @Override
        public AnyEntityTag from(UUID uuid) {
            return new EntityTag(uuid).value().map(this::of).orElse(null);
        }

        @Override
        public AnyEntityTag of(Entity entity) {
            if (entity == null) {
                return null;
            }
            return FACTORIES.stream().map((factory) -> (AnyEntityTag) factory.tryOf(entity))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElseThrow();
        }
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
        public EntityTag of(Entity entity) {
            return new EntityTag(entity);
        }

        @Override
        public String name() {
            return null;
        }

        @Override
        public String objectIdentifier() {
            return "e";
        }
    }
}
