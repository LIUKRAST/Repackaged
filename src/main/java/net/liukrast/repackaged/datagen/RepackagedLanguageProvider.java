package net.liukrast.repackaged.datagen;

import net.liukrast.deployer.lib.registry.DeployerRegistries;
import net.liukrast.repackaged.RepackagedConstants;
import net.liukrast.repackaged.RepackagedLang;
import net.liukrast.repackaged.registry.RepackagedItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class RepackagedLanguageProvider extends LanguageProvider {
    public RepackagedLanguageProvider(PackOutput output) {
        super(output, RepackagedConstants.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addReplaced("itemGroup.%s", "Create: Repackaged");

        /* AUTO-GENERATED */
        RepackagedConstants.getElementEntries(BuiltInRegistries.ITEM)
                .filter(e -> {
                    if(RepackagedItems.RARE_BATTERIES.stream().anyMatch(de -> de.get().equals(e.getValue()))) return false;
                    if(RepackagedItems.STANDARD_BATTERIES.stream().anyMatch(de -> de.get().equals(e.getValue()))) return false;
                    if(RepackagedItems.RARE_BOTTLES.stream().anyMatch(de -> de.get().equals(e.getValue()))) return false;
                    return RepackagedItems.STANDARD_BOTTLES.stream().noneMatch(de -> de.get().equals(e.getValue()));
                })
                .forEach(e -> add(e.getValue().getDescriptionId(), autoName(e.getKey())));
        RepackagedConstants.getElementEntries(BuiltInRegistries.ENTITY_TYPE)
                .forEach(e -> add(e.getValue(), autoName(e.getKey())));


        RepackagedConstants.getElementEntries(DeployerRegistries.STOCK_INVENTORY)
                .forEach(e -> {

                    if(e.getValue().packageHandler().shouldRenderSearchBar()) {

                    }
                });
        addReplaced("stock_inventory_type.%s.fluid", "Fluids");
        addReplaced("stock_inventory_type.%s.fluid.search", "Search fluids");
        addReplaced("stock_inventory_type.%s.energy", "Energy");
        addReplaced("stock_inventory_type.%s.entity", "Entities");
        addReplaced("stock_inventory_type.%s.entity.search", "Search entities");

        addReplaced("item.%s.battery", "Brass Battery");
        addReplaced("item.%s.bottle", "Copper Bottle");

        addReplaced("stock_inventory_type.%s.energy.action_add", "Scroll or click to add");
        addReplaced("stock_inventory_type.%s.energy.action_remove", "Scroll or click to remove");
    }

    private void createPonder(Item item, String header, String... tooltips) {
        String id = BuiltInRegistries.ITEM.getKey(item).getPath();
        createPonder(id, header, tooltips);
    }

    private void createPonder(String id, String header, String... tooltips) {
        addReplaced("%s.ponder." + id + ".header", header);
        for(int i = 0; i < tooltips.length; i++) {
            addReplaced("%s.ponder." + id + ".text_" + (i+1), tooltips[i]);
        }
    }

    private void addShiftSummary(ItemLike key, String value) {
        add(RepackagedLang.getTooltip(key), value);
    }

    private void addReplaced(String key, String value) {
        add(String.format(key, RepackagedConstants.MOD_ID), value);
    }

    private void addPrefixed(String key, String value) {
        addReplaced("%s."+key, value);
    }

    public String autoName(String id) {
        String[] words = id.split("_");
        for(int i = 0; i < words.length; i++) {
            words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1);
        }
        return String.join(" ", words);
    }
}
