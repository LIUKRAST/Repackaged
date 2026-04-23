package net.liukrast.repackaged.datagen.loot;

import net.liukrast.repackaged.Repackaged;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class RepackagedBlockLootSubProvider extends BlockLootSubProvider {
    public RepackagedBlockLootSubProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        Repackaged.CONSTANTS
                .getElements(BuiltInRegistries.BLOCK)
                .forEach(this::dropSelf);
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return BuiltInRegistries.BLOCK.stream()
                .filter(b -> BuiltInRegistries.BLOCK.getKey(b).getNamespace().equals(Repackaged.CONSTANTS.getModId()))
                .toList();
    }
}
