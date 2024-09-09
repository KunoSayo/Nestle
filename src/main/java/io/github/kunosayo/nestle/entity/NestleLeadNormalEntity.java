package io.github.kunosayo.nestle.entity;

import io.github.kunosayo.nestle.entity.data.NestleLeadData;
import io.github.kunosayo.nestle.util.NestleUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class NestleLeadNormalEntity extends NestleLeadEntity {
    public static Player inParamFrom;
    public static LivingEntity inParamTarget;
    public static final EntityType<NestleLeadNormalEntity> ENTITY_TYPE = EntityType.Builder
            .<NestleLeadNormalEntity>of(NestleLeadNormalEntity::new, MobCategory.MISC)
            .noSave()
            .noSummon()
            .fireImmune()
            .canSpawnFarFromPlayer()
            .build("nestle_lead_normal_entity");
    /**
     * The entity used nestle lead
     */
    public Player from;
    /**
     * The entity
     */
    public LivingEntity target;

    public NestleLeadNormalEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.from = inParamFrom;
        this.target = inParamTarget;
        this.noCulling = true;
    }

    @Override
    public LivingEntity getSrc() {
        return from;
    }

    @Override
    public LivingEntity getDst() {
        return target;
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
        if (this.from == null || this.target == null || !this.from.isAlive() || !this.target.isAlive() || from.isSpectator()) {
            if (!level.isClientSide) {
                kill();
            }
            return;
        }

        var fromPlayer = from;

        var mid = fromPlayer.position().add(target.position()).multiply(0.5, 0.5, 0.5);
        if (fromPlayer.distanceToSqr(target) > 225.0) {
            if (!level.isClientSide) {
                NestleLeadData.removeTwo(fromPlayer, target);
                kill();
            }
            return;
        }
        if (!level.isClientSide) {
            // Check valid.
            teleportTo(mid.x, mid.y, mid.z);
            if (!NestleLeadData.isNestle(fromPlayer, target)) {
                kill();
                return;
            }
        }


        NestleUtil.nestleEntityTo(fromPlayer, mid, 999, 3.0, 3.5, false);
        NestleUtil.nestleEntityTo(target, mid, 999, 3.0, 3.5, false);
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
        buffer.writeInt(from.getId());
        buffer.writeInt(target.getId());
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
        int fromID = additionalData.readInt();
        int targetID = additionalData.readInt();
        if (this.level().getEntity(fromID) instanceof Player p) {
            this.from = p;
        }
        if (this.level().getEntity(targetID) instanceof LivingEntity l) {
            this.target = l;
        }
    }
}
