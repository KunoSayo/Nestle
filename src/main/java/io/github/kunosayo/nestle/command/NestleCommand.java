package io.github.kunosayo.nestle.command;

import com.mojang.brigadier.CommandDispatcher;
import io.github.kunosayo.nestle.entity.data.NestleLeadData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;
import java.util.stream.Stream;

public final class NestleCommand {

    private static int unlinkPlayer(ServerPlayer player) {
        var data = player.getData(NestleLeadData.ATTACHMENT_TYPE);
        int ret = data.targets.size();
        data.targets.clear();
        return ret;
    }

    private static int unlinkPlayer(ServerPlayer player, Stream<UUID> targets) {
        var data = player.getData(NestleLeadData.ATTACHMENT_TYPE);
        return targets.mapToInt(uuid -> data.targets.remove(uuid) ? 1 : 0).sum();
    }

    private static Component successMessage(int result) {
        return Component.translatable("commands.nestle.unlink.success", result);
    }

    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        // unlink: unlink self
        // unlink players: unlink with the players if self player, or unlink the players if self not player
        dispatcher.register(Commands.literal("nestle")
                .then(Commands.literal("unlink")
                        .executes(context -> {
                            var player = context.getSource().getPlayerOrException();
                            int result = unlinkPlayer(player);
                            context.getSource().sendSuccess(() -> successMessage(result), true);
                            return result;
                        })
                        .then(Commands.argument("players", EntityArgument.players())
                                .requires(commandSourceStack -> commandSourceStack.isPlayer() || commandSourceStack.hasPermission(2))
                                .executes(context -> {
                                            var srcPlayer = context.getSource().getPlayer();
                                            var playersStream = EntityArgument.getPlayers(context, "players").stream();
                                            int result;
                                            if (srcPlayer == null) {
                                                result = playersStream.mapToInt(NestleCommand::unlinkPlayer).sum();
                                            } else {
                                                result = unlinkPlayer(srcPlayer, playersStream.map(Player::getUUID));
                                            }

                                            context.getSource().sendSuccess(() -> successMessage(result), true);
                                            return result;
                                        }
                                )
                                .then(Commands.argument("targets", EntityArgument.entities())
                                        .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                                        .executes(context -> {
                                            var players = EntityArgument.getPlayers(context, "players");
                                            var targets = EntityArgument.getEntities(context, "targets");
                                            int result = players.parallelStream().mapToInt(player -> unlinkPlayer(player, targets.stream().map(Entity::getUUID))).sum();
                                            context.getSource().sendSuccess(() -> successMessage(result), true);
                                            return result;
                                        })
                                ))
                )
        );
    }
}
