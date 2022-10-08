package com.morphanone.denizenfabric.commands;

import com.denizenscript.denizencore.DenizenCore;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;

public class Commands {
    public static NarrateCommand NARRATE = registerCommand(new NarrateCommand());

    public static AbstractCommand bootstrap() {
        return NARRATE;
    }

    public static <T extends AbstractCommand> T registerCommand(T command) {
        DenizenCore.commandRegistry.register(command.getName(), command);
        return command;
    }
}
