package net.liukrast.repackage.content.energy;

import net.liukrast.deployer.lib.logistics.packager.AbstractInventorySummary;
import net.liukrast.repackage.registry.RepackagedStockInventoryTypes;
import net.neoforged.neoforge.common.util.Lazy;

public class EnergyInventorySummary extends AbstractInventorySummary<Energy, EnergyStack> {

    public static final Lazy<EnergyInventorySummary> EMPTY = Lazy.of(EnergyInventorySummary::new);

    public EnergyInventorySummary() {
        super(RepackagedStockInventoryTypes.ENERGY.get());
    }
}
