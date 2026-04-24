package net.liukrast.repackaged.content.energy;

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
import net.liukrast.repackaged.RepackagedConfig;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class EnergyStockInventoryType extends StockInventoryType<Energy, EnergyStack, IEnergyStorage> {
    private static final Codec<GenericRequestPromise<EnergyStack>> REQUEST_CODEC = GenericRequestPromise.simpleCodec(EnergyStack.CODEC);

    public EnergyStockInventoryType() {
        defaultUnpackProcedure = (level, pos, state, side, items, orderContext, simulate, packager) -> {
            BlockEntity targetBE = level.getBlockEntity(pos);
            if(targetBE == null) return false;

            IEnergyStorage energyStorage = level.getCapability(getBlockCapability(), pos, state, targetBE, null);
            if(energyStorage == null)
                return false;

            int total = 0;
            for(EnergyStack stack : items) {
                total+=stack.getAmount();
            }
            return energyStorage.receiveEnergy(total, simulate) >= total;
        };
    }

    public static final Hash.Strategy<? super EnergyStack> ENERGY_STACK =
            new Hash.Strategy<>() {

                @Override
                public int hashCode(EnergyStack stack) {
                    return stack.getOwner().map(String::hashCode).orElse(0);
                }

                @Override
                public boolean equals(EnergyStack a, EnergyStack b) {
                    if(a.getOwner().isEmpty()) return true;
                    if(b.getOwner().isEmpty()) return true;
                    return b.getOwner().equals(a.getOwner());
                }
            };

    private static final IValueHandler<Energy, EnergyStack, IEnergyStorage> VALUE_HANDLER = new IValueHandler<>(EnergyStack.CODEC, EnergyStack.STREAM_CODEC) {

        @Override
        public Hash.Strategy<? super EnergyStack> hashStrategy() {
            return ENERGY_STACK;
        }

        @Override
        public Energy fromValue(EnergyStack key) {
            return Energy.INSTANCE;
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
            return new EnergyStack(amount, Optional.empty());
        }

        @Override
        public void shrink(EnergyStack stack, int amount) {
            stack.setAmount(stack.getAmount() - amount);
        }

        @Override
        public EnergyStack copyWithCount(EnergyStack stack, int amount) {
            return new EnergyStack(amount, stack.getOwner());
        }

        @Override
        public EnergyStack copy(EnergyStack stack) {
            return new EnergyStack(stack.getAmount(), stack.getOwner());
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
            return new EnergyStack(handler.getEnergyStored(), Optional.empty());
        }

        @Override
        public int maxCountPerSlot() {
            return RepackagedConfig.Server.MAX_BATTERY_ENERGY.getAsInt();
        }

        @Override
        public EnergyStack extract(IEnergyStorage handler, EnergyStack value, boolean simulate, AbstractPackagerBlockEntity<Energy, EnergyStack, IEnergyStorage> packager) {
            if(!handler.canExtract()) return EnergyStack.EMPTY;
            return new EnergyStack(handler.extractEnergy(value.getAmount(), simulate), Optional.of(""));
        }

        @Override
        public int fill(IEnergyStorage handler, EnergyStack value, boolean simulate, AbstractPackagerBlockEntity<Energy, EnergyStack, IEnergyStorage> packager) {
            if(!handler.canReceive()) return 0;
            return value.getAmount() - handler.receiveEnergy(value.getAmount(), simulate);
        }

        @Override
        public EnergyStack setInSlot(IEnergyStorage handler, int slot, EnergyStack value, boolean simulate) {
            int result = fill(handler, value, simulate, null);
            return new EnergyStack(result, value.getOwner());
        }

        @Override
        public boolean isBulky(Energy key) {
            return false;
        }

        @Override
        public IEnergyStorage create(int i) {
            return new EnergyStorage(RepackagedConfig.Server.MAX_BATTERY_ENERGY.getAsInt());
        }

        @Override
        public int getMaxPackageSlots() {
            return 1;
        }

        @Override
        public EnergyStack insertItem(IEnergyStorage handler, int i, EnergyStack stack, boolean simulate) {
            if(!handler.canReceive()) return EnergyStack.EMPTY;
            return new EnergyStack(stack.getAmount() - handler.receiveEnergy(stack.getAmount(), simulate), stack.getOwner());
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
            List<DeferredItem<GenericPackageItem>> pool = !RepackagedItems.RARE_BATTERIES.isEmpty() && STYLE_PICKER.nextInt(RARE_CHANCE) == 0 ? RepackagedItems.RARE_BATTERIES : RepackagedItems.STANDARD_BATTERIES;
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
        public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, List<Component> tooltipComponents, TooltipFlag tooltipFlag, IEnergyStorage handler) {
            tooltipComponents.add(Component.literal(handler.getEnergyStored() + "⚡").withStyle(ChatFormatting.GRAY));
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

    @Override
    public BlockCapability<IEnergyStorage, @Nullable Direction> getBlockCapability() {
        return Capabilities.EnergyStorage.BLOCK;
    }
}
