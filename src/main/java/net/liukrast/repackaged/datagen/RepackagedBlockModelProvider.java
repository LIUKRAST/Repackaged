package net.liukrast.repackaged.datagen;

import net.liukrast.repackaged.Repackaged;
import net.liukrast.repackaged.registry.RepackagedItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import static net.liukrast.deployer.lib.helper.MinecraftHelpers.ModelProvider.Blocks.createPanel;

public class RepackagedBlockModelProvider extends BlockModelProvider {
    public RepackagedBlockModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Repackaged.CONSTANTS.getModId(), existingFileHelper);
    }

    @Override
    protected void registerModels() {
        createPanel(this, RepackagedItems.FLUID_GAUGE.get());
        createPanel(this, RepackagedItems.ENERGY_GAUGE.get());
    }
}
