package io.github.kunosayo.nestle.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

public abstract class NestleLeadEntity extends Entity implements IEntityWithComplexSpawn {

    public NestleLeadEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public abstract LivingEntity getSrc();

    public abstract LivingEntity getDst();
}
