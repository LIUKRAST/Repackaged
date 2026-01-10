package net.liukrast.repackage.registry;

import net.liukrast.deployer.lib.logistics.packager.SimplePackagerBlock;
import net.liukrast.repackage.RepackagedConstants;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;

public class RepackagedBlocks {
    private RepackagedBlocks() {}

    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(RepackagedConstants.MOD_ID);
    protected static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RepackagedConstants.MOD_ID);

    public static final DeferredBlock<SimplePackagerBlock> FLUID_PACKAGER = BLOCKS.register("fluid_packager", () -> new SimplePackagerBlock(BlockBehaviour.Properties.of()
            .noOcclusion()
            .isRedstoneConductor(($1, $2, $3) -> false)
            .mapColor(MapColor.TERRACOTTA_ORANGE)
            .sound(SoundType.COPPER_BULB),
            RepackagedBlockEntityTypes.FLUID_PACKAGER::get,
            Capabilities.FluidHandler.BLOCK,
            Optional.of(RepackagedPartialModels.FLUID_PACKAGER_TRAY)
    ));
    public static final DeferredBlock<SimplePackagerBlock> BATTERY_CHARGER = BLOCKS.register("battery_charger", () -> new SimplePackagerBlock(BlockBehaviour.Properties.of()
            .noOcclusion()
            .isRedstoneConductor(($1, $2, $3) -> false)
            .mapColor(MapColor.TERRACOTTA_YELLOW)
            .sound(SoundType.AMETHYST),
            RepackagedBlockEntityTypes.BATTERY_CHARGER::get,
            Capabilities.EnergyStorage.BLOCK
    ));

    static {
        ITEMS.register("fluid_packager", () -> new BlockItem(FLUID_PACKAGER.get(), new Item.Properties()));
        ITEMS.register("battery_charger", () -> new BlockItem(BATTERY_CHARGER.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}
