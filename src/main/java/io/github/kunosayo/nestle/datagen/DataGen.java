package io.github.kunosayo.nestle.datagen;


import io.github.kunosayo.nestle.Nestle;
import net.minecraft.data.PackOutput;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = Nestle.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DataGen {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        var provider = event.getLookupProvider();

        // other providers here
        generator.addProvider(
                event.includeClient(),
                new CompassModelProvider(output, existingFileHelper)
        );

        generator.addProvider(event.includeClient(), new NestleBlocksModelProvider(output, existingFileHelper));

        generator.addProvider(event.includeServer(), new GenLootTable(output, provider));

        generator.addProvider(event.includeServer(), new NestleBlockTagsProvider(output, provider, existingFileHelper));
    }

}
