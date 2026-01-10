package net.liukrast.repackaged.content.energy;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import net.liukrast.deployer.lib.logistics.GenericPackageOrderData;
import net.liukrast.deployer.lib.logistics.packager.AbstractInventorySummary;
import net.liukrast.deployer.lib.logistics.packager.GenericPackageItem;
import net.liukrast.deployer.lib.logistics.packager.StockInventoryType;
import net.liukrast.deployer.lib.logistics.packagerLink.GenericRequestPromise;
import net.liukrast.deployer.lib.logistics.stockTicker.GenericOrderContained;
import net.liukrast.repackaged.RepackagedConstants;
import net.liukrast.repackaged.registry.RepackagedDataComponents;
import net.liukrast.repackaged.registry.RepackagedItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class EnergyStockInventoryType extends StockInventoryType<Energy, EnergyStack, IEnergyStorage> {
    private static final Codec<GenericRequestPromise<EnergyStack>> REQUEST_CODEC = GenericRequestPromise.simpleCodec(EnergyStack.CODEC);

    public static final int MAX_BATTERY_ENERGY = 500_000;

    private static final IValueHandler<Energy, EnergyStack, IEnergyStorage> VALUE_HANDLER = new IValueHandler<>(EnergyStack.CODEC, EnergyStack.STREAM_CODEC) {

        @Override
        public Energy fromValue(EnergyStack key) {
            return Energy.INSTANCE;
        }

        @Override
        public boolean equalsIgnoreCount(EnergyStack a, EnergyStack b) {
            return true;
        }

        @Override
        public boolean test(FilterItemStack filter, Level level, EnergyStack value) {
            return true;
        }

        @Override
        public int getCount(EnergyStack value) {
            return value.getAmount();
        }

        @Override
        public void setCount(EnergyStack value, int count) {
            value.setAmount(count);
        }

        @Override
        public boolean isEmpty(EnergyStack stack) {
            return stack.isEmpty();
        }

        @Override
        public EnergyStack create(Energy key, int amount) {
            return new EnergyStack(amount);
        }

        @Override
        public void shrink(EnergyStack stack, int amount) {
            stack.setAmount(stack.getAmount() - amount);
        }

        @Override
        public EnergyStack copyWithCount(EnergyStack stack, int amount) {
            return new EnergyStack(amount);
        }

        @Override
        public EnergyStack copy(EnergyStack stack) {
            return new EnergyStack(stack.getAmount());
        }

        @Override
        public boolean isStackable(EnergyStack stack) {
            return true;
        }

        @Override
        public EnergyStack empty() {
            return EnergyStack.EMPTY;
        }
    };

    private static final IStorageHandler<Energy, EnergyStack, IEnergyStorage> STORAGE_HANDLER = new IStorageHandler<>() {
        @Override
        public int getSlots(IEnergyStorage handler) {
            return 1;
        }

        @Override
        public EnergyStack getStackInSlot(IEnergyStorage handler, int slot) {
            return new EnergyStack(handler.getEnergyStored());
        }

        @Override
        public int maxCountPerSlot() {
            return MAX_BATTERY_ENERGY;
        }

        @Override
        public EnergyStack extract(IEnergyStorage handler, EnergyStack value, boolean simulate) {
            if(!handler.canExtract()) return EnergyStack.EMPTY;
            return new EnergyStack(handler.extractEnergy(value.getAmount(), simulate));
        }

        @Override
        public int fill(IEnergyStorage handler, EnergyStack value, boolean simulate) {
            if(!handler.canReceive()) return 0;
            return value.getAmount() - handler.receiveEnergy(value.getAmount(), simulate);
        }

        @Override
        public EnergyStack setInSlot(IEnergyStorage handler, int slot, EnergyStack value, boolean simulate) {
            int result = fill(handler, value, simulate);
            return new EnergyStack(result);
        }

        @Override
        public boolean isBulky(Energy key) {
            return false;
        }

        @Override
        public IEnergyStorage create(int i) {
            return new EnergyStorage(MAX_BATTERY_ENERGY);
        }

        @Override
        public int getMaxPackageSlots() {
            return 1;
        }

        @Override
        public EnergyStack insertItem(IEnergyStorage handler, int i, EnergyStack stack, boolean simulate) {
            if(!handler.canReceive()) return EnergyStack.EMPTY;
            return new EnergyStack(stack.getAmount() - handler.receiveEnergy(stack.getAmount(), simulate));
        }
    };

    private static final INetworkHandler<Energy, EnergyStack, IEnergyStorage> NETWORK_HANDLER = new INetworkHandler<>() {
        @Override
        public Codec<GenericRequestPromise<EnergyStack>> requestCodec() {
            return REQUEST_CODEC;
        }

        @Override
        public AbstractInventorySummary<Energy, EnergyStack> createSummary() {
            return new EnergyInventorySummary();
        }

        @Override
        public AbstractInventorySummary<Energy, EnergyStack> empty() {
            return EnergyInventorySummary.EMPTY.get();
        }

        @Override
        public DataComponentType<? super GenericPackageOrderData<EnergyStack>> getComponent() {
            return RepackagedDataComponents.BATTERY_ORDER_DATA.get();
        }
    };

    private static final IPackageHandler<Energy, EnergyStack, IEnergyStorage> PACKAGE_HANDLER = new IPackageHandler<>() {
        @Override
        public void setBoxContent(ItemStack stack, IEnergyStorage inventory) {
            stack.set(RepackagedDataComponents.BATTERY_CONTENTS, inventory.getEnergyStored());
        }

        private static final Random STYLE_PICKER = new Random();
        private static final int RARE_CHANCE = 7500;

        @Override
        public ItemStack getRandomBox() {
            List<DeferredItem<GenericPackageItem>> pool = STYLE_PICKER.nextInt(RARE_CHANCE) == 0 ? RepackagedItems.RARE_BATTERIES : RepackagedItems.STANDARD_BATTERIES;
            return new ItemStack(pool.get(STYLE_PICKER.nextInt(pool.size())).get());
        }

        @Override
        public IEnergyStorage getContents(ItemStack box) {
            return box.getCapability(Capabilities.EnergyStorage.ITEM);
        }

        @Override
        public DataComponentType<GenericPackageOrderData<EnergyStack>> packageOrderData() {
            return RepackagedDataComponents.BATTERY_ORDER_DATA.get();
        }

        @Override
        public DataComponentType<GenericOrderContained<EnergyStack>> packageOrderContext() {
            return RepackagedDataComponents.BATTERY_ORDER_CONTEXT.get();
        }

        @Override
        public int clickAmount(boolean ctrlDown, boolean shiftDown, boolean altDown) {
            return ctrlDown ? 100 : shiftDown ? 1000 : altDown ? 1 : 10;
        }

        @Override
        public int scrollAmount(boolean ctrlDown, boolean shiftDown, boolean altDown) {
            return ctrlDown ? 100 : shiftDown ? 1000 : altDown ? 1 : 10;
        }

        @Override
        public boolean shouldRenderSearchBar() {
            return false;
        }

        @Override
        public boolean matchesModSearch(EnergyStack stack, String searchValue) {
            return true;
        }

        @Override
        public boolean matchesTagSearch(EnergyStack stack, String searchValue) {
            return true;
        }

        @Override
        public boolean matchesSearch(EnergyStack stack, String searchValue) {
            return true;
        }

        @Override
        public void renderCategory(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY, List<EnergyStack> categoryStacks, List<EnergyStack> itemsToOrder, AbstractInventorySummary<Energy, EnergyStack> forcedEntries, CategoryRenderData data) {
            if(categoryStacks.isEmpty()) return;
            graphics.blit(TEXTURE, data.itemsX(), data.itemsY(), 32, 48, 192, 128);

            var entry = categoryStacks.getFirst();
            int customCount = entry.getAmount();
            EnergyStack order = itemsToOrder.isEmpty() ? null : itemsToOrder.getFirst();
            if(entry.getAmount() < BigItemStack.INF) {
                int forcedCount = forcedEntries.getCountOf(entry);
                if(forcedCount != 0)
                    customCount = Math.min(customCount, -forcedCount - 1);
                if(order != null)
                    customCount -= order.getAmount();
                customCount = Math.max(0, customCount);
            }
            drawCount(graphics, customCount, data.itemsX() + 144, data.itemsY() + 41);
        }

        private static final ResourceLocation TEXTURE = RepackagedConstants.id("textures/gui/energy_stock_inventory.png");

        @Override
        public void renderOrderedItems(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY, List<EnergyStack> itemsToOrder, AbstractInventorySummary<Energy, EnergyStack> forcedEntries, OrderRenderData data) {
            graphics.blit(TEXTURE, data.itemsX()-39, data.orderY() - 8, 0, 0, 256, 48);
            if(itemsToOrder.isEmpty()) return;
            int amountON176 = Math.min(itemsToOrder.getFirst().getAmount()*176/MAX_BATTERY_ENERGY, 176);
            graphics.blit(TEXTURE, data.itemsX()+1+176-amountON176, data.orderY()+1, 40, 185, amountON176, 17);
            drawCount(graphics, itemsToOrder.getFirst().getAmount(), data.itemsX() + 160, data.orderY() + 7);
        }

        private void drawCount(GuiGraphics graphics, int customCount, int x, int y) {
            String text = customCount >= 1000000 ? (customCount / 1000000) + "m"
                    : customCount >= 10000 ? (customCount / 1000) + "k"
                    : customCount >= 1000 ? ((customCount * 10) / 1000) / 10f + "k" : customCount >= 100 ? customCount + "" : " " + customCount;

            if (customCount >= BigItemStack.INF)
                text = "+";

            text += "⚡"; //Special character!!

            if (text.isBlank())
                return;

            int totalW = 0;
            for(var c : text.toCharArray()) {
                int w = switch (c) {
                    case '.' -> 3;
                    case 'm', '⚡' -> 7;
                    case '+' -> 9;
                    default -> 5;
                };
                totalW+=w-1;
            }

            int x0 = 0;
            for(char c : text.toCharArray()) {
                int w = switch (c) {
                    case '.' -> 3;
                    case 'm', '⚡' -> 7;
                    case '+' -> 9;
                    default -> 5;
                };
                int p = switch (c) {
                    case 'k' -> 64;
                    case 'm' -> 70;
                    case 'b' -> 78;
                    case '+' -> 84;
                    case '⚡' -> 94;
                    default -> (c - '0')*6;
                };

                RenderSystem.enableBlend();
                graphics.blit(TEXTURE, x0+x-totalW/2, y, 48+p, 209, w, 7);
                x0+=w-1;
            }

        }

        private static final Component ACTION_REMOVE = Component.translatable("stock_inventory_type.repackaged.energy.action_remove");
        private static final Component ACTION_ADD = Component.translatable("stock_inventory_type.repackaged.energy.action_add");

        @Override
        public void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, EnergyStack entry, Font font, boolean isOrder) {
            graphics.renderTooltip(font, isOrder ? ACTION_REMOVE : ACTION_ADD, mouseX, mouseY);
        }

        @Override
        public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, List<Component> tooltipComponents, TooltipFlag tooltipFlag, IEnergyStorage handler) {
            tooltipComponents.add(Component.literal(handler.getEnergyStored() + "⚡").withStyle(ChatFormatting.GRAY));
        }

        @Override
        public int getColWidth() {
            return 20*9;
        }

        @Override
        public int getRowHeight() {
            return 20*4;
        }
    };

    @Override
    public @NotNull IValueHandler<Energy, EnergyStack, IEnergyStorage> valueHandler() {
        return VALUE_HANDLER;
    }

    @Override
    public @NotNull IStorageHandler<Energy, EnergyStack, IEnergyStorage> storageHandler() {
        return STORAGE_HANDLER;
    }

    @Override
    public @NotNull INetworkHandler<Energy, EnergyStack, IEnergyStorage> networkHandler() {
        return NETWORK_HANDLER;
    }

    @Override
    public @NotNull IPackageHandler<Energy, EnergyStack, IEnergyStorage> packageHandler() {
        return PACKAGE_HANDLER;
    }

    private static final ItemStack ICON = Items.LIGHTNING_ROD.getDefaultInstance();

    @Override
    public @NotNull ItemStack getIcon() {
        return ICON;
    }

    @Override
    public BlockCapability<IEnergyStorage, @Nullable Direction> getBlockCapability() {
        return Capabilities.EnergyStorage.BLOCK;
    }
}
