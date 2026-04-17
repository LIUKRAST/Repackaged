package net.liukrast.repackaged.content.fluid;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperRequestMenu;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import net.liukrast.deployer.lib.helper.GuiRenderingHelpers;
import net.liukrast.deployer.lib.logistics.packager.screen.StockTabScreen;
import net.liukrast.repackaged.RepackagedConstants;
import net.liukrast.repackaged.registry.RepackagedItems;
import net.liukrast.repackaged.registry.RepackagedStockInventoryTypes;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class FluidTabScreen extends StockTabScreen<Fluid, FluidStack> {
    private static final ResourceLocation TEXTURE = RepackagedConstants.id("textures/gui/fluid_stock_inventory.png");
    public FluidTabScreen(StockTickerBlockEntity blockEntity, StockKeeperRequestMenu menu) {
        super(blockEntity, menu, RepackagedItems.STANDARD_BOTTLES.getFirst().asItem(), RepackagedStockInventoryTypes.FLUID.get());
    }

    @Override
    public void renderTooltip(@NotNull GuiGraphics guiGraphics, FluidStack fluidStack, int mouseX, int mouseY) {
        GuiRenderingHelpers.renderTooltip(guiGraphics, fluidStack, mouseX, mouseY, font);
    }

    private FluidStack getOrderForFluid(FluidStack stack, List<FluidStack> itemsToOrder) {
        for (FluidStack entry : itemsToOrder)
            if (FluidStack.isSameFluidSameComponents(stack, entry))
                return entry;
        return null;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.blit(TEXTURE, -33, height-38, 0, 0, 256, 48);
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderEntry(@NotNull GuiGraphics graphics, int scale, FluidStack entry, boolean isStackHovered, boolean isRenderingOrders) {
        int customCount = entry.getAmount();
        if (!isRenderingOrders) {
            FluidStack order = getOrderForFluid(entry, itemsToOrder);
            if (entry.getAmount() < BigItemStack.INF) {
                int forcedCount = forcedEntries.getCountOf(entry);
                if (forcedCount != 0)
                    customCount = Math.min(customCount, -forcedCount - 1);
                if (order != null)
                    customCount -= order.getAmount();
                customCount = Math.max(0, customCount);
            }
            AllGuiTextures.STOCK_KEEPER_REQUEST_SLOT.render(graphics, 0, 0);
        }

        //entry instanceof CraftableBigItemStack;
        PoseStack ms = graphics.pose();
        ms.pushPose();

        float scaleFromHover = 1;
        if (isStackHovered)
            scaleFromHover += .075f;

        ms.translate((colWidth - 18) / 2.0, (rowHeight - 18) / 2.0, 0);
        ms.translate(18 / 2.0, 18 / 2.0, 0);
        ms.scale(scale, scale, scale);
        ms.scale(scaleFromHover, scaleFromHover, scaleFromHover);
        ms.translate(-18 / 2.0, -18 / 2.0, 0);
        if(customCount != 0) GuiRenderingHelpers.renderFluidSlot(graphics, entry, 0, 0, 16,16);
        ms.popPose();
    }

    @Override
    public int clickAmount(boolean ctrlDown, boolean shiftDown, boolean altDown) {
        return ctrlDown ? 100 : shiftDown ? 1000 : altDown ? 1 : 10;
    }

    @Override
    public boolean matchesSearch(FluidStack stack, String value) {
        boolean modSearch;
        boolean tagSearch = false;
        if ((modSearch = value.startsWith("@")) || (tagSearch = value.startsWith("#")))
            value = value.substring(1);
        value = value.toLowerCase(Locale.ROOT);
        if (modSearch) {
            return BuiltInRegistries.FLUID.getKey(stack.getFluid())
                    .getNamespace()
                    .contains(value);
        } else if (tagSearch) {
            String finalValue = value;
            return stack.getTags()
                    .anyMatch(key -> key.location()
                            .toString()
                            .contains(finalValue));
        }
        return stack.getHoverName()
                .getString()
                .toLowerCase(Locale.ROOT)
                .contains(value)
                || BuiltInRegistries.FLUID.getKey(stack.getFluid())
                .getPath()
                .contains(value);
    }
}
