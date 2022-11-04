package com.morphanone.denizenmod.objects;

import com.denizenscript.denizencore.objects.core.ElementTag;
import com.morphanone.denizenmod.tags.annotations.Tag;
import com.morphanone.denizenmod.tags.TagFactories;
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

    public String getRawNameString() {
        return getNameString().orElse(null);
    }

    @Tag
    public ElementTag nameTag() {
        return getNameString().map(ElementTag::new).orElse(null);
    }

    @Tag
    public AbstractEntityTag targetTag() {
        return value().map((entity) -> entity instanceof Mob mob ? TagFactories.ENTITY_ANY.of(mob) : null).orElse(null);
    }

    @Tag
    public LocationTag locationTag() {
        return value().map((entity) -> new LocationTag(
                entity.position(),
                new Vec2(entity.getXRot(), entity.getYRot()),
                TagFactories.WORLD.of(entity.getLevel())
        )).orElse(null);
    }

    @Tag
    public LocationTag eyeLocationTag() {
        return value().map((entity) -> new LocationTag(
                entity.getEyePosition(),
                new Vec2(entity.getXRot(), entity.getYRot()),
                TagFactories.WORLD.of(entity.getLevel())
        )).orElse(null);
    }

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
