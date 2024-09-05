package io.github.kunosayo.nestle.datagen;

import io.github.kunosayo.nestle.init.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class BlocksLootSubProvider extends BlockLootSubProvider {


    protected BlocksLootSubProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, registries);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries()
                .stream()
                .map(blockDeferredHolder -> (Block) blockDeferredHolder.value())
                .toList();
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.NESTLE_BLOCK.get());
        dropSelf(ModBlocks.NESTLE_RESISTANCE_BLOCK.get());
    }
}
