package net.liukrast.repackaged.content.energy;

import com.simibubi.create.content.logistics.stockTicker.StockKeeperRequestMenu;
import net.liukrast.deployer.lib.helper.GuiRenderingHelpers;
import net.liukrast.deployer.lib.logistics.packager.AbstractInventorySummary;
import net.liukrast.deployer.lib.logistics.packager.StockInventoryType;
import net.liukrast.deployer.lib.logistics.packager.screen.KeeperSourceContext;
import net.liukrast.deployer.lib.logistics.packager.screen.KeeperTabScreen;
import net.liukrast.deployer.lib.logistics.packager.screen.ProvidesOrder;
import net.liukrast.deployer.lib.logistics.stockTicker.GenericOrderContained;
import net.liukrast.repackaged.Repackaged;
import net.liukrast.repackaged.registry.RepackagedItems;
import net.liukrast.repackaged.registry.RepackagedStockInventoryTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class EnergyTabScreen extends KeeperTabScreen implements ProvidesOrder<EnergyStack> {
    private static final ResourceLocation TEXTURE = Repackaged.CONSTANTS.id("textures/gui/energy_stock_inventory.png");
    private static final List<Component> UNFINISHED_ORDER = List.of(
            Component.translatable("stock_inventory_type.unfinished_order").withStyle(style -> style.withColor(0x5391e1)),
            Component.translatable("stock_inventory_type.unfinished_order_line_1").withStyle(ChatFormatting.GRAY),
            Component.translatable("stock_inventory_type.unfinished_order_line_2").withStyle(ChatFormatting.GRAY)
    );
    private final StockInventoryType<Energy, EnergyStack, IEnergyStorage> type = RepackagedStockInventoryTypes.ENERGY.get();

    public int amountInStorage = 0;
    private int cached = 0;
    public int amountToOrder = 0;

    public EnergyTabScreen(KeeperSourceContext context, StockKeeperRequestMenu menu) {
        super(context, menu, Component.translatable("stock_inventory_type.repackaged.energy"), RepackagedItems.STANDARD_BATTERIES.getFirst().asItem());
    }

    @Override
    public void containerTick() {
        List<List<EnergyStack>> clientStockSnapshot = context.getClientStockSnapshot(type);
        int counter = 0;
        if(clientStockSnapshot != null) {
            for (var a : clientStockSnapshot) {
                for(var b : a) {
                    counter += b.getAmount();
                }
            }
        }
        if(counter != amountInStorage)
            cached = 0;
        amountInStorage = counter;

    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(TEXTURE, (width-138)>>1,30,0,32,138,128);
        graphics.blit(TEXTURE, 2, height-33, 0, 0, 186, 32);

        graphics.blit(TEXTURE, 7, height-26, 0, 160, 176, 13);

        DecimalFormat df = new DecimalFormat("#,###", new DecimalFormatSymbols(Locale.ITALY));
        graphics.drawString(font, Component.literal(df.format(amountInStorage-amountToOrder-cached) + "⚡").withStyle(st -> st.withFont(GuiRenderingHelpers.LOGISTICS_FONT)), (width-120)>>1, 60, 0xe4ddce, false);
        graphics.drawString(font, Component.literal(df.format(amountToOrder) + "⚡").withStyle(st -> st.withFont(GuiRenderingHelpers.LOGISTICS_FONT)), 8, height-23, 0xe4ddce, false);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public List<Component> getWarnTooltip() {
        return amountToOrder <= 0 ? null : UNFINISHED_ORDER;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int scrollAmount = hasControlDown() ? 1000 : hasShiftDown() ? 1 : hasAltDown() ? 100_000 : 100;
        amountToOrder = (int)Mth.clamp(amountToOrder + scrollY * scrollAmount, 0, amountInStorage);
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public void onSendIt() {
        amountToOrder = 0;
    }

    @Override
    public @Nullable GenericOrderContained<EnergyStack> addToSendQueue() {
        if(amountToOrder == 0) return null;
        AbstractInventorySummary<Energy, EnergyStack> summary = context.getLastClientsideSnapshotAsSummary(type);
        int inStorage = summary.getCountOf(new EnergyStack(1, Optional.empty()));
        int ato = Math.min(inStorage, amountToOrder);
        cached = ato;
        return type.valueHandler().createContained(List.of(new EnergyStack(ato, Optional.empty())));
    }

    @Override
    public @NotNull StockInventoryType<?, EnergyStack, ?> getType() {
        return type;
    }
}
