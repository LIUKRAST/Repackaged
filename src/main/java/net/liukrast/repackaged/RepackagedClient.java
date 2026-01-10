package net.liukrast.repackage;

import net.liukrast.repackage.registry.RepackagedBlockEntityTypes;
import net.liukrast.repackage.registry.RepackagedPartialModels;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = RepackagedConstants.MOD_ID, dist = Dist.CLIENT)
public class RepackagedClient {
    public RepackagedClient(IEventBus eventBus, ModContainer container) {
        //NeoForge.EVENT_BUS.register(this);
        eventBus.addListener(RepackagedBlockEntityTypes::registerRenderers);
        eventBus.addListener(RepackagedBlockEntityTypes::fmlClientSetup);
        RepackagedPartialModels.init();
    }
}
