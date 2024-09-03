package io.github.kunosayo.nestle.item;

import com.mojang.datafixers.util.Pair;
import io.github.kunosayo.nestle.config.NestleConfig;
import io.github.kunosayo.nestle.entity.data.NestleData;
import io.github.kunosayo.nestle.listener.GameListener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;


public class NestleItem extends Item {
    public NestleItem() {
        super(new Properties().stacksTo(64));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);

        Player targetPlayer = getPlayerPointingAt(player, level);
        if (targetPlayer == null) {
            return super.use(level, player, usedHand);
        }

        itemStack.consume(1, player);

        if (!level.isClientSide) {
            GameListener.playerNestlePlayerMap.put(new Pair<>(player.getUUID(), targetPlayer.getUUID()), 10);
            NestleData.addValue(player, targetPlayer, NestleConfig.NESTLE_CONFIG.getLeft().damagePlayerValueReduce.get());

        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }

    public Player getPlayerPointingAt(Player player, Level level) {
        double distance = NestleConfig.NESTLE_CONFIG.getLeft().nestleRadius.get();

        Vec3 startVec = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 endVec = startVec.add(lookVec.scale(distance));

        EntityHitResult result = ProjectileUtil.getEntityHitResult(level, player, startVec, endVec, player.getBoundingBox().expandTowards(lookVec.scale(distance)), entity -> entity != player);
        if (result != null && result.getType() == HitResult.Type.ENTITY && result.getEntity() instanceof Player targetPlayer && player.hasLineOfSight(targetPlayer)) {

            return targetPlayer;
        } else {
            return null;
        }
    }
}

