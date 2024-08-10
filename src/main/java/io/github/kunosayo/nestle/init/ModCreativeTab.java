package io.github.kunosayo.nestle.init;

import io.github.kunosayo.nestle.Nestle;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Nestle.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TABS = TABS.register(Nestle.MOD_ID, () -> CreativeModeTab.builder()
            .title(Component.translatable("item_group." + Nestle.MOD_ID + ".name"))
            .icon(() -> ModItems.NESTLE.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ModItems.NESTLE_BLOCK_ITEM);
                output.accept(ModItems.NESTLE_RESISTANCE_BLOCK_ITEM);
                output.accept(ModItems.NESTLE.get());
                output.accept(ModItems.NESTLE_COMPASS.get());
                output.accept(ModItems.NESTLE_LEAD.get());
            }).build());
}