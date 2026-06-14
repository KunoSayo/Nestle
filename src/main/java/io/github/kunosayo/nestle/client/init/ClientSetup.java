package io.github.kunosayo.nestle.client.init;

import io.github.kunosayo.nestle.Nestle;
import io.github.kunosayo.nestle.client.input.NestleKey;
import io.github.kunosayo.nestle.client.property.NestleBoundBoundProperty;
import io.github.kunosayo.nestle.client.property.NestleCompassAngle;
import io.github.kunosayo.nestle.client.render.NestleLeadEntityRenderer;
import io.github.kunosayo.nestle.entity.NestleLeadNormalEntity;
import io.github.kunosayo.nestle.entity.NestleLeadPlayerEntity;
import io.github.kunosayo.nestle.init.ModItems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterConditionalItemModelPropertyEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = Nestle.MOD_ID)
public class ClientSetup implements IModBusEvent {
    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(NestleKey.NESTLE_KEY);
    }

    @SubscribeEvent
    public static void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(NestleLeadPlayerEntity.ENTITY_TYPE, NestleLeadEntityRenderer::new);
        event.registerEntityRenderer(NestleLeadNormalEntity.ENTITY_TYPE, NestleLeadEntityRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterRangeProperties(RegisterRangeSelectItemModelPropertyEvent event) {
        NestleCompassAngle.register(event);
    }

    @SubscribeEvent
    public static void onRegisterConditionalProperties(RegisterConditionalItemModelPropertyEvent event) {
        NestleBoundBoundProperty.register(event);
    }
}
