package net.liukrast.repackaged.datagen;

import net.liukrast.repackaged.RepackagedConstants;
import net.liukrast.repackaged.registry.RepackagedItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import static net.liukrast.deployer.lib.helper.MinecraftHelpers.ModelProvider.Items.createGauge;

public class RepackagedItemModelProvider extends ItemModelProvider {
    public RepackagedItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, RepackagedConstants.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        createGauge(this, RepackagedItems.FLUID_GAUGE.get());
    }


}
