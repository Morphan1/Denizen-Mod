package com.morphanone.denizenmod.tags.factories;

import com.denizenscript.denizencore.tags.TagContext;
import com.morphanone.denizenmod.objects.WorldTag;
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

    public WorldTag fromIdentity(String input) {
        WorldTag worldTag = WorldTag.fromName(input);
        return worldTag.value().isPresent() ? worldTag : null;
    }

    @Override
    public WorldTag valueOf(String input, TagContext context) {
        return fromIdentity(input);
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
