package net.liukrast.repackaged.content.fluid;

import net.liukrast.deployer.lib.logistics.packager.AbstractInventorySummary;
import net.liukrast.repackaged.registry.RepackagedStockInventoryTypes;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidInventorySummary extends AbstractInventorySummary<Fluid, FluidStack> {

    public static final Lazy<FluidInventorySummary> EMPTY = Lazy.of(FluidInventorySummary::new);

    public FluidInventorySummary() {
        super(RepackagedStockInventoryTypes.FLUID.get());
    }
}
