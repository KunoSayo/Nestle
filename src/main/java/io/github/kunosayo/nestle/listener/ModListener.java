package io.github.kunosayo.nestle.listener;

import io.github.kunosayo.nestle.Nestle;
import io.github.kunosayo.nestle.config.NestleConfig;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;

@EventBusSubscriber(modid = Nestle.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModListener {
    @SubscribeEvent
    public static void onLoading(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == NestleConfig.NESTLE_CONFIG.getRight()) {
            NestleConfig.NESTLE_CONFIG.getLeft().update();
        }
    }

    @SubscribeEvent
    public static void onReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == NestleConfig.NESTLE_CONFIG.getRight()) {
            NestleConfig.NESTLE_CONFIG.getLeft().update();
        }
    }

}
