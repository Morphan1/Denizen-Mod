package com.morphanone.denizenmod.minecraft.commands;

import com.morphanone.denizenmod.tags.CommonTagContext;
import com.morphanone.denizenmod.tags.TagFactories;
import net.minecraft.commands.CommandSourceStack;

public class CommandUtil {
    public static CommonTagContext createServerContext(CommandSourceStack source) {
        return new CommonTagContext().player(TagFactories.PLAYER.of(source.getPlayer()));
    }
}
