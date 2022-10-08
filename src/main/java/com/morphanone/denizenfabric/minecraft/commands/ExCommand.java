package com.morphanone.denizenfabric.minecraft.commands;

import com.denizenscript.denizencore.utilities.ExCommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.morphanone.denizenfabric.Denizen;
import com.morphanone.denizenfabric.scripts.FabricScriptEntryData;
import com.morphanone.denizenfabric.tags.FabricTagContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public class ExCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(generate("ex", CommandUtil::createServerContext));
    }

    public static <S> LiteralArgumentBuilder<S> generate(String nameText, Function<S, FabricTagContext> createContext) {
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
    public static <S> int run(CommandContext<S> context, boolean quiet, Function<S, FabricTagContext> createContext) {
        FabricTagContext tagContext = createContext.apply(context.getSource());
        ExCommandHelper.runString("EX_COMMAND", getString(context, "command"), new FabricScriptEntryData(tagContext), (queue) -> {
            if (tagContext.player != null) {
                queue.debugOutput = (message) -> {
                    tagContext.player.value().ifPresent((player) -> {
                        player.sendSystemMessage(
                                // TODO: gotta be a better way to handle text...
                                Component.literal(Denizen.instance.cleanseLogString(message).replace("<FORCE_ALIGN>", ""))
                        );
                    });
                };
            }
        });
        return 1;
    }
}
