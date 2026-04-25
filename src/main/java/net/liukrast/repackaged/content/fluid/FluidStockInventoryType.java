package net.liukrast.repackaged.content.fluid;

import com.mojang.serialization.Codec;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import it.unimi.dsi.fastutil.Hash;
import net.liukrast.deployer.lib.logistics.GenericPackageOrderData;
import net.liukrast.deployer.lib.logistics.packager.AbstractInventorySummary;
import net.liukrast.deployer.lib.logistics.packager.AbstractPackagerBlockEntity;
import net.liukrast.deployer.lib.logistics.packager.GenericPackageItem;
import net.liukrast.deployer.lib.logistics.packager.StockInventoryType;
import net.liukrast.deployer.lib.logistics.packagerLink.GenericRequestPromise;
import net.liukrast.deployer.lib.logistics.stockTicker.GenericOrderContained;
import net.liukrast.repackaged.registry.RepackagedDataComponents;
import net.liukrast.repackaged.registry.RepackagedItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackLinkedSet;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class FluidStockInventoryType extends StockInventoryType<Fluid, FluidStack, IFluidHandler> {
    private static final Codec<GenericRequestPromise<FluidStack>> REQUEST_CODEC =  GenericRequestPromise.simpleCodec(FluidStack.OPTIONAL_CODEC);
    private static final IValueHandler<Fluid, FluidStack, IFluidHandler> VALUE_HANDLER = new IValueHandler<>(FluidStack.OPTIONAL_CODEC, FluidStack.OPTIONAL_STREAM_CODEC) {

        @Override
        public Hash.Strategy<? super FluidStack> hashStrategy() {
            return FluidStackLinkedSet.TYPE_AND_COMPONENTS;
        }

        @Override
        public Fluid fromValue(FluidStack key) {
            return key.getFluid();
        }

        @Override
        public boolean test(FilterItemStack filter, Level level, FluidStack value) {
            return filter.test(level, value);
        }

        @Override
        public int getCount(FluidStack value) {
            return value.getAmount();
        }

        @Override
        public void setCount(FluidStack value, int count) {
            value.setAmount(count);
        }

        @Override
        public boolean isEmpty(FluidStack stack) {
            return stack.isEmpty();
        }

        @Override
        public FluidStack create(Fluid key, int amount) {
            return new FluidStack(key, amount);
        }

        @Override
        public void shrink(FluidStack stack, int amount) {
            stack.shrink(amount);
        }

        @Override
        public FluidStack copyWithCount(FluidStack stack, int amount) {
            return stack.copyWithAmount(amount);
        }

        @Override
        public FluidStack copy(FluidStack stack) {
            return stack.copy();
        }

        @Override
        public boolean isStackable(FluidStack stack) {
            return true;
        }

        @Override
        public FluidStack empty() {
            return FluidStack.EMPTY;
        }
    };
    private static final IStorageHandler<Fluid, FluidStack, IFluidHandler> STORAGE_HANDLER = new IStorageHandler<>() {
        @Override
        public int getSlots(IFluidHandler handler) {
            return handler.getTanks();
        }

        @Override
        public FluidStack getStackInSlot(IFluidHandler handler, int slot) {
            return handler.getFluidInTank(slot);
        }

        @Override
        public int maxCountPerSlot() {
            return 1000;
        }

        @Override
        public FluidStack extract(IFluidHandler handler, FluidStack value, boolean simulate, AbstractPackagerBlockEntity<Fluid, FluidStack, IFluidHandler> packager) {
            return handler.drain(value, simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
        }

        @Override
        public int fill(IFluidHandler handler, FluidStack stack, boolean simulate, AbstractPackagerBlockEntity<Fluid, FluidStack, IFluidHandler> packager) {
            return stack.getAmount() - handler.fill(stack.copy(), simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
        }

        @Override
        public FluidStack setInSlot(IFluidHandler handler, int slot, FluidStack value, boolean simulate) {
            int result = fill(handler, value, simulate, null);
            return new FluidStack(value.getFluid(), result);
        }

        @Override
        public boolean isBulky(Fluid key) {
            return false;
        }

        @Override
        public IFluidHandler create(int slots) {
            return new FluidTank(1000);
        }

        @Override
        public int getMaxPackageSlots() {
            return 1;
        }

        @Override
        public FluidStack insertItem(IFluidHandler handler, int i, FluidStack value, boolean simulate) {
            return new FluidStack(value.getFluid(), fill(handler, value, simulate, null));
        }
    };

    private static final INetworkHandler<Fluid, FluidStack, IFluidHandler> NETWORK_HANDLER = new INetworkHandler<>() {
        @Override
        public Codec<GenericRequestPromise<FluidStack>> requestCodec() {
            return REQUEST_CODEC;
        }

        @Override
        public AbstractInventorySummary<Fluid, FluidStack> createSummary() {
            return new FluidInventorySummary();
        }

        @Override
        public AbstractInventorySummary<Fluid, FluidStack> empty() {
            return FluidInventorySummary.EMPTY.get();
        }

        @Override
        public DataComponentType<? super GenericPackageOrderData<FluidStack>> getComponent() {
            return RepackagedDataComponents.BOTTLE_ORDER_DATA.get();
        }
    };

    private static final IPackageHandler<Fluid, FluidStack, IFluidHandler> PACKAGE_HANDLER = new IPackageHandler<>() {
        @Override
        public void setBoxContent(ItemStack stack, IFluidHandler inventory) {
            stack.set(RepackagedDataComponents.BOTTLE_CONTENTS, SimpleFluidContent.copyOf(inventory.getFluidInTank(0)));
        }

        private static final Random STYLE_PICKER = new Random();
        private static final int RARE_CHANCE = 7500;

        @Override
        public ItemStack getRandomBox() {
            List<DeferredItem<GenericPackageItem>> pool = !RepackagedItems.RARE_BOTTLES.isEmpty() && STYLE_PICKER.nextInt(RARE_CHANCE) == 0 ? RepackagedItems.RARE_BOTTLES : RepackagedItems.STANDARD_BOTTLES;
            return new ItemStack(pool.get(STYLE_PICKER.nextInt(pool.size())).get());
        }

        @Override
        public IFluidHandler getContents(ItemStack box) {
            FluidTank newInv = new FluidTank(1000);
            SimpleFluidContent contents = box.getOrDefault(RepackagedDataComponents.BOTTLE_CONTENTS.get(), SimpleFluidContent.EMPTY);
            newInv.fill(contents.copy(), IFluidHandler.FluidAction.EXECUTE);
            return newInv;
        }

        @Override
        public DataComponentType<GenericPackageOrderData<FluidStack>> packageOrderData() {
            return RepackagedDataComponents.BOTTLE_ORDER_DATA.get();
        }

        @Override
        public DataComponentType<GenericOrderContained<FluidStack>> packageOrderContext() {
            return RepackagedDataComponents.BOTTLE_ORDER_CONTEXT.get();
        }

        @Override
        public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, List<Component> tooltipComponents, TooltipFlag tooltipFlag, IFluidHandler handler) {
            int visibleNames = 0;
            int skippedNames = 0;
            for(int i = 0; i < handler.getTanks(); i++) {
                FluidStack BigFluidStack = handler.getFluidInTank(i);
                if(BigFluidStack.isEmpty())
                    continue;
                if(visibleNames > 2) {
                    skippedNames++;
                    continue;
                }

                visibleNames++;
                tooltipComponents.add(BigFluidStack.getHoverName()
                        .copy()
                        .append(" x" + BigFluidStack.getAmount() + "Mb")
                        .withStyle(ChatFormatting.GRAY));
            }

            if (skippedNames > 0)
                tooltipComponents.add(Component.translatable("container.shulkerBox.more", skippedNames)
                        .withStyle(ChatFormatting.ITALIC));
        }
    };

    @Override
    public @NotNull IValueHandler<Fluid, FluidStack, IFluidHandler> valueHandler() {
        return VALUE_HANDLER;
    }

    @Override
    public @NotNull IStorageHandler<Fluid, FluidStack, IFluidHandler> storageHandler() {
        return STORAGE_HANDLER;
    }

    @Override
    public @NotNull INetworkHandler<Fluid, FluidStack, IFluidHandler> networkHandler() {
        return NETWORK_HANDLER;
    }

    @Override
    public @NotNull IPackageHandler<Fluid, FluidStack, IFluidHandler> packageHandler() {
        return PACKAGE_HANDLER;
    }

    @Override
    public BlockCapability<IFluidHandler, @Nullable Direction> getBlockCapability() {
        return Capabilities.FluidHandler.BLOCK;
    }
}
