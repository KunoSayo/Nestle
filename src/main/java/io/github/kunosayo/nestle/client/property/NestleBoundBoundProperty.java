package io.github.kunosayo.nestle.client.property;

import com.mojang.serialization.MapCodec;
import io.github.kunosayo.nestle.Nestle;
import io.github.kunosayo.nestle.data.NestleBoundData;
import io.github.kunosayo.nestle.init.ModItems;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RegisterConditionalItemModelPropertyEvent;
import org.jspecify.annotations.Nullable;

/**
 * A ConditionalItemModelProperty that returns true when the nestle_bound item
 * has been bound to a player.
 * Uses Data Components (NestleBoundData) to check binding status.
 */
public class NestleBoundBoundProperty implements ConditionalItemModelProperty {

    public static final MapCodec<NestleBoundBoundProperty> MAP_CODEC = MapCodec.unit(new NestleBoundBoundProperty());

    public NestleBoundBoundProperty() {
    }

    @Override
    public boolean get(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed, ItemDisplayContext displayContext) {
        NestleBoundData boundData = itemStack.get(ModItems.NESTLE_BOUND_DATA.get());
        return boundData != null;
    }

    @Override
    public MapCodec<? extends ConditionalItemModelProperty> type() {
        return MAP_CODEC;
    }

    /**
     * Registers this property with the item model system.
     * Call this from RegisterConditionalItemModelPropertyEvent handler.
     */
    public static void register(RegisterConditionalItemModelPropertyEvent event) {
        event.register(
                Identifier.fromNamespaceAndPath(Nestle.MOD_ID, "is_bound"),
                MAP_CODEC
        );
    }
}
