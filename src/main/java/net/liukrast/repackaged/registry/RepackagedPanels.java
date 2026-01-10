package net.liukrast.repackage.registry;

import net.liukrast.deployer.lib.logistics.board.PanelType;
import net.liukrast.deployer.lib.registry.DeployerRegistries;
import net.liukrast.repackage.RepackagedConstants;
import net.liukrast.repackage.content.fluid.FluidPanelBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RepackagedPanels {
    private RepackagedPanels() {}
    private static final DeferredRegister<PanelType<?>> REGISTER = DeferredRegister.create(DeployerRegistries.PANEL, RepackagedConstants.MOD_ID);

    public static final DeferredHolder<PanelType<?>, PanelType<FluidPanelBehaviour>> FLUID = REGISTER.register("fluid", () -> new PanelType<>(FluidPanelBehaviour::new, FluidPanelBehaviour.class));

    public static void init(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
