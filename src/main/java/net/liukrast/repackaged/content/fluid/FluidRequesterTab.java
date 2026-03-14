package net.liukrast.repackaged.content.fluid;

import com.simibubi.create.content.logistics.redstoneRequester.RedstoneRequesterMenu;
import net.liukrast.deployer.lib.logistics.packager.screen.StockRequesterPage;
import net.liukrast.repackaged.registry.RepackagedItems;
import net.liukrast.repackaged.registry.RepackagedStockInventoryTypes;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidRequesterTab extends StockRequesterPage<FluidStack> {
    public FluidRequesterTab(RedstoneRequesterMenu container) {
        super(container, RepackagedItems.STANDARD_BOTTLES.getFirst().asItem(), RepackagedStockInventoryTypes.FLUID.get());
    }
}
