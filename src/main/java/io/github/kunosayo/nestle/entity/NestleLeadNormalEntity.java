package io.github.kunosayo.nestle.entity;

import io.github.kunosayo.nestle.Nestle;
import io.github.kunosayo.nestle.entity.data.NestleLeadData;
import io.github.kunosayo.nestle.util.NestleUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.UUID;

public class NestleLeadNormalEntity extends NestleLeadEntity {
    public static Player inParamFrom;
    public static LivingEntity inParamTarget;
    public static final EntityType<NestleLeadNormalEntity> ENTITY_TYPE = EntityType.Builder
            .<NestleLeadNormalEntity>of(NestleLeadNormalEntity::new, MobCategory.MISC)
            .noSave()
            .noSummon()
            .sized(0.0f, 0.0f)
            .fireImmune()
            .canSpawnFarFromPlayer()
            .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Nestle.MOD_ID, "nestle_lead_normal_entity")));
    /**
     * The player entity used nestle lead
     */
    public UUID from;
    /**
     * The entity
     */
    public LivingEntity target;
    private int targetID;

    public NestleLeadNormalEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        if (inParamFrom != null) {
            this.from = inParamFrom.getUUID();
        }
        this.target = inParamTarget;
    }

    @Override
    public LivingEntity getSrc() {
        return this.level().getPlayerByUUID(from);
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
    protected void readAdditionalSaveData(ValueInput valueInput) {

    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {

    }

    @Override
    public boolean isAlwaysTicking() {
        return true;
    }


    @Override
    public void tick() {
        super.tick();

        var level = level();
        if (from == null) {
            if (level instanceof ServerLevel sl) {
                kill(sl);
            }
            return;
        }
        var fromPlayer = level.getPlayerByUUID(from);

        if (target == null && level.isClientSide()) {
            if (level.getEntity(targetID) instanceof LivingEntity l) {
                target = l;
            }
        }

        if (fromPlayer == null || this.target == null || !fromPlayer.isAlive() || !this.target.isAlive() || fromPlayer.isSpectator()) {
            if (level instanceof ServerLevel sl) {
                kill(sl);
            }
            return;
        }


        var mid = fromPlayer.position().add(target.position()).multiply(0.5, 0.5, 0.5);
        if (fromPlayer.distanceToSqr(target) > 225.0) {
            if (!level.isClientSide()) {
                NestleLeadData.removeTwo(fromPlayer, target);
                if (level instanceof ServerLevel sl) {
                    kill(sl);
                }
            }
            return;
        }
        if (!level.isClientSide()) {
            // Check valid.
            teleportTo(mid.x, mid.y, mid.z);
            if (!NestleLeadData.isNestle(fromPlayer, target)) {
                if (level instanceof ServerLevel sl) {
                    kill(sl);
                }
                return;
            }
        }


        NestleUtil.nestleEntityTo(fromPlayer, mid, 999, 3.0, 3.5, false);
        NestleUtil.nestleEntityTo(target, mid, 999, 3.0, 3.5, false);
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float v) {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {

    }


    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeUUID(from);
        buffer.writeInt(target.getId());
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
        var fromID = additionalData.readUUID();
        int targetID = additionalData.readInt();
        this.from = fromID;
        this.targetID = targetID;
        if (this.level().getEntity(targetID) instanceof LivingEntity l) {
            this.target = l;
        }
    }
}
