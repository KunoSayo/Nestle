package io.github.kunosayo.nestle.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

public abstract class NestleLeadEntity extends Entity implements IEntityWithComplexSpawn {

    public NestleLeadEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public abstract LivingEntity getSrc();

    public abstract LivingEntity getDst();

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float v) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {

    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {

    }

    void kill() {
        if (level() instanceof ServerLevel sl) {
            kill(sl);
        }
    }
}
