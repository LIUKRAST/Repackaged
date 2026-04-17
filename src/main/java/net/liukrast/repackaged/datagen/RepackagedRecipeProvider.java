package net.liukrast.repackaged.datagen;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import net.liukrast.repackaged.registry.RepackagedBlocks;
import net.liukrast.repackaged.registry.RepackagedItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class RepackagedRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public RepackagedRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, RepackagedBlocks.FLUID_PACKAGER.get())
                .pattern(" a ")
                .pattern("aba")
                .pattern("cac")
                .define('a', Items.COPPER_INGOT)
                .define('b', AllBlocks.CARDBOARD_BLOCK)
                .define('c', Items.REDSTONE)
                .unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT)).save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, RepackagedBlocks.BATTERY_CHARGER.get())
                .pattern(" a ")
                .pattern("aba")
                .pattern("cac")
                .define('a', AllItems.BRASS_INGOT)
                .define('b', AllBlocks.CARDBOARD_BLOCK)
                .define('c', Items.REDSTONE)
                .unlockedBy("has_brass_ingot", has(AllItems.BRASS_INGOT)).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, RepackagedBlocks.PACKAGE_SHELF)
                .pattern("a").pattern("b")
                .define('a', AllItems.ELECTRON_TUBE)
                .define('b', AllBlocks.PACKAGER)
                .unlockedBy("has_packager", has(AllBlocks.PACKAGER)).save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, RepackagedItems.FLUID_GAUGE)
                .requires(AllBlocks.FACTORY_GAUGE).requires(AllItems.COPPER_SHEET)
                .unlockedBy("has_factory_gauge", has(AllBlocks.FACTORY_GAUGE)).save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, RepackagedItems.FLUID_GAUGE)
                .requires(RepackagedItems.FLUID_GAUGE)
                .unlockedBy("has_fluid_gauge", has(RepackagedItems.FLUID_GAUGE)).save(output, "fluid_gauge_clear");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, RepackagedItems.ENERGY_GAUGE)
                .requires(AllBlocks.FACTORY_GAUGE).requires(AllItems.BRASS_SHEET)
                .unlockedBy("has_factory_gauge", has(AllBlocks.FACTORY_GAUGE)).save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, RepackagedItems.ENERGY_GAUGE)
                .requires(RepackagedItems.ENERGY_GAUGE)
                .unlockedBy("has_energy_gauge", has(RepackagedItems.ENERGY_GAUGE)).save(output, "energy_gauge_clear");
    }
}
