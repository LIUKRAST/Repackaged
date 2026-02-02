package net.liukrast.repackaged;

import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.liukrast.deployer.lib.event.ChainConveyorPackageRenderExtensionEvent;
import net.liukrast.deployer.lib.event.PackageRenderExtensionEvent;
import net.liukrast.repackaged.content.fluid.BottleRenderer;
import net.liukrast.repackaged.registry.RepackagedBlockEntityTypes;
import net.liukrast.repackaged.registry.RepackagedItems;
import net.liukrast.repackaged.registry.RepackagedPartialModels;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.stream.Stream;

@Mod(value = RepackagedConstants.MOD_ID, dist = Dist.CLIENT)
public class RepackagedClient {
    public RepackagedClient(IEventBus eventBus, ModContainer ignored) {
        eventBus.addListener(RepackagedBlockEntityTypes::registerRenderers);
        eventBus.addListener(RepackagedBlockEntityTypes::fmlClientSetup);
        RepackagedPartialModels.init();
        eventBus.register(this);
    }

    @SubscribeEvent
    private void registerClientExtensions(RegisterClientExtensionsEvent event) {
        Stream.concat(RepackagedItems.STANDARD_BOTTLES.stream(), RepackagedItems.RARE_BOTTLES.stream())
                .forEach(pack ->
                        event.registerItem(SimpleCustomRenderer.create(pack.get(), new BottleRenderer()), pack)
                );
    }

    @SubscribeEvent
    private void packageRenderExtension(PackageRenderExtensionEvent event) {
        event.registerExtension(
                (entity, yaw, pt, ms, buffer, light) ->
                        BottleRenderer.render(entity.box, ms, buffer, light),
                Stream.concat(RepackagedItems.STANDARD_BOTTLES.stream(), RepackagedItems.RARE_BOTTLES.stream())
                        .map(DeferredHolder::get).toArray(Item[]::new)
        );
    }

    //@SubscribeEvent
    private void chainConveyorRenderExtension(ChainConveyorPackageRenderExtensionEvent event) {
        event.registerExtension((be, box, pt, ms, buffer, light, overlay) -> {
            BottleRenderer.render(box.item, ms, buffer, light);
        }, Stream.concat(RepackagedItems.STANDARD_BOTTLES.stream(), RepackagedItems.RARE_BOTTLES.stream()).map(DeferredHolder::get).toArray(Item[]::new));
    }
}
