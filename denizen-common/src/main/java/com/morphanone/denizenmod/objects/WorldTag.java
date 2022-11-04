package com.morphanone.denizenmod.objects;

import com.morphanone.denizenmod.DenizenMod;
import com.morphanone.denizenmod.tags.annotations.GenerateTag;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Optional;

public class WorldTag extends AbstractObjectTag implements ObjectReferenceTag<Level> {
    public Reference<Level> level;

    public ResourceKey<Level> dimension;

    public WorldTag(ResourceKey<Level> dimension) {
        this.level = new WeakReference<>(null);
        this.dimension = dimension;
    }

    public WorldTag(Level level) {
        this.level = new WeakReference<>(level);
        if (level != null) {
            this.dimension = level.dimension();
        }
    }

    public static WorldTag fromName(String name) {
        return new WorldTag(ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(name)));
    }

    @Override
    public Optional<Level> value() {
        Level level = this.level.get();
        if (level == null) {
            level = DenizenMod.instance.getLevel(dimension);
            if (level != null) {
                this.level = new WeakReference<>(level);
            }
        }
        return Optional.ofNullable(level);
    }

    @GenerateTag
    public String name() {
        return dimension.location().toString();
    }

    @Override
    public String rawSimpleIdentity() {
        return name();
    }
}
