package net.liukrast.repackaged.datagen;

import net.liukrast.deployer.lib.helper.datagen.DeployerLanguageProviderImpl;
import net.liukrast.repackaged.Repackaged;
import net.liukrast.repackaged.registry.RepackagedBlocks;
import net.liukrast.repackaged.registry.RepackagedItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;

public class RepackagedLanguageProvider extends DeployerLanguageProviderImpl {
    public RepackagedLanguageProvider(PackOutput output) {
        super(output, Repackaged.CONSTANTS.getModId(), "en_us");
    }

    @Override
    protected void addTranslations() {
        addReplaced("itemGroup.%s", "Create: Repackaged");

        /* AUTO-GENERATED */
        Repackaged.CONSTANTS.getElementEntries(BuiltInRegistries.ITEM)
                .filter(e -> {
                    if(RepackagedItems.RARE_BATTERIES.stream().anyMatch(de -> de.get().equals(e.getValue()))) return false;
                    if(RepackagedItems.STANDARD_BATTERIES.stream().anyMatch(de -> de.get().equals(e.getValue()))) return false;
                    if(RepackagedItems.RARE_BOTTLES.stream().anyMatch(de -> de.get().equals(e.getValue()))) return false;
                    return RepackagedItems.STANDARD_BOTTLES.stream().noneMatch(de -> de.get().equals(e.getValue()));
                })
                .forEach(e -> add(e.getValue().getDescriptionId(), autoName(e.getKey())));
        Repackaged.CONSTANTS.getElementEntries(BuiltInRegistries.ENTITY_TYPE)
                .forEach(e -> add(e.getValue(), autoName(e.getKey())));
        addReplaced("stock_inventory_type.%s.fluid", "Fluids");
        addReplaced("stock_inventory_type.%s.fluid.search", "Search fluids");
        addReplaced("stock_inventory_type.%s.energy", "Energy");

        addReplaced("item.%s.battery.brass", "Brass Battery");
        addReplaced("item.%s.bottle.copper", "Copper Bottle");
        addReplaced("item.%s.rare_bottle.rare_exposed_copper", "Rare Exposed Copper Bottle");
        addReplaced("item.%s.rare_bottle.rare_large_exposed_copper", "Rare Exposed Large Copper Bottle");
        addReplaced("item.%s.rare_bottle.rare_weathered_copper", "Rare Weathered Copper Bottle");
        addReplaced("item.%s.rare_bottle.rare_large_weathered_copper", "Rare Weathered Large Copper Bottle");
        addReplaced("item.%s.rare_bottle.rare_oxidized_copper", "Rare Oxidized Copper Bottle");
        addReplaced("item.%s.rare_bottle.rare_large_oxidized_copper", "Rare Oxidized Large Copper Bottle");
        addReplaced("item.%s.rare_bottle.rare_golden", "Rare Golden Bottle");

        addReplaced("stock_inventory_type.%s.energy.action_add", "Scroll or click to add");
        addReplaced("stock_inventory_type.%s.energy.action_remove", "Scroll or click to remove");

        addPrefixed("logistics.shelf.connected", "Connected shelves");
        addPrefixed("logistics.shelf.click_to_separate", "Click to separate shelves");
        addPrefixed("logistics.shelf.click_to_merge", "Click to merge shelves");

        addPrefixed("gui.battery_charger.info_header", "Battery Charger");
        addPrefixed("gui.battery_charger.status", "Status:");
        addPrefixed("gui.battery_charger.status.charging", "Charging");
        addPrefixed("gui.battery_charger.status.discharging", "Discharging");
        addPrefixed("gui.battery_charger.status.idle", "Idle");
        addPrefixed("gui.battery_charger.progress", "Progress:");

        createPonder(
                RepackagedBlocks.BATTERY_CHARGER.asItem(),
                "Charge your batteries with ⚡",
                "Any FE storage can be used!",
                "Just like packagers, battery chargers extract energy to create batteries",
                "Although, some energy storages have limits on how fast energy can be transferred...",
                "...so your battery charger might need some time to extract/insert energy",
                "When something is extracting the battery automatically...",
                "...it won't be able to do that until the charging is finished.",
                "You can manually get the battery before it's done charging if needed"
        );

        createPonder(
                "stacked_packagers",
                "Stack your packagers to sort different items",
                "When a package arrives in an incorrect packager, it will be rejected...",
                "...but don't worry, we can find a solution for our lonely package...",
                "...you can extract the item and move it to another packager"
        );

        createPonder(
                RepackagedBlocks.PACKAGE_SHELF.asItem(),
                "Reunite your orders with a package shelf",
                "Sometimes, you might want to order multiple package types at the same time...",
                "...but coming from different places, you might never get those packages in order in one place!",
                "Package shelf works just like a re-packager...",
                "...it will export all items in the shelf, only if every item ordered is present!",
                "You can now export packages in order and unpack them.",
                "Sometimes, orders are bigger than expected, and they don't fit in the shelf...",
                "...the shelf can be increased by adding another block on top"
        );

        createPonder(
                RepackagedBlocks.PACKAGER_CONNECTOR.asItem(),
                "Sort your packages with a package connector",
                "Sorting your packages with chutes can sometimes be tricky, especially in horizontal setups.",
                "Place the connectors on your packagers...",
                "...any rejected package will now travel on the line until it finds a valid packager!",
                "Any connector facing a non-packager block will skip to the next one"
        );
    }
}
