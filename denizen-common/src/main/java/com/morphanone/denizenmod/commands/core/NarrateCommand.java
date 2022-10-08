package com.morphanone.denizenmod.commands.core;

import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.scripts.commands.generator.ArgLinear;
import com.denizenscript.denizencore.scripts.commands.generator.ArgName;
import com.denizenscript.denizencore.scripts.commands.generator.ArgRaw;
import com.morphanone.denizenmod.DenizenMod;
import com.morphanone.denizenmod.objects.PlayerTag;
import com.morphanone.denizenmod.utilities.Context;
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
                .orElse(DenizenMod.instance.getMainCommandSource())
                .sendSystemMessage(Component.literal(message));
    }
}
