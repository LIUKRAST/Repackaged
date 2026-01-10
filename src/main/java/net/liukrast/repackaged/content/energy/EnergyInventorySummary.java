package net.liukrast.repackaged.content.energy;

import net.liukrast.deployer.lib.logistics.packager.AbstractInventorySummary;
import net.liukrast.repackaged.registry.RepackagedStockInventoryTypes;
import net.neoforged.neoforge.common.util.Lazy;

public class EnergyInventorySummary extends AbstractInventorySummary<Energy, EnergyStack> {

    public static final Lazy<EnergyInventorySummary> EMPTY = Lazy.of(EnergyInventorySummary::new);

    public EnergyInventorySummary() {
        super(RepackagedStockInventoryTypes.ENERGY.get());
    }
}
