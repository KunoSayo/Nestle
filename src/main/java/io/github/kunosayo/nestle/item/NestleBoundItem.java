package io.github.kunosayo.nestle.item;

import io.github.kunosayo.nestle.data.NestleBoundData;
import io.github.kunosayo.nestle.init.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * A binding item that allows players to bind their id to it via shift+right click.
 * The item displays different textures based on whether it has been bound.
 * Uses Data Components (NestleBoundData) to store binding information.
 */
public class NestleBoundItem extends Item {

    public NestleBoundItem(Identifier id) {
        var key = ResourceKey.create(Registries.ITEM, id);
        super(new Properties().stacksTo(1).setId(key));
    }

    public static HashSet<UUID> getPlayerActiveBounds(ServerPlayer player) {
        var inv = player.getInventory();
        var result = new HashSet<UUID>(9);
        for (int i = 0; i < 9; i++) {
            var item = inv.getItem(i);
            if (item.is(ModItems.NESTLE_BOUND)) {
                var data = getBoundData(item);
                if (data != null) {
                    result.add(data.id());
                }
            }
        }
        return result;
    }

    public static boolean isPlayerActiveBoundsContains(ServerPlayer player, UUID key) {
        var inv = player.getInventory();
        for (int i = 0; i < 9; i++) {
            var item = inv.getItem(i);
            if (item.is(ModItems.NESTLE_BOUND)) {
                var data = getBoundData(item);
                if (data != null && data.id().equals(key)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);

        // Only handle on server side
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        // Check if player is sneaking (shift is held)
        if (player.isSecondaryUseActive()) {
            // Check if item is already bound
            NestleBoundData boundData = getBoundData(itemStack);
            if (boundData != null) {
                // Already bound - show message or do nothing
                if (boundData.id().equals(player.getUUID())) {
                    player.sendSystemMessage(
                            Component.translatable("item.nestle.nestle_bound.already_bound_self")
                                    .withStyle(ChatFormatting.YELLOW)
                    );
                } else {
                    player.sendSystemMessage(
                            Component.translatable("item.nestle.nestle_bound.already_bound_other")
                                    .withStyle(ChatFormatting.RED)
                    );
                }
            } else {
                // Bind the item to the player
                bindToPlayer(itemStack, player);
                player.sendSystemMessage(
                        Component.translatable("item.nestle.nestle_bound.bound")
                                .withStyle(ChatFormatting.GREEN)
                );
            }
            return InteractionResult.SUCCESS_SERVER;
        }

        return super.use(level, player, usedHand);
    }

    /**
     * Binds the item to the specified player using Data Components.
     */
    public static void bindToPlayer(ItemStack itemStack, Player player) {
        NestleBoundData boundData = new NestleBoundData(player.getUUID());
        itemStack.set(ModItems.NESTLE_BOUND_DATA, boundData);
    }

    /**
     * Checks if the item is bound to any player.
     */
    public static boolean isBound(ItemStack itemStack) {
        NestleBoundData boundData = getBoundData(itemStack);
        return boundData != null;
    }

    /**
     * Gets the bound data from the item, or null if not bound.
     */
    public static NestleBoundData getBoundData(ItemStack itemStack) {
        return itemStack.get(ModItems.NESTLE_BOUND_DATA.get());
    }


    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);

        NestleBoundData boundData = getBoundData(itemStack);
        if (boundData != null) {
            String displayName = boundData.playerName() != null ? boundData.playerName() : boundData.id().toString();
            builder.accept(Component.translatable("item.nestle.nestle_bound.tooltip_bound", displayName)
                    .withStyle(ChatFormatting.AQUA));
        } else {
            builder.accept(Component.translatable("item.nestle.nestle_bound.tooltip_unbound")
                    .withStyle(ChatFormatting.GRAY));
        }

        builder.accept(Component.translatable("item.nestle.nestle_bound.tooltip_usage")
                .withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        // Add enchantment glow when bound
        return isBound(stack);
    }
}
