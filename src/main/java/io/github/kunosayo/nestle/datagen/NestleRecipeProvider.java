package io.github.kunosayo.nestle.datagen;

import io.github.kunosayo.nestle.init.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.jspecify.annotations.NonNull;

/**
 * Recipe datagen provider for Nestle mod.
 * Generates all crafting recipes for the mod.
 */
public class NestleRecipeProvider extends RecipeProvider {

    /**
     * Creates a new NestleRecipeProvider.
     *
     * @param provider the registry provider
     * @param output   the recipe output
     */
    protected NestleRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
        super(provider, output);
    }

    @Override
    protected void buildRecipes() {
        // Nestle item recipe - 9 strings -> 4 nestle
        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.NESTLE.get(), 4)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', Items.STRING)
                .group("nestle")
                .unlockedBy("has_string", has(Items.STRING))
                .save(output);

        // Nestle block recipe - 8 stone surrounding 1 nestle -> 8 nestle blocks
        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.NESTLE_BLOCK_ITEM.get(), 8)
                .pattern("###")
                .pattern("#I#")
                .pattern("###")
                .define('#', Blocks.STONE)
                .define('I', ModItems.NESTLE.get())
                .group("nestle")
                .unlockedBy("has_nestle", has(ModItems.NESTLE.get()))
                .save(output);

        // Nestle compass recipe - compass + nestle -> nestle compass
        ShapelessRecipeBuilder.shapeless(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.NESTLE_COMPASS.get())
                .requires(Items.COMPASS)
                .requires(ModItems.NESTLE.get())
                .group("nestle")
                .unlockedBy("has_compass", has(Items.COMPASS))
                .unlockedBy("has_nestle", has(ModItems.NESTLE.get()))
                .save(output);

        // Nestle lead recipe - lead + nestle -> nestle lead
        ShapelessRecipeBuilder.shapeless(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.NESTLE_LEAD.get())
                .requires(Items.LEAD)
                .requires(ModItems.NESTLE.get())
                .group("nestle")
                .unlockedBy("has_lead", has(Items.LEAD))
                .unlockedBy("has_nestle", has(ModItems.NESTLE.get()))
                .save(output);

        // Nestle resistance block recipe - cactus + nestle block -> nestle resistance block
        ShapelessRecipeBuilder.shapeless(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.NESTLE_RESISTANCE_BLOCK_ITEM.get())
                .requires(Blocks.CACTUS)
                .requires(ModItems.NESTLE_BLOCK_ITEM.get())
                .group("nestle")
                .unlockedBy("has_cactus", has(Blocks.CACTUS))
                .unlockedBy("has_nestle_block", has(ModItems.NESTLE_BLOCK_ITEM.get()))
                .save(output);

        // Nestle bound recipe - nestle lead + nestle -> nestle bound
        ShapelessRecipeBuilder.shapeless(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.NESTLE_BOUND.get())
                .requires(ModItems.NESTLE_LEAD.get())
                .requires(ModItems.NESTLE.get())
                .unlockedBy("has_nestle_lead", has(ModItems.NESTLE_LEAD.get()))
                .unlockedBy("has_nestle", has(ModItems.NESTLE.get()))
                .save(output);

        // Unbind nestle bound recipe - nestle bound -> nestle bound (recipe book display recipe)
        ShapelessRecipeBuilder.shapeless(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ModItems.NESTLE_BOUND.get())
                .requires(ModItems.NESTLE_BOUND.get())
                .group("nestle")
                .unlockedBy("has_nestle_bound", has(ModItems.NESTLE_BOUND.get()))
                .save(output, "unbind_nestle_bound");
    }

    /**
     * Runner class for registering the recipe provider with the data generator.
     */
    public static class Runner extends RecipeProvider.Runner {

        /**
         * Creates a new Runner for the recipe provider.
         *
         * @param output         the pack output
         * @param lookupProvider the registry lookup provider
         */
        public Runner(net.minecraft.data.PackOutput output, java.util.concurrent.CompletableFuture<net.minecraft.core.HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }

        @Override
        protected @NonNull RecipeProvider createRecipeProvider(net.minecraft.core.HolderLookup.@NonNull Provider provider, net.minecraft.data.recipes.@NonNull RecipeOutput output) {
            return new NestleRecipeProvider(provider, output);
        }

        @Override
        public String getName() {
            return "nestle";
        }
    }
}
