package io.github.kunosayo.nestle.listener;

import com.mojang.datafixers.util.Pair;
import io.github.kunosayo.nestle.Nestle;
import io.github.kunosayo.nestle.config.NestleConfig;
import io.github.kunosayo.nestle.data.NestleValue;
import io.github.kunosayo.nestle.effect.NestleEffect;
import io.github.kunosayo.nestle.entity.NestleLeadNormalEntity;
import io.github.kunosayo.nestle.entity.NestleLeadPlayerEntity;
import io.github.kunosayo.nestle.entity.data.NestleData;
import io.github.kunosayo.nestle.entity.data.NestleLeadData;
import io.github.kunosayo.nestle.init.ModEffects;
import io.github.kunosayo.nestle.init.ModItems;
import io.github.kunosayo.nestle.item.NestleBoundItem;
import io.github.kunosayo.nestle.util.NestleUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = Nestle.MOD_ID)
public class GameListener {
    private static final HashSet<UUID> damaging = new HashSet<>();
    private static final Vec3 ALL_FIVE = new Vec3(5.0, 5.0, 5.0);
    public static HashMap<Pair<UUID, UUID>, Integer> playerNestlePlayerMap = new HashMap<>();
    private static boolean isRoot = true;


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDamage(LivingDamageEvent.Pre event) {
        var entity = event.getEntity();
        if (entity.level().isClientSide()) {
            return;
        }
        ServerLevel sl;
        if (entity.level() instanceof ServerLevel tsl) {
            sl = tsl;
        } else {
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

            boolean selfIsPlayer = entity instanceof Player;
            final boolean selfNestle = entity.hasEffect(ModEffects.NESTLE_EFFECT);
            final boolean requireDesireNestle = !selfNestle && NestleConfig.NESTLE_CONFIG.getLeft().entitiesNotSpreadDamageByDefaultSet.contains(entity.getType());

            var cond = TargetingConditions
                    .forNonCombat()
                    .ignoreLineOfSight()
                    .range(NestleConfig.NESTLE_CONFIG.getLeft().nestleRadius.get());
            var otherEntityToGetDamage = sl.getNearbyEntities(LivingEntity.class, cond, entity, aabb)
                    .stream()
                    .filter(livingEntity -> {
                        final boolean hasDesire = livingEntity.hasEffect(ModEffects.DESIRE_NESTLE_EFFECT);

                        if (livingEntity.isSpectator() || livingEntity.isInvulnerable() || livingEntity.isRemoved() || livingEntity.dead) {
                            return false;
                        }
                        if (livingEntity instanceof Player player) {
                            if (player.isCreative()) {
                                return false;
                            }
                            if (selfIsPlayer) {
                                // both player
                                boolean playerPass = player.getData(NestleData.ATTACHMENT_TYPE)
                                        .getValue(entity.getUUID()).getValue() >= NestleConfig.NESTLE_CONFIG.getLeft().damageApportionRequire.get();
                                if (playerPass) {
                                    return true;
                                }
                            }
                        }

                        if (requireDesireNestle && !hasDesire) {
                            return false;
                        }

                        if (livingEntity.getType() == entity.getType()) {
                            if (livingEntity.position().distanceToSqr(entityPos) <= 1.5 * 1.5) {
                                return true;
                            }
                        }

                        return selfNestle || hasDesire;
                    })
                    // not in the damage chain.
                    .filter(livingEntity -> damaging.add(livingEntity.getUUID()))
                    .collect(Collectors.toSet());

            // Get players that have nestle_bound
            ServerPlayer selfPlayer = null;
            var otherPlayerAdded = new ArrayList<ServerPlayer>();
            if (entity instanceof ServerPlayer theSelfPlayer) {
                selfPlayer = theSelfPlayer;
                var selfBound = NestleBoundItem.getPlayerActiveBounds(selfPlayer);
                for (ServerPlayer otherPlayer : tsl.getServer().getPlayerList().getPlayers()) {
                    if (otherPlayer.isCreative() || otherPlayer.isSpectator() || otherPlayer.isInvulnerable() || otherPlayer.isRemoved() || otherPlayer.dead) {
                        continue;
                    }
                    if (otherEntityToGetDamage.contains(otherPlayer)) {
                        continue;
                    }

                    if (otherPlayer.hasEffect(ModEffects.NESTLE_RESISTANCE_EFFECT)) {
                        continue;
                    }

                    var aValue = NestleData.getValueTo(selfPlayer, otherPlayer);
                    if (aValue.getValue() < 0) {
                        continue;
                    }
                    var bValue = NestleData.getValueTo(otherPlayer, selfPlayer);
                    if (bValue.getValue() < 0) {
                        continue;
                    }
                    if (selfBound.contains(otherPlayer.getUUID())) {
                        otherPlayerAdded.add(otherPlayer);
                        continue;
                    }
                    if (NestleBoundItem.isPlayerActiveBoundsContains(otherPlayer, selfPlayer.getUUID())) {
                        otherPlayerAdded.add(otherPlayer);
                    }
                }

                otherEntityToGetDamage.addAll(otherPlayerAdded);
            }

            int totalCount = otherEntityToGetDamage.size() + 1;

            float damageToSpread = originDamage / totalCount;
            boolean anyLive = entity.getHealth() > rawDamage / totalCount;
            if (!anyLive) {
                for (LivingEntity livingEntity : otherEntityToGetDamage) {
                    if (livingEntity.getHealth() > damageToSpread) {
                        anyLive = true;
                        break;
                    }
                }
            }

            if (!anyLive) {
                return;
            }
            for (LivingEntity livingEntity : otherEntityToGetDamage) {
                livingEntity.hurt(event.getSource(), damageToSpread);
            }

            for (ServerPlayer otherPlayer : otherPlayerAdded) {
                NestleData.addValue(selfPlayer, otherPlayer, -(int) Math.round(damageToSpread * NestleConfig.NESTLE_CONFIG.getLeft().boundDamageValueScale.get()));
                NestleData.addValue(otherPlayer, selfPlayer, -(int) Math.round(damageToSpread * NestleConfig.NESTLE_CONFIG.getLeft().boundDamageValueScale.get()));
            }

            event.setNewDamage(rawDamage / totalCount);

        } finally {
            if (currentRoot) {
                isRoot = true;
            }
        }
    }

