package net.liukrast.repackaged.datagen;

import net.liukrast.repackaged.RepackagedConstants;
import net.liukrast.repackaged.registry.RepackagedItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Objects;
import java.util.function.Function;

public class RepackagedItemModelProvider extends ItemModelProvider {
    public RepackagedItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, RepackagedConstants.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        createGauge(RepackagedItems.FLUID_GAUGE.get());
    }

    public static ItemModelBuilder createGauge(ItemModelProvider instance, Item item, Function<String, String> texture) {
        var id = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item));
        return instance.getBuilder(id.toString()).parent(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath("create", "block/factory_gauge/item")))
                .texture("0", ResourceLocation.fromNamespaceAndPath(id.getNamespace(), texture.apply(id.getPath())))
                .texture("particle", ResourceLocation.fromNamespaceAndPath(id.getNamespace(), texture.apply(id.getPath())));
    }

    @SuppressWarnings("unused")
    public static ItemModelBuilder createGauge(ItemModelProvider instance, Item item) {
        return createGauge(instance, item, id -> "block/" + id);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static ItemModelBuilder createPanel(ItemModelProvider instance, Item item) {
        return createGauge(instance, item, id -> "block/" + id.split("_")[0] + "_panel");
    }

    private void createGauge(Item item) {
        createPanel(this, item);
    }
}
