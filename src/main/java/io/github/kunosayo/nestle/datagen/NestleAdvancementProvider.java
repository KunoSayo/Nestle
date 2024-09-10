package io.github.kunosayo.nestle.datagen;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import io.github.kunosayo.nestle.Nestle;
import io.github.kunosayo.nestle.advancements.NestleValueRequireTriggerInstance;
import io.github.kunosayo.nestle.data.NestleValue;
import io.github.kunosayo.nestle.init.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class NestleAdvancementProvider extends AdvancementProvider {


    public NestleAdvancementProvider(PackOutput output,
                                     CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, existingFileHelper, List.of(new NestleAdvancementGenerator()));
    }

    private static final class NestleAdvancementGenerator implements AdvancementProvider.AdvancementGenerator {
        @Override
        public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper) {
            var root = Advancement.Builder.advancement().display(ModItems.NESTLE,
                            Component.translatable("advancements.nestle.nestle_advancement.title"),
                            Component.translatable("advancements.nestle.nestle_advancement.description"),
                            ResourceLocation.fromNamespaceAndPath("nestle", "textures/block/nestle_block_top_powered.png"),
                            AdvancementType.TASK,
                            true,
                            true,
                            true)
                    .addCriterion("nestle_require", NestleValueRequireTriggerInstance.instance(Either.right(1L)))
                    .requirements(AdvancementRequirements.allOf(List.of("nestle_require")))
                    .save(saver, ResourceLocation.fromNamespaceAndPath(Nestle.MOD_ID, "nestle_advancement"), existingFileHelper);


            // Bad nestle
            Advancement.Builder.advancement()
                    .parent(root)
                    .display(Items.WITHER_SKELETON_SKULL,
                            Component.translatable("advancements.nestle.nestle_neg_advancement.title"),
                            Component.translatable("advancements.nestle.nestle_neg_advancement.description"),
                            null,
                            AdvancementType.TASK,
                            true,
                            true,
                            false)
                    .addCriterion("nestle_require", NestleValueRequireTriggerInstance.instance(Either.right(-1L)))
                    .requirements(AdvancementRequirements.allOf(List.of("nestle_require")))
                    .save(saver, ResourceLocation.fromNamespaceAndPath(Nestle.MOD_ID, "nestle_neg_advancement"), existingFileHelper);

            // Close advancement
            var closeNestle = Advancement.Builder.advancement()
                    .parent(root)
                    .display(Blocks.COBWEB,
                            Component.translatable("advancements.nestle.nestle_close_advancement.title"),
                            Component.translatable("advancements.nestle.nestle_close_advancement.description"),
                            null,
                            AdvancementType.TASK,
                            true,
                            true,
                            false)
                    .addCriterion("nestle_require", NestleValueRequireTriggerInstance.instance(Either.left(Pair.of(0, 1))))
                    .requirements(AdvancementRequirements.allOf(List.of("nestle_require")))
                    .save(saver, ResourceLocation.fromNamespaceAndPath(Nestle.MOD_ID, "nestle_close_advancement"), existingFileHelper);

            Advancement.Builder.advancement()
                    .parent(closeNestle)
                    .display(Items.PINK_WOOL,
                            Component.translatable("advancements.nestle.nestle_free_advancement.title"),
                            Component.translatable("advancements.nestle.nestle_free_advancement.description"),
                            null,
                            AdvancementType.TASK,
                            true,
                            true,
                            false)
                    .addCriterion("nestle_require", NestleValueRequireTriggerInstance.instance(Either.right(0L)))
                    .requirements(AdvancementRequirements.allOf(List.of("nestle_require")))
                    .save(saver, ResourceLocation.fromNamespaceAndPath(Nestle.MOD_ID, "nestle_free_advancement"), existingFileHelper);

            // Different advancement
            Advancement.Builder.advancement()
                    .parent(root)
                    .display(Items.END_PORTAL_FRAME,
                            Component.translatable("advancements.nestle.nestle_different_advancement.title"),
                            Component.translatable("advancements.nestle.nestle_different_advancement.description"),
                            null,
                            AdvancementType.TASK,
                            true,
                            true,
                            false)
                    .addCriterion("nestle_require", NestleValueRequireTriggerInstance.instance(Either.left(Pair.of(NestleValue.DIFFERENT_INDEX, 1))))
                    .requirements(AdvancementRequirements.allOf(List.of("nestle_require")))
                    .save(saver, ResourceLocation.fromNamespaceAndPath(Nestle.MOD_ID, "nestle_different_advancement"), existingFileHelper);


        }
    }
}
