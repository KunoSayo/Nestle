package io.github.kunosayo.nestle.listener;

import io.github.kunosayo.nestle.Nestle;
import io.github.kunosayo.nestle.config.NestleConfig;
import io.github.kunosayo.nestle.entity.NestleLeadEntity;
import io.github.kunosayo.nestle.entity.data.NestleData;
import io.github.kunosayo.nestle.entity.data.NestleLeadData;
import io.github.kunosayo.nestle.init.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

@EventBusSubscriber(modid = Nestle.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class GameListener {

    public static final HashMap<UUID, Vec3> pendingVel = new HashMap<>();
    private static final HashSet<UUID> damaging = new HashSet<>();
    private static final Vec3 ALL_FIVE = new Vec3(5.0, 5.0, 5.0);
    private static boolean isRoot = true;

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Pre event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }
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

    @SubscribeEvent
    public static void onUseItem(PlayerInteractEvent.EntityInteract event) {

        var player = event.getEntity();
        if (player.level().isClientSide) {
            return;
        }
        if (ModItems.NESTLE_LEAD.is(event.getItemStack().getItemHolder())) {
            var entity = event.getTarget();
            if (entity instanceof Player) {
                NestleLeadData data;
                if (player.hasData(NestleLeadData.ATTACHMENT_TYPE)) {
                    data = player.getData(NestleLeadData.ATTACHMENT_TYPE);
                    if (data.target == entity.getUUID()) {
                        // Cancel nestle
                        player.removeData(NestleLeadData.ATTACHMENT_TYPE);
                        return;
                    }
                } else {
                    data = player.getData(NestleLeadData.ATTACHMENT_TYPE);
                }

                // Set target and spawn entity.
                NestleLeadEntity.inParamFrom = player.getUUID();
                NestleLeadEntity.inParamTarget = entity.getUUID();


                data.target = entity.getUUID();
                NestleLeadEntity.ENTITY_TYPE.spawn(((ServerLevel) player.level()), player.getBlockPosBelowThatAffectsMyMovement(), MobSpawnType.EVENT);

            }
        }
    }

    @SubscribeEvent
    public static void onTickEntity(EntityTickEvent.Pre event) {
        if (event.getEntity() instanceof Player player) {
            var vel = pendingVel.remove(player.getUUID());
            if (vel != null) {
                player.push(vel);
            }
        }
    }
}
