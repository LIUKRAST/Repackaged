package net.liukrast.repackaged;

import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.liukrast.deployer.lib.DeployerConfig;
import net.liukrast.deployer.lib.event.PackageVisualEvent;
import net.liukrast.repackaged.content.fluid.*;
import net.liukrast.repackaged.registry.RepackagedBlockEntityTypes;
import net.liukrast.repackaged.registry.RepackagedDataComponents;
import net.liukrast.repackaged.registry.RepackagedItems;
import net.liukrast.repackaged.registry.RepackagedPartialModels;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.fluids.SimpleFluidContent;

@Mod(value = RepackagedConstants.MOD_ID, dist = Dist.CLIENT)
public class RepackagedClient {
    public RepackagedClient(IEventBus eventBus, ModContainer container) {
        eventBus.addListener(RepackagedBlockEntityTypes::registerRenderers);
        eventBus.addListener(RepackagedBlockEntityTypes::fmlClientSetup);
        container.registerConfig(ModConfig.Type.CLIENT, RepackagedConfig.Client.SPEC);
        RepackagedPartialModels.init();
        eventBus.register(this);
    }

    @SubscribeEvent
    private void registerClientExtensions(RegisterClientExtensionsEvent event) {
        RepackagedItems.bottleStream()
                .forEach(pack ->
                        event.registerItem(SimpleCustomRenderer.create(pack.get(), new BottleRenderer()), pack)
                );
    }

    @SubscribeEvent
    private void registerPackageVisuals(PackageVisualEvent event) {
        event.registerForChainConveyor(BottleChainVisual::new);
        event.registerForEntity(
                BottleVisual::new,
                box -> {
                    if(RepackagedItems.STANDARD_BOTTLES.stream().noneMatch(def -> box.box.is(def.get()))) return false;
                    return box.box.getOrDefault(RepackagedDataComponents.BOTTLE_CONTENTS, SimpleFluidContent.EMPTY).copy().isEmpty();
                }
        );
    }
}
