package io.github.kunosayo.nestle.datagen;

import io.github.kunosayo.nestle.Nestle;
import io.github.kunosayo.nestle.block.NestleBlock;
import io.github.kunosayo.nestle.init.ModBlocks;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.template.FaceBuilder;

import java.util.Optional;

import static net.minecraft.client.data.models.BlockModelGenerators.createBooleanModelDispatch;
import static net.minecraft.client.data.models.BlockModelGenerators.plainVariant;

public class NestleBlocksModelProvider {


    public static final ModelTemplate NESTLE_BLOCK_TEMPLATE = new ModelTemplate(ModelTemplates.CUBE.model,
            Optional.empty(),
            TextureSlot.BOTTOM,
            TextureSlot.TOP,
            TextureSlot.FRONT,
            TextureSlot.BACK,
            TextureSlot.SIDE).extend()
            .element(elementBuilder -> {
                elementBuilder.from(0.0f, 0.0f, 0.0f)
                        .to(16.0f, 16.0f, 16.0f)
                        .allFaces((direction, faceBuilder) -> {
                            faceBuilder.cullface(direction);
                            switch (direction) {
                                case DOWN -> faceBuilder.texture(TextureSlot.BOTTOM);
                                case UP -> faceBuilder.texture(TextureSlot.TOP);
                                case NORTH -> faceBuilder.texture(TextureSlot.BACK);
                                case SOUTH -> faceBuilder.texture(TextureSlot.FRONT);
                                case WEST, EAST -> faceBuilder.texture(TextureSlot.SIDE);
                            }
                            switch (direction) {
                                case DOWN, UP, NORTH, SOUTH, WEST -> faceBuilder.uvs(0.0f, 0.0f, 16.0f, 16.0f);
                                case EAST -> faceBuilder.uvs(16.0f, 0.0f, 0.0f, 16.0f);
                            }
                        });
            })
            .build();

    private static Material modLocMat(String s) {
        return new Material(Identifier.fromNamespaceAndPath(Nestle.MOD_ID, s));
    }

    private static TextureMapping getGeneralMapping(Block block, ModelTemplate template) {
        var mapping = new TextureMapping();
        for (TextureSlot requiredSlot : template.requiredSlots) {
            mapping.put(requiredSlot, TextureMapping.getBlockTexture(block, "_" + requiredSlot.getId()));
        }
        return mapping;
    }


    private static void registerNestleResistanceBlock(BlockModelGenerators blockModels) {

        Material bottomPoweredTexture = modLocMat("block/nestle_resistance_block_bottom_powered");
        Material topPoweredTexture = modLocMat("block/nestle_resistance_block_top_powered");
        Material sidePoweredTexture = modLocMat("block/nestle_resistance_block_side_powered");
        Material frontPoweredTexture = modLocMat("block/nestle_resistance_block_front");
        Material backPoweredTexture = modLocMat("block/nestle_resistance_block_back");


        var off = plainVariant(NESTLE_BLOCK_TEMPLATE.create(ModBlocks.NESTLE_RESISTANCE_BLOCK.get(),
                getGeneralMapping(ModBlocks.NESTLE_RESISTANCE_BLOCK.get(), NESTLE_BLOCK_TEMPLATE),
                blockModels.modelOutput));
        var powered = plainVariant(NESTLE_BLOCK_TEMPLATE.createWithSuffix(ModBlocks.NESTLE_RESISTANCE_BLOCK.get(), "_powered",
                new TextureMapping()
                        .put(TextureSlot.BOTTOM, bottomPoweredTexture)
                        .put(TextureSlot.TOP, topPoweredTexture)
                        .put(TextureSlot.FRONT, frontPoweredTexture)
                        .put(TextureSlot.BACK, backPoweredTexture)
                        .put(TextureSlot.SIDE, sidePoweredTexture)
                , blockModels.modelOutput));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(ModBlocks.NESTLE_RESISTANCE_BLOCK.get())
                .with(createBooleanModelDispatch(BlockStateProperties.POWERED, powered, off))
                .with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING));


    }

    private static void registerNestleBlock(BlockModelGenerators blockModels) {

        Material bottomTexture = modLocMat("block/nestle_block_bottom");
        Material topTexture = modLocMat("block/nestle_block_top");
        Material sideTexture = modLocMat("block/nestle_block_side");
        Material frontTexture = modLocMat("block/nestle_resistance_block_front");
        Material backTexture = modLocMat("block/nestle_block_back");

        Material bottomPoweredTexture = modLocMat("block/nestle_block_bottom_powered");
        Material topPoweredTexture = modLocMat("block/nestle_block_top_powered");
        Material sidePoweredTexture = modLocMat("block/nestle_block_side_powered");
        Material frontPoweredTexture = modLocMat("block/nestle_resistance_block_front");
        Material backPoweredTexture = modLocMat("block/nestle_block_back");


        var off = plainVariant(NESTLE_BLOCK_TEMPLATE.create(ModBlocks.NESTLE_BLOCK.get(),
                new TextureMapping()
                        .put(TextureSlot.BOTTOM, bottomTexture)
                        .put(TextureSlot.TOP, topTexture)
                        .put(TextureSlot.FRONT, frontTexture)
                        .put(TextureSlot.BACK, backTexture)
                        .put(TextureSlot.SIDE, sideTexture),
                blockModels.modelOutput));
        var powered = plainVariant(NESTLE_BLOCK_TEMPLATE.createWithSuffix(ModBlocks.NESTLE_BLOCK.get(), "_powered",
                new TextureMapping()
                        .put(TextureSlot.BOTTOM, bottomPoweredTexture)
                        .put(TextureSlot.TOP, topPoweredTexture)
                        .put(TextureSlot.FRONT, frontPoweredTexture)
                        .put(TextureSlot.BACK, backPoweredTexture)
                        .put(TextureSlot.SIDE, sidePoweredTexture)
                , blockModels.modelOutput));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(ModBlocks.NESTLE_BLOCK.get())
                .with(createBooleanModelDispatch(BlockStateProperties.POWERED, powered, off))
                .with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING));

    }

    public static void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        registerNestleResistanceBlock(blockModels);
        registerNestleBlock(blockModels);


    }


}
