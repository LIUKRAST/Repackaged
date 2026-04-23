package net.liukrast.repackaged.content.energy;

import com.simibubi.create.content.logistics.redstoneRequester.RedstoneRequesterMenu;
import net.liukrast.deployer.lib.helper.GuiRenderingHelpers;
import net.liukrast.deployer.lib.logistics.packager.StockInventoryType;
import net.liukrast.deployer.lib.logistics.packager.screen.RequesterTabScreen;
import net.liukrast.deployer.lib.logistics.stockTicker.GenericOrderContained;
import net.liukrast.repackaged.Repackaged;
import net.liukrast.repackaged.registry.RepackagedItems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class EnergyRequesterTabScreen extends RequesterTabScreen<EnergyStack> {
    private static final ResourceLocation TEXTURE = Repackaged.CONSTANTS.id("textures/gui/energy_stock_inventory.png");

    private int amount;
    public EnergyRequesterTabScreen(RedstoneRequesterMenu container, StockInventoryType<?, EnergyStack, ?> type, GenericOrderContained<EnergyStack> data) {
        super(container, RepackagedItems.STANDARD_BATTERIES.getFirst().asItem(), type, data);
    }

    @Override
    protected void init() {
        amount = orderData.stacks().stream().mapToInt(EnergyStack::getAmount).sum();
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(TEXTURE, 17, 8, 0, 0, 186, 32);
        guiGraphics.blit(TEXTURE, 17+5, 8+7, 0, 160, 176, 13);
        DecimalFormat df = new DecimalFormat("#,###", new DecimalFormatSymbols(Locale.ITALY));
        guiGraphics.drawString(font, Component.literal(df.format(amount) + "⚡").withStyle(st -> st.withFont(GuiRenderingHelpers.LOGISTICS_FONT)),23,18, 0xe4ddce, false);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int scrollAmount = hasControlDown() ? 1000 : hasShiftDown() ? 1 : 100;
        int scrolled = Mth.clamp(
                (int)(amount + Math.signum(scrollY) * scrollAmount),
                0,
                10_000_000
        );
        amount = scrolled;
        orderData = type.valueHandler().createContained(List.of(new EnergyStack(scrolled, Optional.empty())));
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
}
