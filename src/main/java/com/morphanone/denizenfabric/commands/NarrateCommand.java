package com.morphanone.denizenfabric.commands;

import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.scripts.commands.generator.ArgLinear;
import com.denizenscript.denizencore.scripts.commands.generator.ArgName;
import com.denizenscript.denizencore.scripts.commands.generator.ArgRaw;
import com.morphanone.denizenfabric.objects.PlayerTag;
import com.morphanone.denizenfabric.utilities.Context;
import net.minecraft.commands.CommandSource;
import net.minecraft.network.chat.Component;

public class NarrateCommand extends AbstractCommand {
    public NarrateCommand() {
        setName("narrate");
        setSyntax("narrate [<message>]");
        setRequiredArguments(1, 1);
        autoCompile();
    }

    // TODO: targets
    public static void autoExecute(ScriptEntry scriptEntry,
                                   @ArgRaw @ArgLinear @ArgName("message") String message) {
        Context.getPlayer(scriptEntry).<CommandSource>flatMap(PlayerTag::value)
                .orElse(Context.getDebugTarget(scriptEntry))
                .sendSystemMessage(Component.literal(message));
    }
}
