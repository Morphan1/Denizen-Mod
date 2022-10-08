package com.morphanone.denizenfabric.utilities;

import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.tags.TagContext;
import com.morphanone.denizenfabric.objects.PlayerTag;
import com.morphanone.denizenfabric.tags.FabricTagContext;
import net.minecraft.commands.CommandSource;

import java.util.Objects;
import java.util.Optional;

public class Context {
    public static Optional<PlayerTag> getPlayer(TagContext context) {
        return Optional.ofNullable(((FabricTagContext) context).player);
    }

    public static Optional<PlayerTag> getPlayer(ScriptEntry entry) {
        return getPlayer(entry.getContext());
    }

    public static CommandSource getDebugTarget(TagContext context) {
        return Objects.requireNonNull(((FabricTagContext) context).debugTarget.get());
    }

    public static CommandSource getDebugTarget(ScriptEntry entry) {
        return getDebugTarget(entry.getContext());
    }
}
