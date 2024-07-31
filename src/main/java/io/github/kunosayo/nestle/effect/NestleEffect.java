package io.github.kunosayo.nestle.effect;

import io.github.kunosayo.nestle.config.NestleConfig;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

public class NestleEffect extends MobEffect {
    public NestleEffect(MobEffectCategory pCategory, int pColor) {
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
        int speed = 5;
        entities.stream()
                .filter(pLivingEntity::hasLineOfSight)
                .min(Comparator.comparingDouble(o -> o.position().distanceToSqr(pLivingEntity.position())))
                .ifPresent(livingEntity -> {
                    // the target we nestle with
                    var toTargetVec = livingEntity.position().subtract(pLivingEntity.position());
                    double sizeSqr = toTargetVec.distanceToSqr(Vec3.ZERO);
                    var normal = toTargetVec.normalize();

                    double pushVel = Math.max(Math.sqrt(Math.min(speed, sizeSqr)) - 0.5, 0.0);
                    pLivingEntity.push(normal.x * pushVel, normal.y * pushVel, normal.z * pushVel);


                });

        return true;
    }


    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
        return true;
    }
}
