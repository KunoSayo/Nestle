package io.github.kunosayo.nestle.item;

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

        if (!level.isClientSide) {
            Player targetPlayer = getPlayerPointingAt(player, level);
            if (targetPlayer == null) {
                return super.use(level, player, usedHand);
            }

            Vec3 playerPosition = player.position();
            Vec3 targetPlayerPosition = targetPlayer.position();
            Vec3 rushDirection = targetPlayerPosition.subtract(playerPosition).normalize();
            double rushSpeed = 20;

            player.setDeltaMovement(rushDirection.scale(rushSpeed));
            player.hurtMarked = true;
            itemStack.shrink(1);
        }
        return super.use(level, player, usedHand);
    }

    public Player getPlayerPointingAt(Player player, Level level) {
        double distance = 20.0;

        Vec3 startVec = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 endVec = startVec.add(lookVec.scale(distance));

        EntityHitResult result = ProjectileUtil.getEntityHitResult(level, player, startVec, endVec, player.getBoundingBox().expandTowards(lookVec.scale(distance)), entity -> entity instanceof Player && entity != player);
        if (result != null && result.getType() == HitResult.Type.ENTITY) {
//            System.out.println("type: " + result.getClass().getName());
            return (Player) result.getEntity();
        } else {
            return null;
        }
    }
}

