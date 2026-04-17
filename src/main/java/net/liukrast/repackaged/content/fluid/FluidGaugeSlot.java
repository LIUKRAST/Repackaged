package net.liukrast.repackaged.content.fluid;

import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;
import net.liukrast.deployer.lib.helper.GuiRenderingHelpers;
import net.liukrast.deployer.lib.logistics.board.connection.PanelConnection;
import net.liukrast.deployer.lib.logistics.board.screen.StockSlot;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class FluidGaugeSlot extends StockSlot<FluidStack, FluidPanelBehaviour> {
    public FluidGaugeSlot(FluidPanelBehaviour panel, PanelConnection<?> connection) {
        super(panel, connection);
    }

    @Override
    public Component getTitle(FluidStack fluidStack) {
        return fluidStack.getHoverName();
    }

    @Override
    public void renderInputSlot(GuiGraphics graphics, FluidStack stack, int mouseX, int mouseY, int x, int y) {
        if(!stack.isEmpty()) GuiRenderingHelpers.renderFluidSlot(graphics, stack, x+1, y+1, 14,14);
        if(mouseX < x - 2 || mouseX >= x - 2 + 20 || mouseY < y -2 || mouseY >= y -2 + 20)
            return;

        if(stack.isEmpty()) {
            graphics.renderComponentTooltip(Minecraft.getInstance().font, List.of(CreateLang.translate("gui.factory_panel.empty_panel")
                                    .color(ScrollInput.HEADER_RGB)
                                    .component(),
                            CreateLang.translate("gui.factory_panel.left_click_disconnect")
                                    .style(ChatFormatting.DARK_GRAY)
                                    .style(ChatFormatting.ITALIC)
                                    .component()),
                    mouseX, mouseY);
            return;
        }
        if(panel.hasInteraction("restocker")) {
            graphics.renderComponentTooltip(Minecraft.getInstance().font,
                    List.of(CreateLang.translate("gui.factory_panel.sending_item", CreateLang.builder().add(stack.getHoverName().copy())
                                            .string())
                                    .color(ScrollInput.HEADER_RGB)
                                    .component(),
                            CreateLang.translate("gui.factory_panel.sending_item_tip")
                                    .style(ChatFormatting.GRAY)
                                    .component(),
                            CreateLang.translate("gui.factory_panel.sending_item_tip_1")
                                    .style(ChatFormatting.GRAY)
                                    .component()),
                    mouseX, mouseY);
            return;
        }


        graphics.renderComponentTooltip(Minecraft.getInstance().font,
                List.of(CreateLang.translate("gui.factory_panel.sending_item", CreateLang.builder().add(stack.getHoverName().copy())
                                        .add(CreateLang.text(" x" + stack.getAmount() + "Mb"))
                                        .string())
                                .color(ScrollInput.HEADER_RGB)
                                .component(),
                        CreateLang.translate("gui.factory_panel.scroll_to_change_amount")
                                .style(ChatFormatting.DARK_GRAY)
                                .style(ChatFormatting.ITALIC)
                                .component(),
                        CreateLang.translate("gui.factory_panel.left_click_disconnect")
                                .style(ChatFormatting.DARK_GRAY)
                                .style(ChatFormatting.ITALIC)
                                .component()),
                mouseX, mouseY);
    }

    @Override
    public void renderOutputSlot(GuiGraphics graphics, FluidStack stack, int mouseX, int mouseY, int x, int y) {
        if(!stack.isEmpty()) GuiRenderingHelpers.renderFluidSlot(graphics, stack, x+1, y+1, 14,14);
    }

    @Override
    public int scrollAmount(boolean ctrlDown, boolean shiftDown, boolean altDown) {
        return ctrlDown ? 100 : shiftDown ? 1000 : altDown ? 1 : 10;
    }
}
