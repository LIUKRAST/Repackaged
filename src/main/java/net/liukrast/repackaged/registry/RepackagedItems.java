package net.liukrast.repackaged.registry;

import net.liukrast.deployer.lib.logistics.board.LogisticallyLinkedPanelBlockItem;
import net.liukrast.deployer.lib.logistics.board.PanelBlockItem;
import net.liukrast.deployer.lib.logistics.packager.CustomPackageStyle;
import net.liukrast.deployer.lib.logistics.packager.GenericPackageItem;
import net.liukrast.repackaged.Repackaged;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.stream.Stream;

public class RepackagedItems {
    private RepackagedItems() {}

    protected static final DeferredRegister.Items REGISTER = DeferredRegister.Items.createItems(Repackaged.CONSTANTS.getModId());

    public static final List<DeferredItem<GenericPackageItem>> STANDARD_BOTTLES;
    public static final List<DeferredItem<GenericPackageItem>> STANDARD_BATTERIES;
    public static final List<DeferredItem<GenericPackageItem>> RARE_BOTTLES;
    public static final List<DeferredItem<GenericPackageItem>> RARE_BATTERIES;

    public static Stream<DeferredItem<GenericPackageItem>> bottleStream() {
        return Stream.concat(STANDARD_BOTTLES.stream(), RARE_BOTTLES.stream());
    }

    public static final DeferredItem<PanelBlockItem> FLUID_GAUGE = REGISTER.register("fluid_gauge", () -> new LogisticallyLinkedPanelBlockItem(RepackagedPanels.FLUID::get, new Item.Properties()));
    public static final DeferredItem<PanelBlockItem> ENERGY_GAUGE = REGISTER.register("energy_gauge", () -> new LogisticallyLinkedPanelBlockItem(RepackagedPanels.ENERGY::get, new Item.Properties()));

    static {
        STANDARD_BOTTLES = RepackagedPackageStyles.BOTTLE_STYLES.stream()
                .filter(style -> !style.rare())
                .map(style -> REGISTER.register(style.getItemId().getPath(), () -> new GenericPackageItem(new Item.Properties().stacksTo(1), style, RepackagedStockInventoryTypes.FLUID::get, "item." + Repackaged.CONSTANTS.getModId() + ".bottle." + style.type())))
                .toList();

        STANDARD_BATTERIES = RepackagedPackageStyles.BATTERY_STYLES.stream()
                .filter(style -> !style.rare())
                .map(style -> REGISTER.register(style.getItemId().getPath(), () -> new GenericPackageItem(new Item.Properties().stacksTo(1), style, RepackagedStockInventoryTypes.ENERGY::get, "item." + Repackaged.CONSTANTS.getModId() + ".battery." + style.type())))
                .toList();

        RARE_BOTTLES = RepackagedPackageStyles.BOTTLE_STYLES.stream()
                .filter(CustomPackageStyle::rare)
                .map(style -> REGISTER.register(style.getItemId().getPath(), () -> new GenericPackageItem(new Item.Properties().stacksTo(1), style, RepackagedStockInventoryTypes.FLUID::get, "item." + Repackaged.CONSTANTS.getModId() + ".rare_bottle." + style.type())))
                .toList();

        RARE_BATTERIES = RepackagedPackageStyles.BATTERY_STYLES.stream()
                .filter(CustomPackageStyle::rare)
                .map(style -> REGISTER.register(style.getItemId().getPath(), () -> new GenericPackageItem(new Item.Properties().stacksTo(1), style, RepackagedStockInventoryTypes.ENERGY::get, "item." + Repackaged.CONSTANTS.getModId() + ".rare_battery." + style.type())))
                .toList();
    }

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
