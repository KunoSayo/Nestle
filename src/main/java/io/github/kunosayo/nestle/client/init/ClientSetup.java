package io.github.kunosayo.nestle.client.init;

import io.github.kunosayo.nestle.Nestle;
import io.github.kunosayo.nestle.client.input.NestleKey;
import io.github.kunosayo.nestle.client.render.NestleLeadEntityRenderer;
import io.github.kunosayo.nestle.entity.NestleLeadEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = Nestle.MOD_ID)
public class ClientSetup {
    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(NestleKey.NESTLE_KEY);
    }

    @SubscribeEvent
    public static void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(NestleLeadEntity.ENTITY_TYPE, NestleLeadEntityRenderer::new);
    }
}