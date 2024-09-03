package io.github.kunosayo.nestle.effect;

import io.github.kunosayo.nestle.config.NestleConfig;
import io.github.kunosayo.nestle.util.NestleUtil;
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
        double radius = (NestleConfig.NESTLE_CONFIG.getLeft().nestleRadius.get() + 1.5) * (pAmplifier + 1);
        var level = pLivingEntity.level();
        var cond = TargetingConditions.forNonCombat()
                .range(radius)
                .ignoreLineOfSight();
        var rv = new Vec3(radius, radius, radius);
        var aabb = new AABB(pLivingEntity.position().subtract(rv), pLivingEntity.position().add(rv));
        List<LivingEntity> entities = level.getNearbyEntities(LivingEntity.class, cond, pLivingEntity, aabb);


        for (LivingEntity livingEntity : entities) {
            NestleUtil.nestleEntityTo(livingEntity, pLivingEntity.position(), 4.5 * (pAmplifier * 0.0625 + 1), 1.5, 4.5 * (pAmplifier * 0.0625 + 1));
        }

        return true;
    }


    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
        return true;
    }
}
