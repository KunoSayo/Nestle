package io.github.kunosayo.nestle.datagen;

import io.github.kunosayo.nestle.Nestle;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class CompassModelProvider extends ItemModelProvider {

    public CompassModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Nestle.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (int i = 0; i < 32; i++) {
            if (i == 16) {
                continue;
            }
            var tex = String.format("nestle:item/nestle_compass_%02d", i);
            var name = String.format("nestle:compass_%02d", i);
            getBuilder(name)
                    .parent(getExistingFile(mcLoc("item/generated")))
                    .texture("layer0", tex);
        }
    }

}
