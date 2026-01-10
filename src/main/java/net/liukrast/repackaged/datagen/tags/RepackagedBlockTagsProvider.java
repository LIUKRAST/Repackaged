package net.liukrast.repackaged.datagen.tags;

import net.liukrast.repackaged.RepackagedConstants;
import net.liukrast.repackaged.registry.RepackagedBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NonnullDefault;

import java.util.concurrent.CompletableFuture;

@NonnullDefault
public class RepackagedBlockTagsProvider extends BlockTagsProvider {
    public RepackagedBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, RepackagedConstants.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        var axe = tag(BlockTags.MINEABLE_WITH_AXE);
        var pick = tag(BlockTags.MINEABLE_WITH_PICKAXE);
        pick.add(RepackagedBlocks.BATTERY_CHARGER.get());
        pick.add(RepackagedBlocks.FLUID_PACKAGER.get());
    }
}
