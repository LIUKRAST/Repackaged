package net.liukrast.repackaged;

import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.createmod.ponder.foundation.PonderIndex;
import net.liukrast.deployer.lib.helper.ClientRegisterHelpers;
import net.liukrast.repackaged.content.energy.EnergyGaugeSlot;
import net.liukrast.repackaged.content.energy.EnergyRequesterTabScreen;
import net.liukrast.repackaged.content.energy.EnergyTabScreen;
import net.liukrast.repackaged.content.fluid.*;
import net.liukrast.repackaged.content.logistics.PackageShelfBehaviour;
import net.liukrast.repackaged.registry.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.SimpleFluidContent;

@Mod(value = "repackaged", dist = Dist.CLIENT)
public class RepackagedClient {
    public RepackagedClient(IEventBus eventBus, ModContainer container) {
        eventBus.addListener(RepackagedBlockEntityTypes::registerRenderers);
        eventBus.addListener(RepackagedBlockEntityTypes::fmlClientSetup);
        container.registerConfig(ModConfig.Type.CLIENT, RepackagedConfig.Client.SPEC);
        RepackagedPartialModels.init();
        eventBus.register(this);
        NeoForge.EVENT_BUS.addListener(this::onTickPre);
        NeoForge.EVENT_BUS.addListener(this::onTickPost);
    }

    private void onTickPre(ClientTickEvent.Pre event) {
        PackageShelfBehaviour.tick();
    }

    private void onTickPost(ClientTickEvent.Post post) {
        PackageShelfBehaviour.tick();
    }

    @SubscribeEvent
    private void registerClientExtensions(RegisterClientExtensionsEvent event) {
        RepackagedItems.bottleStream()
                .forEach(pack ->
                        event.registerItem(SimpleCustomRenderer.create(pack.get(), new BottleRenderer()), pack)
                );
    }

    @SubscribeEvent
    private void fMLClientSetup(FMLClientSetupEvent event) {
        ClientRegisterHelpers.registerPackageVisual4ChainConveyor(BottleChainVisual::new);
        ClientRegisterHelpers.registerPackageVisual4Entity(BottleVisual::new,
                box -> {
                    if(RepackagedItems.STANDARD_BOTTLES.stream().noneMatch(def -> box.box.is(def.get()))) return false;
                    var fluid = box.box.getOrDefault(RepackagedDataComponents.BOTTLE_CONTENTS, SimpleFluidContent.EMPTY);
                    return !fluid.isEmpty();
                });
        ClientRegisterHelpers.registerStockKeeperTab(FluidTabScreen::new);
        ClientRegisterHelpers.registerStockKeeperTab(EnergyTabScreen::new);
        ClientRegisterHelpers.registerRedstoneRequesterTab(RepackagedStockInventoryTypes.FLUID.get(), FluidRequesterTabScreen::new);
        ClientRegisterHelpers.registerRedstoneRequesterTab(RepackagedStockInventoryTypes.ENERGY.get(), EnergyRequesterTabScreen::new);
        ClientRegisterHelpers.registerGaugeSlot(RepackagedPanels.FLUID.get(), FluidGaugeSlot::new);
        ClientRegisterHelpers.registerGaugeSlot(RepackagedPanels.ENERGY.get(), EnergyGaugeSlot::new);

        PonderIndex.addPlugin(new RepackagedPonderPlugin());
    }
}
