package com.morphanone.denizenmod.tags.factories;

import com.denizenscript.denizencore.tags.TagContext;
import com.morphanone.denizenmod.DenizenMod;
import com.morphanone.denizenmod.objects.WorldTag;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class WorldTagFactory extends ObjectReferenceTagFactory<WorldTag, Level> {
    public WorldTagFactory() {
        super(WorldTag.class, Level.class);
    }

    @Override
    public String name() {
        return "world";
    }

    @Override
    public String objectIdentifier() {
        return "w";
    }

    @Override
    public WorldTag getDefault(TagContext context) {
        return null;
    }

    public Level fromIdentity(String input) {
        return DenizenMod.instance.getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(input)));
    }

    @Override
    public WorldTag valueOf(String input, TagContext context) {
        return of(fromIdentity(input));
    }

    @Override
    public boolean matches(String input) {
        return fromIdentity(input) != null;
    }

    @Override
    public WorldTag of(Level level) {
        return level != null ? new WorldTag(level) : null;
    }
}
