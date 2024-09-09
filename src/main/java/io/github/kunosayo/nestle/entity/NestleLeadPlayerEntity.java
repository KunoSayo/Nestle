package io.github.kunosayo.nestle.entity;

import io.github.kunosayo.nestle.entity.data.NestleLeadData;
import io.github.kunosayo.nestle.util.NestleUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class NestleLeadPlayerEntity extends NestleLeadEntity {
    public static UUID inParamFrom;
    public static UUID inParamTarget;
    public static final EntityType<NestleLeadPlayerEntity> ENTITY_TYPE = EntityType.Builder
            .<NestleLeadPlayerEntity>of(NestleLeadPlayerEntity::new, MobCategory.MISC)
            .noSave()
            .noSummon()
            .fireImmune()
            .canSpawnFarFromPlayer()
            .build("nestle_lead_player_entity");
    /**
     * The entity used nestle lead
     */
    public UUID from;
    /**
     * The entity
     */
    public UUID target;

    public NestleLeadPlayerEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.from = inParamFrom;
        this.target = inParamTarget;
        this.noCulling = true;
    }

    @Override
    public LivingEntity getSrc() {
        return level().getPlayerByUUID(from);
    }

    @Override
    public LivingEntity getDst() {
        return level().getPlayerByUUID(target);
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
        if (fromPlayer == null || fromPlayer.isSpectator() || targetPlayer == null || targetPlayer.isSpectator()) {
            if (!level.isClientSide) {
                kill();
            }
            return;
        }

        var mid = fromPlayer.position().add(targetPlayer.position()).multiply(0.5, 0.5, 0.5);
        if (fromPlayer.distanceToSqr(targetPlayer) > 225.0) {
            if (!level.isClientSide) {
                NestleLeadData.removeTwo(fromPlayer, targetPlayer);
                kill();
            }
            return;
        }
        if (!level.isClientSide) {
            // Check valid.
            teleportTo(mid.x, mid.y, mid.z);
            if (!NestleLeadData.isNestle(fromPlayer, targetPlayer)) {
                kill();
                return;
            }
        }


        NestleUtil.nestleEntityTo(fromPlayer, mid, 999, 3.0, 3.5, false);
        NestleUtil.nestleEntityTo(targetPlayer, mid, 999, 3.0, 3.5, false);
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
