package io.github.kunosayo.nestle.init;

import io.github.kunosayo.nestle.Nestle;
import io.github.kunosayo.nestle.item.NestleCompassItem;
import io.github.kunosayo.nestle.item.NestleItem;
import io.github.kunosayo.nestle.item.NestleLeadItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Nestle.MOD_ID);
    public static DeferredItem<BlockItem> NESTLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.NESTLE_BLOCK);
    public static DeferredItem<NestleItem> NESTLE = ITEMS.register("nestle", NestleItem::new);
    public static DeferredItem<Item> NESTLE_COMPASS = ITEMS.register("nestle_compass", NestleCompassItem::new);
    public static DeferredItem<Item> NESTLE_LEAD = ITEMS.register("nestle_lead", NestleLeadItem::new);


}
