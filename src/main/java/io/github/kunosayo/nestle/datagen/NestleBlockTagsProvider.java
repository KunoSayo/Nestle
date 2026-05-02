package io.github.kunosayo.nestle.datagen;

import io.github.kunosayo.nestle.Nestle;
import io.github.kunosayo.nestle.init.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class NestleBlockTagsProvider extends BlockTagsProvider {
    public NestleBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Nestle.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.NESTLE_BLOCK.value(), ModBlocks.NESTLE_RESISTANCE_BLOCK.value());
    }
}
