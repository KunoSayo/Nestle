package io.github.kunosayo.nestle.listener;

import com.mojang.datafixers.util.Pair;
import io.github.kunosayo.nestle.Nestle;
import io.github.kunosayo.nestle.config.NestleConfig;
import io.github.kunosayo.nestle.data.NestleValue;
import io.github.kunosayo.nestle.effect.NestleEffect;
import io.github.kunosayo.nestle.entity.NestleLeadEntity;
import io.github.kunosayo.nestle.entity.data.NestleData;
import io.github.kunosayo.nestle.entity.data.NestleLeadData;
import io.github.kunosayo.nestle.init.ModEffects;
import io.github.kunosayo.nestle.init.ModItems;
import io.github.kunosayo.nestle.util.NestleUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.*;

@EventBusSubscriber(modid = Nestle.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class GameListener {
    private static final HashSet<UUID> damaging = new HashSet<>();
    private static final Vec3 ALL_FIVE = new Vec3(5.0, 5.0, 5.0);
    public static HashMap<Pair<UUID, UUID>, Integer> playerNestlePlayerMap = new HashMap<>();
    private static boolean isRoot = true;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDamage(LivingDamageEvent.Pre event) {
        var entity = event.getEntity();
        if (entity.level().isClientSide) {
            return;
        }
        if (isRoot) {
            if (entity instanceof Player a) {
                if (event.getSource().getDirectEntity() instanceof Player b) {
                    // b attacked a
                    NestleData.addValue(a, b, -NestleConfig.NESTLE_CONFIG.getLeft().damagePlayerValueReduce.get());
                }
            }
        }
        if (entity.hasEffect(ModEffects.NESTLE_RESISTANCE_EFFECT)) {
            return;
        }
        boolean currentRoot = false;
        if (isRoot) {
            isRoot = false;
            currentRoot = true;
            damaging.clear();
        }
        try {
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
            final boolean selfNestle = entity.hasEffect(ModEffects.NESTLE_EFFECT);
            var otherEntityToGetDamage = entity.level()
                    .getNearbyEntities(
                            LivingEntity.class, TargetingConditions
                                    .forNonCombat()
                                    .ignoreLineOfSight()
                                    .range(NestleConfig.NESTLE_CONFIG.getLeft().nestleRadius.get()),
                            entity, aabb)
                    .stream()
                    .filter(livingEntity -> {

                        if (livingEntity.getType() == entity.getType()) {
                            if (livingEntity.position().distanceToSqr(entityPos) <= 1.5 * 1.5) {
                                return true;
                            }
                            if (isPlayer && livingEntity instanceof Player player) {
                                // both player
                                boolean playerPass = player.getData(NestleData.ATTACHMENT_TYPE)
                                        .getValue(entity.getUUID()).getValue() >= NestleConfig.NESTLE_CONFIG.getLeft().damageApportionRequire.get();
                                if (playerPass) {
                                    return true;
                                }
                            }
                        }
                        return selfNestle || livingEntity.hasEffect(ModEffects.DESIRE_NESTLE_EFFECT);
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
            if (entity instanceof Player target) {

                if (NestleLeadData.isNestle(player, target)) {
                    NestleLeadData.removeTwo(player, target);
                    return;
                }
                NestleLeadData.nestleTwo(player, target);

                // Set target and spawn entity.
                NestleLeadEntity.inParamFrom = player.getUUID();
                NestleLeadEntity.inParamTarget = entity.getUUID();


                NestleLeadEntity.ENTITY_TYPE.spawn(((ServerLevel) player.level()), player.getBlockPosBelowThatAffectsMyMovement(), MobSpawnType.EVENT);

            }
        }
    }

    @SubscribeEvent
    public static void onTickPlayer(ServerTickEvent.Pre event) {
        if (playerNestlePlayerMap.isEmpty()) {
            return;
        }


        var it = playerNestlePlayerMap.entrySet().iterator();

        var players = event.getServer().getPlayerList();

        while (it.hasNext()) {
            var entry = it.next();

            var src = players.getPlayer(entry.getKey().getFirst());
            var dst = players.getPlayer(entry.getKey().getSecond());
            if (src == null || dst == null) {
                it.remove();
                continue;
            }

            NestleUtil.nestleEntityTo(src, dst.position(), NestleEffect.SPEED, 0.5, 1.0, true, 1.0);
            if (entry.setValue(entry.getValue() - 1) <= 0) {
                it.remove();
            }
        }
    }
}
