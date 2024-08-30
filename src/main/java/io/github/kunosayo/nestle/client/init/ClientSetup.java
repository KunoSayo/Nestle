package io.github.kunosayo.nestle.client.init;

import io.github.kunosayo.nestle.Nestle;
import io.github.kunosayo.nestle.client.input.NestleKey;
import io.github.kunosayo.nestle.client.render.NestleLeadEntityRenderer;
import io.github.kunosayo.nestle.entity.NestleLeadEntity;
import io.github.kunosayo.nestle.init.ModItems;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

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

    @SubscribeEvent
    public static void onRegisterClientExt(RegisterClientExtensionsEvent event) {
        ItemProperties.register(ModItems.NESTLE_COMPASS.get(), ResourceLocation.withDefaultNamespace("angle"), new CompassItemPropertyFunction((level, item, entity) -> {
            if (Nestle.clientNearestEntityVec == null) {
                return null;
            }
            return new GlobalPos(level.dimension(), Nestle.clientNearestEntityVec);
        }));

    }


}