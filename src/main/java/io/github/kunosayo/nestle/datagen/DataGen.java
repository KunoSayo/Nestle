package io.github.kunosayo.nestle.datagen;


import io.github.kunosayo.nestle.Nestle;
import net.minecraft.data.PackOutput;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = Nestle.MOD_ID, value = Dist.CLIENT)
public class DataGen implements IModBusEvent {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        var generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        var provider = event.getLookupProvider();

        generator.addProvider(true, new ModModelProvider(output));

        generator.addProvider(true, new GenLootTable(output, provider));

        generator.addProvider(true, new NestleBlockTagsProvider(output, provider));

        generator.addProvider(true, new NestleAdvancementProvider(output, provider));
    }

}
