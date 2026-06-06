package io.github.kunosayo.nestle.client.property;

import com.mojang.serialization.MapCodec;
import io.github.kunosayo.nestle.Nestle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.NeedleDirectionHelper;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * A custom RangeSelectItemModelProperty that makes the compass needle point to the nearest living entity.
 * Similar to CompassAngle but points to the nearest entity instead of a fixed position.
 */
public class NestleCompassAngle extends NeedleDirectionHelper implements RangeSelectItemModelProperty {

    public static final MapCodec<NestleCompassAngle> MAP_CODEC = MapCodec.unit(new NestleCompassAngle());
    private final Wobbler wobbler = this.newWobbler(0.8F);
    private final Wobbler noTargetWobbler = this.newWobbler(0.8F);
    private final RandomSource random = RandomSource.create();

    public NestleCompassAngle() {
        super(true);
    }

    @Override
    protected float calculate(ItemStack itemStack, ClientLevel level, int seed, @NonNull ItemOwner owner) {
        var nearestEntity = findNearestLivingEntity(level, owner);
        long gameTime = level.getGameTime();

        if (nearestEntity == null) {
            return getRandomlySpinningRotation(seed, gameTime);
        }

        return getRotationTowardsEntity(owner, gameTime, nearestEntity);
    }

    private float getRandomlySpinningRotation(int seed, long gameTime) {
        if (this.noTargetWobbler.shouldUpdate(gameTime)) {
            this.noTargetWobbler.update(gameTime, this.random.nextFloat());
        }

        float targetRotation = this.noTargetWobbler.rotation() + (float) hash(seed) / (float) Integer.MAX_VALUE;
        return Mth.positiveModulo(targetRotation, 1.0F);
    }

    private float getRotationTowardsEntity(ItemOwner owner, long gameTime, BlockPos targetEntity) {
        double angleToTarget = getAngleFromEntityToEntity(owner, targetEntity);
        float ownerYRotation = getWrappedVisualRotationY(owner);
        LivingEntity livingEntity = owner.asLivingEntity();
        float targetRotation;

        if (livingEntity instanceof Player player && player.isLocalPlayer() && player.level().tickRateManager().runsNormally()) {
            if (this.wobbler.shouldUpdate(gameTime)) {
                this.wobbler.update(gameTime, 0.5F - (ownerYRotation - 0.25F));
            }
            targetRotation = (float) angleToTarget + this.wobbler.rotation();
            return Mth.positiveModulo(targetRotation, 1.0F);
        }

        targetRotation = 0.5F - (ownerYRotation - 0.25F - (float) angleToTarget);
        return Mth.positiveModulo(targetRotation, 1.0F);
    }

    private static double getAngleFromEntityToEntity(ItemOwner owner, BlockPos targetEntity) {
        Vec3 ownerPosition = owner.position();
        return Math.atan2(targetEntity.getZ() - ownerPosition.z(), targetEntity.getX() - ownerPosition.x()) / (Math.PI * 2);
    }

    private static float getWrappedVisualRotationY(ItemOwner owner) {
        return Mth.positiveModulo(owner.getVisualRotationYInDegrees() / 360.0F, 1.0F);
    }

    private static int hash(int input) {
        return input * 1327217883;
    }

    /**
     * Finds the nearest living entity to the item owner within a reasonable range.
     * Uses AABB search with a 500 block radius.
     */
    @Nullable
    private static BlockPos findNearestLivingEntity(ClientLevel level, ItemOwner owner) {
        return Nestle.clientNearestEntityVec;
    }

    @Override
    public MapCodec<NestleCompassAngle> type() {
        return MAP_CODEC;
    }

    /**
     * Registers this property with the item model system.
     * Call this from RegisterRangeSelectItemModelPropertyEvent handler.
     */
    public static void register(RegisterRangeSelectItemModelPropertyEvent event) {
        event.register(
                Identifier.fromNamespaceAndPath(Nestle.MOD_ID, "nearest_entity_angle"),
                MAP_CODEC
        );
    }
}
