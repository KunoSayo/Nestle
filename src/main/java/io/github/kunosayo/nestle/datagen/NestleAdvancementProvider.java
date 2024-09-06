package io.github.kunosayo.nestle.datagen;

import io.github.kunosayo.nestle.Nestle;
import io.github.kunosayo.nestle.advancements.NestleValueRequireTriggerInstance;
import io.github.kunosayo.nestle.init.ModAdvancements;
import io.github.kunosayo.nestle.init.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.Optional;
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
            // Generate your advancements here.
            Advancement.Builder builder = Advancement.Builder.advancement();
            builder.display(ModItems.NESTLE,
                            Component.translatable("advancements.nestle.nestle_advancement.title"),
                            Component.translatable("advancements.nestle.nestle_advancement.description"),
                            ResourceLocation.fromNamespaceAndPath("nestle", "textures/block/nestle_block_top_powered.png"),
                            AdvancementType.TASK,
                            true,
                            true,
                            true)
                    .addCriterion("nestle_require",
                            ModAdvancements.NESTLE_TRIGGER.get().createCriterion(new NestleValueRequireTriggerInstance(Optional.empty(), 1)))
                    .requirements(AdvancementRequirements.allOf(List.of("nestle_require")))
                    .save(saver, ResourceLocation.fromNamespaceAndPath(Nestle.MOD_ID, "nestle_advancement"), existingFileHelper);

        }
    }
}
