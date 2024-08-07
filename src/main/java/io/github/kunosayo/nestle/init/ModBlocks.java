package io.github.kunosayo.nestle.init;

import io.github.kunosayo.nestle.Nestle;
import io.github.kunosayo.nestle.block.NestleBlock;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Nestle.MOD_ID);

    public static DeferredBlock<NestleBlock> NESTLE_BLOCK = BLOCKS
            .registerBlock("nestle_block", NestleBlock::new,
                    BlockBehaviour.Properties.of()
                            .mapColor(DyeColor.RED)
            );
}