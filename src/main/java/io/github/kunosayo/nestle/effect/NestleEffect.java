package io.github.kunosayo.nestle.effect;

import io.github.kunosayo.nestle.config.NestleConfig;
import io.github.kunosayo.nestle.util.NestleUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

public class NestleEffect extends MobEffect {
    public static final double SPEED = Math.sqrt(5.0);

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
        entities.stream()
                .filter(pLivingEntity::hasLineOfSight)
                .min(Comparator.comparingDouble(o -> o.position().distanceToSqr(pLivingEntity.position())))
                .ifPresent(livingEntity -> {

                    NestleUtil.nestleEntityTo(pLivingEntity, livingEntity.position(), SPEED, 0.5, 1.0, true, 1.0);
                });

        return true;
    }


    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
        return true;
    }
}
