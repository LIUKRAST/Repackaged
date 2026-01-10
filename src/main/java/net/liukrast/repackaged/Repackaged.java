package net.liukrast.repackaged;

import net.liukrast.repackaged.content.energy.EnergyStockInventoryType;
import net.liukrast.repackaged.datagen.RepackagedBlockModelProvider;
import net.liukrast.repackaged.datagen.RepackagedDatapackBuiltinEntriesProvider;
import net.liukrast.repackaged.datagen.RepackagedItemModelProvider;
import net.liukrast.repackaged.datagen.RepackagedLanguageProvider;
import net.liukrast.repackaged.datagen.loot.RepackagedBlockLootSubProvider;
import net.liukrast.repackaged.datagen.tags.RepackagedBlockTagsProvider;
import net.liukrast.repackaged.registry.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.energy.ComponentEnergyStorage;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Mod(RepackagedConstants.MOD_ID)
public class Repackaged {
    public Repackaged(IEventBus eventBus) {
        RepackagedBlockEntityTypes.register(eventBus);
        RepackagedBlocks.register(eventBus);
        RepackagedDataComponents.register(eventBus);
        RepackagedItems.register(eventBus);
        RepackagedStockInventoryTypes.register(eventBus);
        eventBus.register(this);
        RepackagedPackageStyles.init();
        RepackagedCreativeTabs.init(eventBus);
        RepackagedPanels.init(eventBus);
    }

    @SubscribeEvent
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                RepackagedBlockEntityTypes.FLUID_PACKAGER.get(),
                (be, context) -> be.inventory
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                RepackagedBlockEntityTypes.BATTERY_CHARGER.get(),
                (be, context) -> be.inventory
        );

        //TODO: Remove this to block other mods from extracting energy directly from batteries,
        // without a battery charger
        event.registerItem(
                Capabilities.EnergyStorage.ITEM,
                (stack, $) -> new ComponentEnergyStorage(stack, RepackagedDataComponents.BATTERY_CONTENTS.get(), EnergyStockInventoryType.MAX_BATTERY_ENERGY, EnergyStockInventoryType.MAX_BATTERY_ENERGY, EnergyStockInventoryType.MAX_BATTERY_ENERGY),
                Stream.concat(RepackagedItems.RARE_BATTERIES.stream(), RepackagedItems.STANDARD_BATTERIES.stream()).toArray(DeferredItem[]::new)
        );
    }

    @SubscribeEvent
    public void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        generator.addProvider(event.includeClient(), new RepackagedLanguageProvider(packOutput));
        generator.addProvider(event.includeClient(), new RepackagedBlockModelProvider(packOutput, helper));
        generator.addProvider(event.includeClient(), new RepackagedItemModelProvider(packOutput, helper));
        var blockTagProvider = new RepackagedBlockTagsProvider(packOutput, lookupProvider, helper);
        generator.addProvider(event.includeServer(), blockTagProvider);
        //generator.addProvider(event.includeServer(), new RepackagedItem)
        var dataPackProvider = new RepackagedDatapackBuiltinEntriesProvider(packOutput, lookupProvider);
        generator.addProvider(event.includeServer(), dataPackProvider);
        generator.addProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(),
                List.of(
                        new LootTableProvider.SubProviderEntry(RepackagedBlockLootSubProvider::new, LootContextParamSets.BLOCK)
                ), lookupProvider));
        //generator.addProvider(event.includeServer(), )
    }
}
