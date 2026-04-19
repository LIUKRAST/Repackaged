package net.liukrast.repackaged.compat;

import com.mrh0.createaddition.index.CABlocks;
import net.liukrast.compat.Compat;
import net.liukrast.repackaged.compat.caddition.AccumulatorUnpacking;
import net.liukrast.repackaged.registry.RepackagedStockInventoryTypes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Compat("createaddition")
public class CreateAdditionsCompat {
    public CreateAdditionsCompat(IEventBus eventBus) {
        eventBus.register(this);
    }

    @SubscribeEvent
    public void common(FMLCommonSetupEvent event) {
        RepackagedStockInventoryTypes.ENERGY.get().registry.register(CABlocks.MODULAR_ACCUMULATOR.get(), AccumulatorUnpacking.INSTANCE);
    }
}
