package net.liukrast.repackage.registry;

import com.simibubi.create.content.logistics.packager.PackagerRenderer;
import com.simibubi.create.content.logistics.packager.PackagerVisual;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import net.liukrast.repackage.RepackagedConstants;
import net.liukrast.repackage.content.energy.BatteryChargerBlockEntity;
import net.liukrast.repackage.content.fluid.FluidPackagerBlockEntity;
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

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, RepackagedConstants.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidPackagerBlockEntity>> FLUID_PACKAGER = BLOCK_ENTITY_TYPES.register("fluid_packager", () -> BlockEntityType.Builder.of(FluidPackagerBlockEntity::new, RepackagedBlocks.FLUID_PACKAGER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BatteryChargerBlockEntity>> BATTERY_CHARGER = BLOCK_ENTITY_TYPES.register("battery_charger", () -> BlockEntityType.Builder.of(BatteryChargerBlockEntity::new, RepackagedBlocks.BATTERY_CHARGER.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(FLUID_PACKAGER.get(), PackagerRenderer::new);
        event.registerBlockEntityRenderer(BATTERY_CHARGER.get(), PackagerRenderer::new);
    }

    @OnlyIn(Dist.CLIENT)
    public static void fmlClientSetup(FMLClientSetupEvent event) {
        SimpleBlockEntityVisualizer.builder(FLUID_PACKAGER.get())
                .factory(PackagerVisual::new)
                .skipVanillaRender(be -> false)
                .apply();
        SimpleBlockEntityVisualizer.builder(BATTERY_CHARGER.get())
                .factory(PackagerVisual::new)
                .skipVanillaRender(be -> false)
                .apply();
    }
}
