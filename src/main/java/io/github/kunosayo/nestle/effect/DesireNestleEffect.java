package io.github.kunosayo.nestle.effect;

import io.github.kunosayo.nestle.config.NestleConfig;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class DesireNestleEffect extends MobEffect {
    public DesireNestleEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public boolean applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        int radius = NestleConfig.NESTLE_CONFIG.getLeft().nestleRadius.get() * (pAmplifier + 1);
        var level = pLivingEntity.level();
        var cond = TargetingConditions.forNonCombat()
                .range(radius)
                .ignoreLineOfSight();
        var rv = new Vec3(radius, radius, radius);
        var aabb = new AABB(pLivingEntity.position().subtract(rv), pLivingEntity.position().add(rv));
        List<LivingEntity> entities = level.getNearbyEntities(LivingEntity.class, cond, pLivingEntity, aabb);

        // the nestle speed by default
        // do not increase the speed for ... ?
        int speed = 20;

        for (LivingEntity livingEntity : entities) {
            // the target we nestle with
            var toTargetVec = livingEntity.position().subtract(pLivingEntity.position());
            double sizeSqr = toTargetVec.distanceToSqr(Vec3.ZERO);
            var normal = toTargetVec.normalize();
            double pushVel = Math.max(Math.sqrt(Math.min(speed, sizeSqr)) - 1.5, 0.0) * (pAmplifier * 0.0625 + 1);

            var curVel = livingEntity.getKnownMovement();
            var targetVel = new Vec3(-pushVel * normal.x, -pushVel * normal.y, -pushVel * normal.z);

            var pendingVel = targetVel.subtract(curVel);
            if (pendingVel.equals(Vec3.ZERO)) {
                continue;
            }
            double curSpeed = pendingVel.distanceTo(Vec3.ZERO);
            double fac = Math.min(curSpeed, speed) / speed;
            var finalImpulse = pendingVel.multiply(fac, fac, fac);
            livingEntity.push(finalImpulse);

        }


        return true;
    }


    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
        return true;
    }
}
