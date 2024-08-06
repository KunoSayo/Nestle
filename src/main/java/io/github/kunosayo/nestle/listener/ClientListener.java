package io.github.kunosayo.nestle.listener;

import io.github.kunosayo.nestle.Nestle;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = Nestle.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientListener {
    @SubscribeEvent
    public static void onTick(ClientTickEvent.Post event) {
        var player = Minecraft.getInstance().player;
        if (player != null) {
            // found the nearest entity
            var entity = player.level().getNearestEntity(LivingEntity.class,
                    TargetingConditions.forNonCombat().ignoreLineOfSight(),
                    player, player.getX(), player.getY(), player.getZ(),
                    new AABB(-500.0 + player.getX(), -256.0 + player.getY(), -500.0 + player.getZ(),
                            500.0 + player.getX(), 256.0 + player.getY(), 500.0 + player.getZ()));
            Nestle.clientNearestEntityVec = entity == null ? null : entity.blockPosition();
        }
    }
}
