package com.morphanone.denizenmod.objects;

import com.morphanone.denizenmod.tags.TagFactories;
import com.morphanone.denizenmod.tags.annotations.GenerateTag;
import com.morphanone.denizenmod.tags.annotations.OptionalType;
import com.morphanone.denizenmod.tags.annotations.Tag;
import com.morphanone.denizenmod.utilities.OptionalFloat;
import com.morphanone.denizenmod.utilities.RayTrace;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;

public abstract class AbstractEntityTag extends AbstractObjectTag implements ObjectReferenceTag<Entity> {
    public UUID uuid;

    public AbstractEntityTag(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String rawSimpleIdentity() {
        return uuid.toString();
    }

    public Optional<Component> getName() {
        return value().map(Entity::getName);
    }

    public Optional<String> getNameString() {
        return getName().map(Component::getString);
    }

    /**
     * {@return the entity's custom name if set, otherwise its translated type}
     */
    @GenerateTag("name")
    public String getRawNameString() {
        return getNameString().orElse(null);
    }

    /**
     * {@return the distance between the entity's base location to their eyes, or 0 if it has no eyes}
     */
    @GenerateTag
    public OptionalFloat eyeHeight() {
        return value().map((entity) -> OptionalFloat.of(entity.getEyeHeight())).orElse(OptionalFloat.empty());
    }

    /**
     * {@return the current world the entity is in}
     */
    @Tag
    @OptionalType(WorldTag.class)
    public Optional<WorldTag> world() {
        return value().map(Entity::getLevel).map(TagFactories.WORLD::of);
    }

    /**
     * {@return the entity's current attack target, if the entity is a hostile mob}
     */
    @Tag
    public AbstractEntityTag targetTag() {
        return value().map((entity) -> entity instanceof Mob mob ? TagFactories.ENTITY_ANY.of(mob) : null).orElse(null);
    }

    /**
     * {@return the current location the entity is standing at}
     */
    @Tag
    public LocationTag locationTag() {
        return value().map((entity) -> new LocationTag(
                entity.position(),
                new Vec2(entity.getXRot(), entity.getYRot()),
                TagFactories.WORLD.of(entity.getLevel())
        )).orElse(null);
    }

    /**
     * {@return the current location of the entity's eyes, or {@link #locationTag} if it has no eyes}
     */
    @Tag
    public LocationTag eyeLocationTag() {
        return value().map((entity) -> new LocationTag(
                entity.getEyePosition(),
                new Vec2(entity.getXRot(), entity.getYRot()),
                TagFactories.WORLD.of(entity.getLevel())
        )).orElse(null);
    }

    /**
     * {@return the block the entity is looking at, }
     */
    @Tag
    public LocationTag cursorOnTag() {
        return value().map((entity) -> new LocationTag(
                Vec3.atLowerCornerOf(
                        RayTrace.blocks(entity.getLevel(), entity.getEyePosition(), entity.getLookAngle(), 50.0, false, ClipContext.Fluid.NONE).block
                ),
                TagFactories.WORLD.of(entity.getLevel())
        )).orElse(null);
    }
}
