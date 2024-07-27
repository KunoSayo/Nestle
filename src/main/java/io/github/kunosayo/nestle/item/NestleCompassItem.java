package io.github.kunosayo.nestle.item;

import io.github.kunosayo.nestle.Nestle;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class NestleCompassItem extends Item {
    public NestleCompassItem() {
        super(new Properties());
        ItemProperties.register(this, ResourceLocation.withDefaultNamespace("angle"), new CompassItemPropertyFunction((level, item, entity) -> {
            if (Nestle.clientNearestEntityVec == null) {
                return null;
            }
            return new GlobalPos(level.dimension(), Nestle.clientNearestEntityVec);
        }));
    }
}
