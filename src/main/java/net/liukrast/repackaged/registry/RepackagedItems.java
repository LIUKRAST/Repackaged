package net.liukrast.repackaged.registry;

import net.liukrast.deployer.lib.logistics.board.PanelBlockItem;
import net.liukrast.deployer.lib.logistics.packager.CustomPackageStyle;
import net.liukrast.deployer.lib.logistics.packager.GenericPackageItem;
import net.liukrast.repackaged.RepackagedConstants;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class RepackagedItems {
    private RepackagedItems() {}

    protected static final DeferredRegister.Items REGISTER = DeferredRegister.Items.createItems(RepackagedConstants.MOD_ID);

    public static final List<DeferredItem<GenericPackageItem>> STANDARD_BOTTLES;
    public static final List<DeferredItem<GenericPackageItem>> STANDARD_BATTERIES;
    public static final List<DeferredItem<GenericPackageItem>> RARE_BOTTLES;
    public static final List<DeferredItem<GenericPackageItem>> RARE_BATTERIES;

    public static final DeferredItem<PanelBlockItem> FLUID_GAUGE = REGISTER.register("fluid_gauge", () -> new PanelBlockItem(RepackagedPanels.FLUID::get, new Item.Properties()));

    static {
        STANDARD_BOTTLES = RepackagedPackageStyles.BOTTLE_STYLES.stream()
                .filter(style -> !style.rare())
                .map(style -> REGISTER.register(style.getItemId().getPath(), () -> new GenericPackageItem(new Item.Properties().stacksTo(1), style, RepackagedStockInventoryTypes.FLUID::get, "item." + RepackagedConstants.MOD_ID + ".bottle")))
                .toList();

        STANDARD_BATTERIES = RepackagedPackageStyles.BATTERY_STYLES.stream()
                .filter(style -> !style.rare())
                .map(style -> REGISTER.register(style.getItemId().getPath(), () -> new GenericPackageItem(new Item.Properties().stacksTo(1), style, RepackagedStockInventoryTypes.ENERGY::get, "item." + RepackagedConstants.MOD_ID + ".battery")))
                .toList();

        RARE_BOTTLES = RepackagedPackageStyles.BOTTLE_STYLES.stream()
                .filter(CustomPackageStyle::rare)
                .map(style -> REGISTER.register(style.getItemId().getPath(), () -> new GenericPackageItem(new Item.Properties().stacksTo(1), style, RepackagedStockInventoryTypes.FLUID::get, "item." + RepackagedConstants.MOD_ID + ".rare_bottle")))
                .toList();

        RARE_BATTERIES = RepackagedPackageStyles.BATTERY_STYLES.stream()
                .filter(CustomPackageStyle::rare)
                .map(style -> REGISTER.register(style.getItemId().getPath(), () -> new GenericPackageItem(new Item.Properties().stacksTo(1), style, RepackagedStockInventoryTypes.ENERGY::get, "item." + RepackagedConstants.MOD_ID + ".rare_battery")))
                .toList();
    }

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
