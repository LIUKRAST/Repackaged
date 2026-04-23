package net.liukrast.repackaged.content.fluid;

import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.logistics.redstoneRequester.RedstoneRequesterMenu;
import net.liukrast.deployer.lib.helper.client.FluidGhostSlot;
import net.liukrast.deployer.lib.logistics.packager.StockInventoryType;
import net.liukrast.deployer.lib.logistics.packager.screen.RequesterTabScreen;
import net.liukrast.deployer.lib.logistics.stockTicker.GenericOrderContained;
import net.liukrast.repackaged.Repackaged;
import net.liukrast.repackaged.registry.RepackagedItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FluidRequesterTabScreen extends RequesterTabScreen<FluidStack> {
    private static final ResourceLocation TEXTURE = Repackaged.CONSTANTS.id("textures/gui/fluid_stock_inventory.png");

    private List<FluidGhostSlot> slots;
    public FluidRequesterTabScreen(RedstoneRequesterMenu container, StockInventoryType<?, FluidStack, ?> type, GenericOrderContained<FluidStack> data) {
        super(container, RepackagedItems.STANDARD_BOTTLES.getFirst().asItem(), type, data);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(TEXTURE, -2, 3, 16, 48, 224, 48);
    }

    @Override
    protected void init() {
        var stacks = orderData.stacks();
        slots = new ArrayList<>();
        for(int i = 0; i < 9; i++) {
            var stack = i >= stacks.size() ? FluidStack.EMPTY : orderData.stacks().get(i);
            var slot = new FluidGhostSlot(i*20 + 21, 11, container, stack);
            slots.add(slot);
            this.addRenderableWidget(slot);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean sup = super.mouseClicked(mouseX, mouseY, button);
        orderData = type
                .valueHandler()
                .createContained(slots.stream().map(FluidGhostSlot::getGhostStack).toList());
        return sup;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        boolean sup = super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        orderData = type
                .valueHandler()
                .createContained(slots.stream().map(FluidGhostSlot::getGhostStack).toList());
        return sup;
    }

    public void setSlot(int index, FluidStack stack) {
        slots.get(index).setGhostStack(stack);
        orderData = type
                .valueHandler()
                .createContained(slots.stream().map(FluidGhostSlot::getGhostStack).toList());
    }

    @Override
    public void quickMoveItemEvent(ItemStack stack) {
        for(var slot : slots) {
            if(slot.getGhostStack().isEmpty()) {
                slot.setGhostStack(FilterItemStack.of(stack).fluid(Minecraft.getInstance().level));
                return;
            }
        }
        orderData = type
                .valueHandler()
                .createContained(slots.stream().map(FluidGhostSlot::getGhostStack).toList());
    }
}
