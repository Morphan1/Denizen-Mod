package com.morphanone.denizenmod.objects;

import com.denizenscript.denizencore.objects.core.ElementTag;
import com.morphanone.denizenmod.DenizenMod;
import com.morphanone.denizenmod.tags.Tag;
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

    public WorldTag(Level level) {
        this.level = new WeakReference<>(level);
        if (level != null) {
            this.dimension = level.dimension();
        }
    }

    public static WorldTag fromName(String name) {
        WorldTag worldTag = new WorldTag(null);
        worldTag.dimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(name));
        return worldTag;
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

    public String name() {
        return dimension.location().toString();
    }

    @Override
    public String rawSimpleIdentity() {
        return name();
    }

    @Tag("name")
    public ElementTag nameTag() {
        return new ElementTag(name());
    }
}
