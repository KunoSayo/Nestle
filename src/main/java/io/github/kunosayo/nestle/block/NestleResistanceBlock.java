package io.github.kunosayo.nestle.block;

import com.mojang.serialization.MapCodec;
import io.github.kunosayo.nestle.init.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class NestleResistanceBlock extends Block {
    public static final MapCodec<NestleResistanceBlock> CODEC = simpleCodec(NestleResistanceBlock::new);

    public NestleResistanceBlock(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<NestleResistanceBlock> codec() {
        return CODEC;
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (level.isClientSide) {
            return;
        }
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(ModEffects.NESTLE_RESISTANCE_EFFECT, 100));
        }
    }
}
