package net.liukrast.repackage;

import net.liukrast.repackage.content.energy.EnergyStockInventoryType;
import net.liukrast.repackage.registry.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.ComponentEnergyStorage;
import net.neoforged.neoforge.registries.DeferredItem;

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
}
