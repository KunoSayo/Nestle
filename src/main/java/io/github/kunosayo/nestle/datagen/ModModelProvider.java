package io.github.kunosayo.nestle.datagen;

import io.github.kunosayo.nestle.Nestle;
import io.github.kunosayo.nestle.init.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.client.renderer.item.properties.numeric.CompassAngle;
import net.minecraft.client.renderer.item.properties.numeric.CompassAngleState;
import net.minecraft.data.PackOutput;

import java.util.ArrayList;
import java.util.Optional;

public class ModModelProvider extends ModelProvider {

    public ModModelProvider(PackOutput output) {
        super(output, Nestle.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        NestleBlocksModelProvider.registerModels(blockModels, itemModels);
        itemModels.generateFlatItem(ModItems.NESTLE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.NESTLE_LEAD.get(), ModelTemplates.FLAT_ITEM);
        var list = new ArrayList<RangeSelectItemModel.Entry>();
        int cnt = 0;
        for (int i = 0; i < 32; i++) {
            if (i == 16) {
                continue;
            }
            ++cnt;

            list.add(new RangeSelectItemModel.Entry(cnt * 1.0f / 31.0f,
                    ItemModelUtils.plainModel(itemModels
                            .createFlatItemModel(ModItems.NESTLE_COMPASS.get(), String.format("_%02d", i), ModelTemplates.FLAT_ITEM))
            ));
        }
        itemModels.itemModelOutput.accept(ModItems.NESTLE_COMPASS.get(),
                new RangeSelectItemModel.Unbaked(
                        Optional.empty(),
                        new CompassAngle(true, CompassAngleState.CompassTarget.NONE),
                        1,
                        list,
                        Optional.of(list.getFirst().model())

                ));
    }


}
