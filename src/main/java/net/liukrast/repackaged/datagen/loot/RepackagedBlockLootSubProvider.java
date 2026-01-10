package net.liukrast.repackaged.datagen.loot;

import net.liukrast.repackaged.RepackagedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class RepackagedBlockLootSubProvider extends BlockLootSubProvider {
    public RepackagedBlockLootSubProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        //HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        List<Predicate<Block>> exceptions = List.of(
        );
        RepackagedConstants.getElements(BuiltInRegistries.BLOCK).filter(b -> b.getLootTable() != BuiltInLootTables.EMPTY && exceptions.stream().allMatch(k -> k.test(b))).forEach(this::dropSelf);
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return BuiltInRegistries.BLOCK.stream()
                .filter(b -> BuiltInRegistries.BLOCK.getKey(b).getNamespace().equals(RepackagedConstants.MOD_ID))
                .toList();
    }
}
