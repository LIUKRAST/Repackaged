package net.liukrast.repackaged.registry;

import com.simibubi.create.foundation.data.SharedProperties;
import net.liukrast.deployer.lib.logistics.packager.SimplePackagerBlock;
import net.liukrast.repackaged.Repackaged;
import net.liukrast.repackaged.content.logistics.PackageShelfBlock;
import net.liukrast.repackaged.content.logistics.PackagerConnectorBlock;
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

    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Repackaged.CONSTANTS.getModId());
    protected static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Repackaged.CONSTANTS.getModId());

    public static final DeferredBlock<SimplePackagerBlock> FLUID_PACKAGER = BLOCKS.register("fluid_packager", () -> new SimplePackagerBlock(BlockBehaviour.Properties.ofFullCopy(SharedProperties.softMetal())
            .noOcclusion()
            .isRedstoneConductor(($1, $2, $3) -> false)
            .mapColor(MapColor.TERRACOTTA_ORANGE)
            .sound(SoundType.COPPER_BULB),
            RepackagedBlockEntityTypes.FLUID_PACKAGER::get,
            Capabilities.FluidHandler.BLOCK,
            Optional.of(RepackagedPartialModels.FLUID_PACKAGER_TRAY)
    ));
    public static final DeferredBlock<SimplePackagerBlock> BATTERY_CHARGER = BLOCKS.register("battery_charger", () -> new SimplePackagerBlock(BlockBehaviour.Properties.ofFullCopy(SharedProperties.softMetal())
            .noOcclusion()
            .isRedstoneConductor(($1, $2, $3) -> false)
            .mapColor(MapColor.TERRACOTTA_YELLOW)
            .sound(SoundType.AMETHYST),
            RepackagedBlockEntityTypes.BATTERY_CHARGER::get,
            Capabilities.EnergyStorage.BLOCK
    ));

    public static final DeferredBlock<PackageShelfBlock> PACKAGE_SHELF = BLOCKS.register("package_shelf", () -> new PackageShelfBlock(BlockBehaviour.Properties.ofFullCopy(SharedProperties.softMetal())
            .noOcclusion()
            .isRedstoneConductor(($1, $2, $3) -> false)
            .mapColor(MapColor.TERRACOTTA_BLUE)
            .sound(SoundType.NETHERITE_BLOCK)
    ));

    public static final DeferredBlock<PackagerConnectorBlock> PACKAGER_CONNECTOR = BLOCKS.register("packager_connector", () -> new PackagerConnectorBlock(BlockBehaviour.Properties.ofFullCopy(SharedProperties.softMetal())
            .noOcclusion()
            .isRedstoneConductor(($1, $2, $3) -> false)
            .mapColor(MapColor.TERRACOTTA_BLUE)
            .sound(SoundType.NETHERITE_BLOCK)
    ));

    static {
        ITEMS.register("fluid_packager", () -> new BlockItem(FLUID_PACKAGER.get(), new Item.Properties()));
        ITEMS.register("battery_charger", () -> new BlockItem(BATTERY_CHARGER.get(), new Item.Properties()));
        ITEMS.register("package_shelf", () -> Repackaged.CONSTANTS.wrapWithShiftSummary(new BlockItem(PACKAGE_SHELF.get(), new Item.Properties())));
        ITEMS.register("packager_connector", () -> new BlockItem(PACKAGER_CONNECTOR.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }

}
