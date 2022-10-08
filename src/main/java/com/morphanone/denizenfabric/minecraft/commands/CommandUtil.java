package com.morphanone.denizenfabric.minecraft.commands;

import com.morphanone.denizenfabric.Denizen;
import com.morphanone.denizenfabric.tags.FabricTagContext;
import com.morphanone.denizenfabric.tags.TagFactories;
import net.minecraft.commands.CommandSourceStack;

public class CommandUtil {
    public static FabricTagContext createServerContext(CommandSourceStack source) {
        return new FabricTagContext(Denizen::getDebugTarget).player(TagFactories.PLAYER.of(source.getPlayer()));
    }
}
