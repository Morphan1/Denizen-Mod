package com.morphanone.denizenmod.minecraft.commands;

import com.denizenscript.denizencore.utilities.ExCommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.morphanone.denizenmod.DenizenMod;
import com.morphanone.denizenmod.scripts.CommonScriptEntryData;
import com.morphanone.denizenmod.tags.CommonTagContext;
import net.minecraft.commands.CommandSourceStack;

import java.util.function.Function;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public class ExsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(generate("exs", CommandUtil::createServerContext));
    }

    public static <S> LiteralArgumentBuilder<S> generate(String nameText, Function<S, CommonTagContext> createContext) {
        LiteralArgumentBuilder<S> name = literal(nameText);
        LiteralArgumentBuilder<S> quietArg = literal("-q");
        RequiredArgumentBuilder<S, String> commandArg = argument("command", greedyString());
        return name.then(quietArg
                        .then(commandArg
                                .executes((context) -> run(context, true, createContext))
                        )
                )
                .then(commandArg
                        .executes((context) -> run(context, false, createContext))
                );
    }

    // TODO: implement quiet, more debug
    public static <S> int run(CommandContext<S> context, boolean quiet, Function<S, CommonTagContext> createContext) {
        CommonTagContext tagContext = createContext.apply(context.getSource());
        Object source = tagContext.player != null ? tagContext.player.uuid : DenizenMod.instance.getMainCommandSource();
        ExCommandHelper.runStringSustained(source, "EXS_COMMAND", getString(context, "command"), new CommonScriptEntryData(tagContext), (queue) -> {
            if (tagContext.player != null) {
                queue.debugOutput = (message) -> tagContext.player.sendSystemMessage(
                        // TODO: gotta be a better way to handle text...
                        DenizenMod.instance.cleanseLogString(message).replace("<FORCE_ALIGN>", "")
                );
            }
        });
        return 1;
    }
}
