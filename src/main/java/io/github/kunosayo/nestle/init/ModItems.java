package io.github.kunosayo.nestle.init;

import io.github.kunosayo.nestle.Nestle;
import io.github.kunosayo.nestle.data.NestleBoundData;
import io.github.kunosayo.nestle.item.NestleBoundItem;
import io.github.kunosayo.nestle.item.NestleCompassItem;
import io.github.kunosayo.nestle.item.NestleItem;
import io.github.kunosayo.nestle.item.NestleLeadItem;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Nestle.MOD_ID);
    public static DeferredItem<BlockItem> NESTLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.NESTLE_BLOCK);
    public static DeferredItem<BlockItem> NESTLE_RESISTANCE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.NESTLE_RESISTANCE_BLOCK);
    public static DeferredItem<NestleItem> NESTLE = ITEMS.register("nestle", NestleItem::new);
    public static DeferredItem<Item> NESTLE_COMPASS = ITEMS.register("nestle_compass", NestleCompassItem::new);
    public static DeferredItem<Item> NESTLE_LEAD = ITEMS.register("nestle_lead", NestleLeadItem::new);
    public static DeferredItem<Item> NESTLE_BOUND = ITEMS.register("nestle_bound", NestleBoundItem::new);

    // Data Components
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Nestle.MOD_ID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<NestleBoundData>> NESTLE_BOUND_DATA =
            DATA_COMPONENTS.registerComponentType("nestle_bound_data",
                    builder -> builder
                            .persistent(NestleBoundData.CODEC)
                            .networkSynchronized(NestleBoundData.STREAM_CODEC)
            );
}
