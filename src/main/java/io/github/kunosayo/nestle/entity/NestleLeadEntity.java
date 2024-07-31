package io.github.kunosayo.nestle.entity;

import io.github.kunosayo.nestle.entity.data.NestleLeadData;
import io.github.kunosayo.nestle.listener.GameListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

import java.util.UUID;

public class NestleLeadEntity extends Entity implements IEntityWithComplexSpawn {
    public static UUID inParamFrom;
    public static UUID inParamTarget;
    public static final EntityType<NestleLeadEntity> ENTITY_TYPE = EntityType.Builder
            .<NestleLeadEntity>of(NestleLeadEntity::new, MobCategory.MISC)
            .noSave()
            .noSummon()
            .fireImmune()
            .canSpawnFarFromPlayer()
            .build("nestle_lead_entity");
    /**
     * The entity used nestle lead
     */
    public UUID from;
    /**
     * The entity
     */
    public UUID target;

    public NestleLeadEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.from = inParamFrom;
        this.target = inParamTarget;
        this.noCulling = true;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return true;
    }

    @Override
    public boolean isAlwaysTicking() {
        return true;
    }


    @Override
    public void tick() {
        super.tick();
        var level = level();
        var fromPlayer = level.getPlayerByUUID(from);
        var targetPlayer = level.getPlayerByUUID(target);
        if (fromPlayer == null || targetPlayer == null) {
            if (!level.isClientSide) {
                kill();
            }
            return;
        }
        if (!level.isClientSide) {
            // Check valid.
            teleportTo(fromPlayer.getX(), fromPlayer.getY(), fromPlayer.getZ());
            // server side
            if (!fromPlayer.hasData(NestleLeadData.ATTACHMENT_TYPE)) {
                kill();
                return;
            }
            var data = fromPlayer.getData(NestleLeadData.ATTACHMENT_TYPE);
            if (data.target != target) {
                kill();
                return;
            }

            if (fromPlayer.distanceToSqr(targetPlayer) > 144.0) {
                fromPlayer.removeData(NestleLeadData.ATTACHMENT_TYPE);
                kill();
                return;
            }
        }

        // the target we nestle with
        var toTargetVec = targetPlayer.position().subtract(fromPlayer.position());
        double sizeSqr = toTargetVec.distanceToSqr(Vec3.ZERO);
        var normal = toTargetVec.normalize();
        double pushVel = Math.max(Math.sqrt(Math.min(25.0, sizeSqr)) - 3.0, 0.0);

        var curVel = fromPlayer.getKnownMovement();
        var targetVel = new Vec3(pushVel * normal.x, pushVel * normal.y, pushVel * normal.z);

        var pendingVel = targetVel.subtract(curVel);
        if (pendingVel.equals(Vec3.ZERO)) {
            return;
        }
        double curSpeed = pendingVel.distanceTo(Vec3.ZERO);
        double fac = Math.min(curSpeed, 5.0) / 5.0;
        var finalImpulse = pendingVel.multiply(fac, fac, fac);
        GameListener.pendingVel.put(fromPlayer.getUUID(), finalImpulse);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeUUID(from);
        buffer.writeUUID(target);
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
        from = additionalData.readUUID();
        target = additionalData.readUUID();
    }
}
