package io.github.kunosayo.nestle.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class NestleCommand implements Command<CommandSourceStack> {
    public static final Command<CommandSourceStack> INSTANCE = new NestleCommand();

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Hello World!"));
        return 0;
    }
}
