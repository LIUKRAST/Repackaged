package net.liukrast.repackaged.content.fluid;

import com.mojang.blaze3d.systems.RenderSystem;
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

import static com.simibubi.create.foundation.gui.AllGuiTextures.NUMBERS;

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
        if(customCount != 0) GuiRenderingHelpers.renderFluid(graphics, entry.copyWithAmount(1000), 0, 0, 16,16);
        ms.popPose();

        ms.pushPose();
        ms.translate(0, 0, 190);
        ms.translate(0, 0, 10);
        if (customCount > 1)
            drawItemCount(graphics, customCount);
        ms.popPose();
    }

    private void drawItemCount(GuiGraphics graphics, int customCount) {

        String text;

        if(customCount >= 1_000_000) {
            text = customCount/1_000_000 + "kb";
        } else if(customCount >= 1000) {
            // Bucket
            text = customCount/1000 + "b";
        } else {
            // decimals of bucket
            int rem = customCount%10000;
            if(rem < 100)
                text = rem + "mb";
            else
                text = customCount/10000 + "." + (rem)/10 + "b";
        }

        if (customCount >= BigItemStack.INF /* What is considered infinite? */)
            text = "+";

        if (text.isBlank())
            return;

        int x = (int) Math.floor(-text.length() * 2.5);
        for (char c : text.toCharArray()) {
            int index = c - '0';
            int xOffset = index * 6;
            int spriteWidth = NUMBERS.getWidth();

            switch (c) {
                case ' ':
                    x += 4;
                    continue;
                case '.':
                    spriteWidth = 3;
                    xOffset = 60;
                    break;
                case 'k':
                    xOffset = 64;
                    break;
                case 'm':
                    spriteWidth = 7;
                    xOffset = 70;
                    break;
                case '+':
                    spriteWidth = 9;
                    xOffset = 84;
                    break;
                case 'b':
                    xOffset = 78;
                    break;
            }

            RenderSystem.enableBlend();
            graphics.blit(NUMBERS.location, 14 + x, 10, 0, NUMBERS.getStartX() + xOffset, NUMBERS.getStartY(),
                    spriteWidth, NUMBERS.getHeight(), 256, 256);
            x += spriteWidth - 1;
        }

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
