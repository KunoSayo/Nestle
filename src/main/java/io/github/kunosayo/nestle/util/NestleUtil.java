package io.github.kunosayo.nestle.util;

import com.mojang.datafixers.util.Pair;
import io.github.kunosayo.nestle.init.ModEffects;
import io.github.kunosayo.nestle.listener.GameListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public final class NestleUtil {

    /**
     * Nestle entity to target position with distance <= radius
     *
     * @param livingEntity  the entity to push
     * @param target        the target position
     * @param maxSpeed      the max speed
     * @param radius        the target position radius
     * @param maxDeltaSpeed max delta speed or 0 if unlimited
     */
    public static void nestleEntityTo(LivingEntity livingEntity, Vec3 target, double maxSpeed, double radius, double maxDeltaSpeed) {
        nestleEntityTo(livingEntity, target, maxSpeed, radius, maxDeltaSpeed, true);
    }

    /**
     * Nestle entity to target position with distance <= radius
     *
     * @param livingEntity  the entity to push
     * @param target        the target position
     * @param maxSpeed      the max speed
     * @param radius        the target position radius
     * @param maxDeltaSpeed max delta speed or 0 to push directly
     * @param sendPacket    if true, will send packet to client if entity is player
     */
    public static void nestleEntityTo(LivingEntity livingEntity, Vec3 target, double maxSpeed, double radius, double maxDeltaSpeed, boolean sendPacket) {
        nestleEntityTo(livingEntity, target, maxSpeed, radius, maxDeltaSpeed, sendPacket, 20.0);
    }

    /**
     * Nestle entity to target position with distance <= radius
     *
     * @param livingEntity  the entity to push
     * @param target        the target position
     * @param maxSpeed      the max speed
     * @param radius        the target position radius
     * @param maxDeltaSpeed max delta speed or 0 to push directly
     * @param sendPacket    if true, will send packet to client if entity is player
     * @param div           The final speed will div this
     */
    public static void nestleEntityTo(LivingEntity livingEntity, Vec3 target, double maxSpeed, double radius, double maxDeltaSpeed, boolean sendPacket, double div) {
        if (livingEntity == null || livingEntity.hasEffect(ModEffects.NESTLE_RESISTANCE_EFFECT)) {
            return;
        }
        if (livingEntity instanceof Player player) {
            if (player.isSpectator()) {
                return;
            }
        }

        var toTargetVec = target.subtract(livingEntity.position());
        double size = toTargetVec.distanceTo(Vec3.ZERO);
        var normal = toTargetVec.normalize();
        if (size < 1.0E-4) {
            return;
        } else {
            normal = new Vec3(toTargetVec.x / size, toTargetVec.y / size, toTargetVec.z / size);
        }

        final double pushVel = Math.max(Math.min(size - radius, maxSpeed), 0.0);

        if (pushVel == 0.0) {
            return;
        }

        var targetVel = new Vec3(pushVel * normal.x, pushVel * normal.y, pushVel * normal.z);

        if (maxDeltaSpeed <= 0) {
            livingEntity.push(targetVel);
            if (sendPacket && livingEntity instanceof ServerPlayer player) {
                player.hurtMarked = true;
            }
            return;
        }

        var curVel = livingEntity.getKnownMovement();

        var pendingVel = targetVel.subtract(curVel);
        if (pendingVel.distanceToSqr(Vec3.ZERO) < 1e-4) {
            return;
        }
        double curSpeed = pendingVel.distanceTo(Vec3.ZERO);
        maxDeltaSpeed = Math.min(maxDeltaSpeed, pushVel);
        double fac = Math.min(curSpeed, maxDeltaSpeed) / curSpeed / div;
        var finalImpulse = pendingVel.multiply(fac, fac, fac);
        livingEntity.push(finalImpulse);
        if (sendPacket && livingEntity instanceof ServerPlayer player) {
            player.hurtMarked = true;
        }
    }

    public static void playerNestlePlayer(UUID src, UUID dst, int ticks) {
        GameListener.playerNestlePlayerMap.put(new Pair<>(src, dst), 10);

    }
}
