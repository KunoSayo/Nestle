package io.github.kunosayo.nestle.listener;

import io.github.kunosayo.nestle.Nestle;
import io.github.kunosayo.nestle.config.NestleConfig;
import io.github.kunosayo.nestle.network.SyncNestleDataPacket;
import io.github.kunosayo.nestle.network.SyncNestleValuePacket;
import io.github.kunosayo.nestle.network.UpdateNestleValuePacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Nestle.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModListener {
    public static final String NETWORK_VERSION = "1.0.0";

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

    @SubscribeEvent
    public static void registerPayload(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(NETWORK_VERSION);
        registrar.playToClient(SyncNestleDataPacket.NETWORK_TYPE, SyncNestleDataPacket.STREAM_CODEC, SyncNestleDataPacket::clientHandler);
        registrar.playToClient(UpdateNestleValuePacket.NETWORK_TYPE, UpdateNestleValuePacket.STREAM_CODEC, UpdateNestleValuePacket::clientHandler);
        registrar.playToClient(SyncNestleValuePacket.NETWORK_TYPE, SyncNestleValuePacket.STREAM_CODEC, SyncNestleValuePacket::clientValueHandler);
    }

}
