package io.github.kunosayo.nestle.effect;

import io.github.kunosayo.nestle.config.NestleConfig;
import io.github.kunosayo.nestle.init.ModEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class DisgustNestleEffect extends MobEffect {
    public DisgustNestleEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        double radius = (NestleConfig.NESTLE_CONFIG.getLeft().nestleRadius.get() + 1.5) * (pAmplifier + 1);
        var level = pLivingEntity.level();
        var cond = TargetingConditions.forNonCombat()
                .selector(livingEntity -> !livingEntity.hasEffect(ModEffects.NESTLE_RESISTANCE_EFFECT))
                .range(radius)
                .ignoreLineOfSight();
        var rv = new Vec3(radius, radius, radius);
        var aabb = new AABB(pLivingEntity.position().subtract(rv), pLivingEntity.position().add(rv));
        List<LivingEntity> entities = level.getNearbyEntities(LivingEntity.class, cond, pLivingEntity, aabb);


        var selfPos = pLivingEntity.position();
        for (LivingEntity livingEntity : entities) {
            var toTarget = livingEntity.position().subtract(selfPos);

            double targetDis = toTarget.distanceTo(Vec3.ZERO);
            double invertDis = radius - targetDis;
            var force = toTarget.normalize().scale(invertDis / 20.0);
            if (livingEntity instanceof ServerPlayer sp) {
                if (sp.isSpectator()) {
                    continue;
                }
                sp.push(force);
                sp.hurtMarked = true;
            } else {
                livingEntity.push(force);
            }
        }
        return true;
    }


    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
        return true;
    }
}
