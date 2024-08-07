package io.github.kunosayo.nestle.block;

import com.mojang.serialization.MapCodec;
import io.github.kunosayo.nestle.init.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class NestleBlock extends Block {
    public static BooleanProperty INVERTED = BlockStateProperties.INVERTED;
    public static final MapCodec<NestleBlock> CODEC = simpleCodec(NestleBlock::new);

    public NestleBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(INVERTED, Boolean.FALSE));
    }

    @Override
    public MapCodec<NestleBlock> codec() {
        return CODEC;
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (level.isClientSide) {
            return;
        }
        if (entity instanceof LivingEntity livingEntity) {
            if (state.getValue(INVERTED)) {
                livingEntity.addEffect(new MobEffectInstance(ModEffects.DESIRE_NESTLE_EFFECT, 100));
            } else {
                livingEntity.addEffect(new MobEffectInstance(ModEffects.NESTLE_EFFECT, 100));
            }
        }
    }


    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(INVERTED, Boolean.valueOf(context.getLevel().hasNeighborSignal(context.getClickedPos())));
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide) {
            boolean flag = state.getValue(INVERTED);
            if (flag != level.hasNeighborSignal(pos)) {
                if (flag) {
                    level.scheduleTick(pos, this, 4);
                } else {
                    level.setBlock(pos, state.cycle(INVERTED), UPDATE_CLIENTS);
                }
            }
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(INVERTED) && !level.hasNeighborSignal(pos)) {
            level.setBlock(pos, state.cycle(INVERTED), UPDATE_CLIENTS);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(INVERTED);
    }
}
