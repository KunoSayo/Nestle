package io.github.kunosayo.nestle.datagen;

import io.github.kunosayo.nestle.Nestle;
import io.github.kunosayo.nestle.client.property.NestleBoundBoundProperty;
import io.github.kunosayo.nestle.client.property.NestleCompassAngle;
import io.github.kunosayo.nestle.init.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.renderer.item.ConditionalItemModel;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
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
        var angles = new float[]{
                0.000000f,
                0.015625f,
                0.046875f,
                0.078125f,
                0.109375f,
                0.140625f,
                0.171875f,
                0.203125f,
                0.234375f,
                0.265625f,
                0.296875f,
                0.328125f,
                0.359375f,
                0.390625f,
                0.421875f,
                0.453125f,
                0.484375f,
                0.515625f,
                0.546875f,
                0.578125f,
                0.609375f,
                0.640625f,
                0.671875f,
                0.703125f,
                0.734375f,
                0.765625f,
                0.796875f,
                0.828125f,
                0.859375f,
                0.890625f,
                0.921875f,
                0.953125f,
                0.984375f
        };
        int i = 16;
        var t16 = ItemModelUtils.plainModel(itemModels
                .createFlatItemModel(ModItems.NESTLE_COMPASS.get(), String.format("_%02d", i), ModelTemplates.FLAT_ITEM));
        for (float a : angles) {
            list.add(new RangeSelectItemModel.Entry(a, i == 16 ? t16 : ItemModelUtils.plainModel(itemModels
                    .createFlatItemModel(ModItems.NESTLE_COMPASS.get(), String.format("_%02d", i), ModelTemplates.FLAT_ITEM))));
            ++i;
            i %= 32;
        }
        itemModels.itemModelOutput.accept(ModItems.NESTLE_COMPASS.get(),
                new RangeSelectItemModel.Unbaked(
                        Optional.empty(),
                        new NestleCompassAngle(),
                        1,
                        list,
                        Optional.of(list.getFirst().model())
                ));

        // Generate nestle_bound item with two states: unbound (false) and bound (true)
        var unboundModel = ItemModelUtils.plainModel(itemModels.createFlatItemModel(ModItems.NESTLE_BOUND.get(), "_unbound", ModelTemplates.FLAT_ITEM));
        var boundModel = ItemModelUtils.plainModel(itemModels.createFlatItemModel(ModItems.NESTLE_BOUND.get(), "", ModelTemplates.FLAT_ITEM));
        itemModels.itemModelOutput.accept(ModItems.NESTLE_BOUND.get(),
                new ConditionalItemModel.Unbaked(
                        Optional.empty(),
                        new NestleBoundBoundProperty(),
                        boundModel,  // onTrue - when bound
                        unboundModel // onFalse - when unbound
                ));
    }


}
