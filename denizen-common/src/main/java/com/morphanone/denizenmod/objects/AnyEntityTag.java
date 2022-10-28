package com.morphanone.denizenmod.objects;

import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.morphanone.denizenmod.tags.Tag;
import com.morphanone.denizenmod.tags.TagFactories;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

import java.util.Optional;

public interface AnyEntityTag extends ObjectTag {
    Optional<? extends Entity> value();

    default Optional<Component> getName() {
        return value().map(Entity::getName);
    }

    default Optional<String> getNameString() {
        return getName().map(Component::getString);
    }

    default String getRawNameString() {
        return getNameString().orElse(null);
    }

    @Tag("name")
    default ElementTag nameTag() {
        return getNameString().map(ElementTag::new).orElse(null);
    }

    @Tag("target")
    default AnyEntityTag targetTag() {
        return value().map((handle) -> {
            if (handle instanceof Mob mob) {
                return mob.getTarget();
            }
            return null;
        }).map(TagFactories.ENTITY::of).orElse(null);
    }
}
