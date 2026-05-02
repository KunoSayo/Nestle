package io.github.kunosayo.nestle.client.init;

import io.github.kunosayo.nestle.Nestle;
import io.github.kunosayo.nestle.client.input.NestleKey;
import io.github.kunosayo.nestle.client.render.NestleLeadEntityRenderer;
import io.github.kunosayo.nestle.entity.NestleLeadNormalEntity;
import io.github.kunosayo.nestle.entity.NestleLeadPlayerEntity;
import io.github.kunosayo.nestle.init.ModItems;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = Nestle.MOD_ID)
public class ClientSetup {
    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(NestleKey.NESTLE_KEY);
    }

    @SubscribeEvent
    public static void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(NestleLeadPlayerEntity.ENTITY_TYPE, NestleLeadEntityRenderer::new);
        event.registerEntityRenderer(NestleLeadNormalEntity.ENTITY_TYPE, NestleLeadEntityRenderer::new);
    }


}