package net.liukrast.repackaged.registry;

import com.simibubi.create.content.logistics.packager.PackagerRenderer;
import com.simibubi.create.content.logistics.packager.PackagerVisual;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import net.liukrast.repackaged.RepackagedConstants;
import net.liukrast.repackaged.content.energy.BatteryChargerBlockEntity;
import net.liukrast.repackaged.content.fluid.FluidPackagerBlockEntity;
import net.liukrast.repackaged.content.logistics.PackageShelfBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RepackagedBlockEntityTypes {
    private RepackagedBlockEntityTypes() {}

    private static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, RepackagedConstants.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidPackagerBlockEntity>> FLUID_PACKAGER = REGISTER.register("fluid_packager", () -> BlockEntityType.Builder.of(FluidPackagerBlockEntity::new, RepackagedBlocks.FLUID_PACKAGER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BatteryChargerBlockEntity>> BATTERY_CHARGER = REGISTER.register("battery_charger", () -> BlockEntityType.Builder.of(BatteryChargerBlockEntity::new, RepackagedBlocks.BATTERY_CHARGER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PackageShelfBlockEntity>> PACKAGE_SHELF = REGISTER.register("package_shelf", () -> BlockEntityType.Builder.of(PackageShelfBlockEntity::new, RepackagedBlocks.PACKAGE_SHELF.get()).build(null));

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(FLUID_PACKAGER.get(), PackagerRenderer::new);
        event.registerBlockEntityRenderer(BATTERY_CHARGER.get(), PackagerRenderer::new);
        event.registerBlockEntityRenderer(PACKAGE_SHELF.get(), PackagerRenderer::new);
    }

    @OnlyIn(Dist.CLIENT)
    public static void fmlClientSetup(FMLClientSetupEvent ignored) {
        SimpleBlockEntityVisualizer.builder(FLUID_PACKAGER.get())
                .factory(PackagerVisual::new)
                .skipVanillaRender(be -> false)
                .apply();
        SimpleBlockEntityVisualizer.builder(BATTERY_CHARGER.get())
                .factory(PackagerVisual::new)
                .skipVanillaRender(be -> false)
                .apply();
        SimpleBlockEntityVisualizer.builder(PACKAGE_SHELF.get())
                .factory(PackagerVisual::new)
                .skipVanillaRender(be -> false)
                .apply();
    }
}
