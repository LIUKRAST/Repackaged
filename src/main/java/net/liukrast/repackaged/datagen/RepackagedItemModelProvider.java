package net.liukrast.repackaged.datagen;

import net.liukrast.repackaged.Repackaged;
import net.liukrast.repackaged.registry.RepackagedItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import static net.liukrast.deployer.lib.helper.MinecraftHelpers.ModelProvider.Items.createPanel;

public class RepackagedItemModelProvider extends ItemModelProvider {
    public RepackagedItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Repackaged.CONSTANTS.getModId(), existingFileHelper);
    }

    @Override
    protected void registerModels() {
        createPanel(this, RepackagedItems.FLUID_GAUGE.get());
        createPanel(this, RepackagedItems.ENERGY_GAUGE.get());
    }


}
