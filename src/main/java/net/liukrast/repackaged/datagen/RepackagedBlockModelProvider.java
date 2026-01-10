package net.liukrast.repackaged.datagen;

import net.liukrast.deployer.lib.helper.MinecraftHelpers;
import net.liukrast.repackaged.RepackagedConstants;
import net.liukrast.repackaged.registry.RepackagedItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class RepackagedBlockModelProvider extends BlockModelProvider {
    public RepackagedBlockModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, RepackagedConstants.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        MinecraftHelpers.ModelProvider.createPanel(this, RepackagedItems.FLUID_GAUGE.get());
    }
}