    @SubscribeEvent
    public static void onUseItem(PlayerInteractEvent.EntityInteractSpecific event) {
        var player = event.getEntity();

        if (ModItems.NESTLE_LEAD.is(event.getItemStack().typeHolder())) {
            if (NestleConfig.NESTLE_CONFIG.getLeft().nestleLeadAvoidEntitiesSet.contains(event.getEntity().getType())) {
                return;
            }
            var entity = event.getTarget();
            if (entity instanceof Player target) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
                if (player.level().isClientSide()) {
                    return;
                }
                if (NestleLeadData.isNestle(player, target)) {
                    NestleLeadData.removeTwo(player, target);
                    return;
                }
                NestleLeadData.nestleTwo(player, target);

                // Set target and spawn entity.
                NestleLeadPlayerEntity.inParamFrom = player.getUUID();
                NestleLeadPlayerEntity.inParamTarget = entity.getUUID();


                NestleLeadPlayerEntity.ENTITY_TYPE.spawn(((ServerLevel) player.level()), player.getBlockPosBelowThatAffectsMyMovement(), EntitySpawnReason.EVENT);
            } else if (entity instanceof LivingEntity target) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
                if (player.level().isClientSide()) {
                    return;
                }
                if (NestleLeadData.isNestle(player, target)) {
                    NestleLeadData.removeTwo(player, target);
                    return;
                }
                NestleLeadData.nestleTwo(player, target);

                // Set target and spawn entity.
                NestleLeadNormalEntity.inParamFrom = player;
                NestleLeadNormalEntity.inParamTarget = target;

                NestleLeadNormalEntity.ENTITY_TYPE.spawn(((ServerLevel) player.level()), player.getBlockPosBelowThatAffectsMyMovement(), EntitySpawnReason.EVENT);

                NestleLeadNormalEntity.inParamFrom = null;
                NestleLeadNormalEntity.inParamTarget = null;
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
