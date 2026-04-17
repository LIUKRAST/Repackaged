package net.liukrast.repackaged.content.energy;

import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;
import net.liukrast.deployer.lib.helper.GuiRenderingHelpers;
import net.liukrast.deployer.lib.logistics.board.connection.PanelConnection;
import net.liukrast.deployer.lib.logistics.board.screen.StockSlot;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;

public class EnergyGaugeSlot extends StockSlot<EnergyStack, EnergyPanelBehaviour> {

    public EnergyGaugeSlot(EnergyPanelBehaviour panel, PanelConnection<?> connection) {
        super(panel, connection);
    }

    @Override
    public Component getTitle(EnergyStack energyStack) {
        return Component.translatable("stock_inventory_type.repackaged.energy");
    }

    @Override
    public void renderInputSlot(GuiGraphics graphics, EnergyStack stack, int mouseX, int mouseY, int x, int y) {
        int amount = stack.getAmount();
        String text;
        if (amount >= 1000000) {
            text = amount / 1000000 + "M⚡";
        } else if (amount >= 1000) {
            text = amount / 1000 + "k⚡";
        } else {
            int rem = amount % 10000;
            if (rem < 100) {
                text = rem + "⚡";
            } else {
                text = amount / 10000 + "." + rem / 10 + "k⚡";
            }
        }
        var comp = Component.literal(text).withStyle(style -> style.withFont(GuiRenderingHelpers.LOGISTICS_FONT));
        int w = Minecraft.getInstance().font.width(comp);
        graphics.drawString(Minecraft.getInstance().font, comp, x + 8 - w/2, y+5,0xe4ddce, false);
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
                    List.of(CreateLang.translate("gui.factory_panel.sending_item", Component.translatable("stock_inventory_type.repackaged.energy"))
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
                List.of(CreateLang.translate("gui.factory_panel.sending_item", CreateLang.builder().add(Component.translatable("stock_inventory_type.repackaged.energy"))
                                        .add(CreateLang.text(" x" + stack.getAmount() + "⚡"))
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
    public void renderOutputSlot(GuiGraphics graphics, EnergyStack stack, int mouseX, int mouseY, int x, int y) {
        int amount = stack.getAmount();
        String text;
        if (amount >= 1000000) {
            text = amount / 1000000 + "M⚡";
        } else if (amount >= 1000) {
            text = amount / 1000 + "k⚡";
        } else {
            int rem = amount % 10000;
            if (rem < 100) {
                text = rem + "⚡";
            } else {
                text = amount / 10000 + "." + rem / 10 + "k⚡";
            }
        }
        var comp = Component.literal(text).withStyle(style -> style.withFont(GuiRenderingHelpers.LOGISTICS_FONT));
        int w = Minecraft.getInstance().font.width(comp);
        graphics.drawString(Minecraft.getInstance().font, comp, x+8-w/2, y+5,0xe4ddce, false);
    }

    @Override
    public int scrollAmount(boolean ctrlDown, boolean shiftDown, boolean altDown) {
        return ctrlDown ? 1000 : shiftDown ? 1 : 100;
    }
}
