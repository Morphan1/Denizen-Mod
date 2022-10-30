package com.morphanone.denizenmod.objects;

import com.denizenscript.denizencore.objects.core.ElementTag;
import com.morphanone.denizenmod.tags.Tag;
import com.morphanone.denizenmod.tags.TagFactories;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

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

    @Tag("name")
    public ElementTag nameTag() {
        return getNameString().map(ElementTag::new).orElse(null);
    }

    @Tag("target")
    public AbstractEntityTag targetTag() {
        return value().map((handle) -> {
            if (handle instanceof Mob mob) {
                return mob.getTarget();
            }
            return null;
        }).map(TagFactories.ENTITY_ANY::of).orElse(null);
    }
}
