package com.morphanone.denizenmod.utilities;

import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.tags.TagContext;
import com.morphanone.denizenmod.objects.PlayerTag;
import com.morphanone.denizenmod.tags.CommonTagContext;
import net.minecraft.commands.CommandSource;

import java.util.Objects;
import java.util.Optional;

public class Context {
    public static Optional<PlayerTag> getPlayer(TagContext context) {
        return Optional.ofNullable(((CommonTagContext) context).player);
    }

    public static Optional<PlayerTag> getPlayer(ScriptEntry entry) {
        return getPlayer(entry.getContext());
    }
}
