package io.github.kunosayo.nestle.client.listener;

import io.github.kunosayo.nestle.Nestle;
import io.github.kunosayo.nestle.client.gui.PlayerNestleInfoList;
import io.github.kunosayo.nestle.client.screen.NestleScreen;
import io.github.kunosayo.nestle.client.task.SingleTask;
import io.github.kunosayo.nestle.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Comparator;


@EventBusSubscriber(value = Dist.CLIENT, modid = Nestle.MOD_ID)
public class ClientListener {
    @SubscribeEvent
    public static void onClientLeave(ClientPlayerNetworkEvent.LoggingOut event) {
        PlayerNestleInfoList.clear();
        SingleTask.INSTANCE.clearTasks();
    }

    @SubscribeEvent
    public static void onTick(ClientTickEvent.Post event) {
        var player = Minecraft.getInstance().player;
        if (player != null) {
            // found the nearest entity
            var aabb = new AABB(-500.0 + player.getX(), -256.0 + player.getY(), -500.0 + player.getZ(),
                    500.0 + player.getX(), 256.0 + player.getY(), 500.0 + player.getZ());
            var entities = player.level().getEntitiesOfClass(LivingEntity.class,
                    aabb, livingEntity -> !livingEntity.isDeadOrDying() && livingEntity != player);
            entities.stream().min(Comparator.comparingDouble(o -> o.distanceToSqr(player)))
                    .ifPresentOrElse(livingEntity -> Nestle.clientNearestEntityVec = livingEntity.blockPosition(), () -> Nestle.clientNearestEntityVec = null);
        } else {
            Nestle.clientNearestEntityVec = null;
        }
    }

    @SubscribeEvent
    public static void onUse(PlayerInteractEvent.RightClickItem event) {
        if (event.getItemStack().is(ModItems.NESTLE_COMPASS) && event.getEntity() == Minecraft.getInstance().player) {
            if (!Minecraft.getInstance().player.isSpectator()) {
                Minecraft.getInstance().setScreen(new NestleScreen());
            }
        }
    }
}
