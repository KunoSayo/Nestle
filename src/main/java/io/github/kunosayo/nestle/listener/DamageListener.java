package io.github.kunosayo.nestle.listener;

import io.github.kunosayo.nestle.Nestle;
import io.github.kunosayo.nestle.config.NestleConfig;
import io.github.kunosayo.nestle.entity.data.NestleData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.HashSet;
import java.util.UUID;

@EventBusSubscriber(modid = Nestle.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class DamageListener {

    private static final HashSet<UUID> damaging = new HashSet<>();
    private static final Vec3 ALL_FIVE = new Vec3(5.0, 5.0, 5.0);
    private static boolean isRoot = true;

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Pre event) {
        boolean currentRoot = false;
        if (isRoot) {
            isRoot = false;
            currentRoot = true;
            damaging.clear();
        }
        try {
            var entity = event.getEntity();
            var uuid = entity.getUUID();
            damaging.add(uuid);


            float originDamage = event.getOriginalDamage();
            float rawDamage = event.getNewDamage();
            if (!(Float.isFinite(rawDamage) && originDamage > 0.0f && rawDamage > 0.0f)) {

                return;
            }

            var entityPos = entity.position();

            var aabb = new AABB(entityPos.subtract(ALL_FIVE), entityPos.add(ALL_FIVE));

            boolean isPlayer = entity instanceof Player;

            var otherEntityToGetDamage = entity.level()
                    .getNearbyEntities(
                            entity.getClass(), TargetingConditions
                                    .forNonCombat()
                                    .ignoreLineOfSight()
                                    .range(5.0),
                            entity, aabb)
                    .stream()
                    .filter(livingEntity -> {
                        if (isPlayer && livingEntity instanceof Player player) {
                            return livingEntity.position().distanceToSqr(entityPos) <= 1.0
                                    || player.getData(NestleData.ATTACHMENT_TYPE)
                                    .getValue(entity.getUUID()).getValue() >= NestleConfig.NESTLE_CONFIG.getLeft().damageApportionRequire.get();
                        } else {
                            return livingEntity.position().distanceToSqr(entityPos) <= 1.0;
                        }
                    })
                    // not in the damage chain.
                    .filter(livingEntity -> damaging.add(livingEntity.getUUID()))
                    .toList();

            int totalCount = otherEntityToGetDamage.size() + 1;

            float damageToSpread = originDamage / totalCount;
            for (LivingEntity livingEntity : otherEntityToGetDamage) {
                livingEntity.hurt(event.getSource(), damageToSpread);
            }

            event.setNewDamage(rawDamage / totalCount);

        } finally {
            if (currentRoot) {
                isRoot = true;
            }
        }


    }
}
