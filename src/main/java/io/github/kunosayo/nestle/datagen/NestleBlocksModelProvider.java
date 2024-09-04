package io.github.kunosayo.nestle.datagen;

import io.github.kunosayo.nestle.Nestle;
import io.github.kunosayo.nestle.init.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.GlazedTerracottaBlock;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class NestleBlocksModelProvider extends BlockStateProvider {
    public NestleBlocksModelProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Nestle.MOD_ID, exFileHelper);
    }


    private void registerNestleResistanceBlock() {
        ResourceLocation bottomTexture = modLoc("block/nestle_resistance_block_bottom");
        ResourceLocation topTexture = modLoc("block/nestle_resistance_block_top");
        ResourceLocation sideTexture = modLoc("block/nestle_resistance_block_side");
        ResourceLocation frontTexture = modLoc("block/nestle_resistance_block_front");
        ResourceLocation backTexture = modLoc("block/nestle_resistance_block_back");

        var model = models().withExistingParent("nestle_resistance_block", "minecraft:block/cube")
                .texture("top", topTexture)
                .texture("bottom", bottomTexture)
                .texture("side", sideTexture)
                .texture("front", frontTexture)
                .texture("back", backTexture)
                .element()
                .from(0.0f, 0.0f, 0.0f)
                .to(16.0f, 16.0f, 16.0f)
                .allFaces((direction, faceBuilder) -> {
                    faceBuilder.cullface(direction);
                    switch (direction) {
                        case DOWN -> faceBuilder.texture("#bottom");
                        case UP -> faceBuilder.texture("#top");
                        case NORTH -> faceBuilder.texture("#back");
                        case SOUTH -> faceBuilder.texture("#front");
                        case WEST, EAST -> faceBuilder.texture("#side");
                    }
                    switch (direction) {
                        case DOWN, UP, NORTH, SOUTH, WEST -> faceBuilder.uvs(0.0f, 0.0f, 16.0f, 16.0f);
                        case EAST -> faceBuilder.uvs(16.0f, 0.0f, 0.0f, 16.0f);
                    }
                })
                .end();
        horizontalBlock(ModBlocks.NESTLE_RESISTANCE_BLOCK.get(), model);
    }

    @Override
    protected void registerStatesAndModels() {

        registerNestleResistanceBlock();
    }
}
