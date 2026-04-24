package net.liukrast.repackaged.content.energy;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.packager.PackagerBlock;
import com.simibubi.create.content.logistics.packager.PackagerItemHandler;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.CapManipulationBehaviourBase;
import net.liukrast.deployer.lib.logistics.OrderStockTypeData;
import net.liukrast.deployer.lib.logistics.packager.AbstractPackagerBlockEntity;
import net.liukrast.deployer.lib.logistics.packager.GenericPackageItem;
import net.liukrast.deployer.lib.logistics.packager.GenericPackagingRequest;
import net.liukrast.deployer.lib.logistics.packager.StockInventoryType;
import net.liukrast.deployer.lib.mixin.accessors.PackagerBlockEntityAccessor;
import net.liukrast.deployer.lib.registry.DeployerDataComponents;
import net.liukrast.repackaged.Repackaged;
import net.liukrast.repackaged.registry.RepackagedBlockEntityTypes;
import net.liukrast.repackaged.registry.RepackagedStockInventoryTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class BatteryChargerBlockEntity extends AbstractPackagerBlockEntity<Energy, EnergyStack, IEnergyStorage> implements IHaveGoggleInformation {
    protected List<Integer> requestedAmountQueue = new LinkedList<>();
    protected boolean isCharging = false;
    protected boolean isUnwrappingEnergy = false;

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        Repackaged.CONSTANTS
                .translate("gui.battery_charger.info_header")
                .forGoggles(tooltip, 0);
        Repackaged.CONSTANTS
                .translate("gui.battery_charger.status")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip, 0);
        Repackaged.CONSTANTS
                .translate(isCharging ? "gui.battery_charger.status.charging" : isUnwrappingEnergy ? "gui.battery_charger.status.discharging" : "gui.battery_charger.status.idle")
                .style(ChatFormatting.AQUA)
                .forGoggles(tooltip, 1);

        IEnergyStorage batteryStorage = getRenderedBox().getCapability(Capabilities.EnergyStorage.ITEM);
        if (batteryStorage == null)
            return true;
        Repackaged.CONSTANTS
                .translate("gui.battery_charger.progress")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip, 0);
        Repackaged.CONSTANTS
                .text(batteryStorage.getEnergyStored() + "⚡")
                .style(ChatFormatting.GOLD)
                .add(Repackaged.CONSTANTS.text("/").style(ChatFormatting.GRAY))
                .add(Repackaged.CONSTANTS.text(batteryStorage.getMaxEnergyStored() + "⚡").style(ChatFormatting.DARK_GRAY))
                .forGoggles(tooltip, 1);

        return true;
    }

    public BatteryChargerBlockEntity(BlockPos pos, BlockState state) {
        super(RepackagedBlockEntityTypes.BATTERY_CHARGER.get(), pos, state);
    }

    @Override
    protected CapManipulationBehaviourBase<IEnergyStorage, ? extends CapManipulationBehaviourBase<?, ?>> createTargetInventory() {
        return new EnergyManipulationBehaviour(this, CapManipulationBehaviourBase.InterfaceProvider.oppositeOfBlockFacing())
                .withFilter(this::supportsBlockEntity);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isUnwrappingEnergy() {
        return isUnwrappingEnergy;
    }

    @Override
    public StockInventoryType<Energy, EnergyStack, IEnergyStorage> getStockType() {
        return RepackagedStockInventoryTypes.ENERGY.get();
    }

    @Override
    public PackagerItemHandler createItemHandler() {
        return new PackagerItemHandler(this) {
            @Override
            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                if(isCharging)
                    return ItemStack.EMPTY;
                return super.extractItem(slot, amount, simulate);
            }
        };
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        isCharging = compound.getBoolean("IsCharging");
        requestedAmountQueue.clear();
        for(var tag : compound.getList("QueueAmounts", CompoundTag.TAG_INT)) {
            if(!(tag instanceof IntTag i)) continue;
            requestedAmountQueue.add(i.getAsInt());
        }
        isUnwrappingEnergy = compound.getBoolean("isUnwrappingEnergy");

    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putBoolean("IsCharging", isCharging);
        ListTag listTag = new ListTag();
        for(var v : requestedAmountQueue) {
            listTag.add(IntTag.valueOf(v));
        }
        compound.put("QueueAmounts", listTag);
        compound.putBoolean("isUnwrappingEnergy", isUnwrappingEnergy);
    }

    @Override
    public void tick() {
        ItemStack cached = previouslyUnwrapped;
        super.tick();
        if(isUnwrappingEnergy) {
            previouslyUnwrapped = cached;
        }
    }

    @Override
    public ItemStack getRenderedBox() {
        if(isUnwrappingEnergy)
            return previouslyUnwrapped;
        return super.getRenderedBox();
    }

    @Override
    public void lazyTick() {
        assert level != null;
        if (level.isClientSide())
            return;
        recheckIfLinksPresent();
        if(tickUnwrapper()) return;
        if(requestedAmountQueue.isEmpty()) {
            if (!redstonePowered) {
                boolean fl = isCharging;
                isCharging = false;
                if(fl) notifyUpdate();
                return;
            }
            redstonePowered = getBlockState().getOptionalValue(PackagerBlock.POWERED)
                    .orElse(false);
            if (!redstoneModeActive()) {
                boolean fl = isCharging;
                isCharging = false;
                if(fl) notifyUpdate();
                return;
            }
            if(heldBox.isEmpty()) attemptToSendSpecial(null);
            else attemptToRedstoneCharge();
        } else {
            // If player extracts package before it's done extracting, the request is canceled
            int toRemove = requestedAmountQueue.size() - queuedExitingPackages.size()-1;
            if (toRemove > 0) requestedAmountQueue.subList(0, toRemove).clear();
            attemptToCharge();
        }
        updateSignAddress();

    }

    public boolean tickUnwrapper() {
        if (isUnwrappingEnergy && !previouslyUnwrapped.isEmpty()) {
            return unwrapBox(previouslyUnwrapped, false);
        }
        return false;
    }

    @Override
    public void attemptToSendSpecial(@Nullable List<GenericPackagingRequest<EnergyStack>> queuedRequests, int index, boolean isFinal) {
        if(queuedRequests != null) attemptToLoadRequest(queuedRequests, index, isFinal);
        else attemptToRedstoneLoad();
    }

    public void attemptToRedstoneLoad() {
        // 1. Load energy storages
        if(animationTicks != 0 || buttonCooldown > 0)
            return;
        if(!heldBox.isEmpty())
            return;
        IEnergyStorage targetInv = targetInventory.getInventory();
        if (targetInv == null || !targetInv.canExtract() || targetInv.getEnergyStored() == 0)
            return;
        ItemStack created = getStockType().packageHandler().getRandomBox();

        if(!signBasedAddress.isBlank()) {
            PackageItem.clearAddress(created);
            PackageItem.addAddress(created, signBasedAddress);
        }
        this.heldBox = created;
        animationInward = false;
        animationTicks = CYCLE;

        attemptToRedstoneCharge();
        ((PackagerBlockEntityAccessor) this).getAdvancement().awardPlayer(AllAdvancements.PACKAGER);
        triggerStockCheck();
        notifyUpdate();
    }


    /**
     * Loads the packages in the queue
     * */
    public void attemptToLoadRequest(List<GenericPackagingRequest<EnergyStack>> queuedRequests, int index, boolean isFinal) {
        // 1. Load energy storages
        IEnergyStorage targetInv = targetInventory.getInventory();
        if (targetInv == null || !targetInv.canExtract() || targetInv.getEnergyStored() == 0)
            return;
        if(queuedRequests.isEmpty())
            return;
        ItemStack created = getStockType().packageHandler().getRandomBox();
        IEnergyStorage batteryStorage = created.getCapability(Capabilities.EnergyStorage.ITEM);
        if (batteryStorage == null)
            return;

        // 2. Load the current first request
        GenericPackagingRequest<EnergyStack> request = queuedRequests.getFirst();
        if (request == null || request.isEmpty()) return;

        // 3. Calculate the amount this box can and will export
        int requestedAmount = request.getCount();
        int boxCapacity = batteryStorage.getMaxEnergyStored();
        int availableInStorage = targetInv.getEnergyStored();

        int thisBoxAmount = Math.min(requestedAmount, Math.min(boxCapacity, availableInStorage));

        // 4. Box metadata
        boolean finalPackageAtLink = thisBoxAmount >= requestedAmount;
        String address = request.address();
        int orderID = request.orderId();
        int linkIndexInOrder = request.linkIndex();
        boolean finalLinkInOrder = request.finalLink()
                .booleanValue();
        int packageIndexAtLink = request.packageCounter()
                .getAndIncrement();

        // 5. Setting metadata
        PackageItem.clearAddress(created);
        PackageItem.addAddress(created, address);
        GenericPackageItem.setOrder(
                getStockType(),
                created,
                orderID,
                linkIndexInOrder,
                finalLinkInOrder,
                packageIndexAtLink,
                finalPackageAtLink,
                request.context()
        );
        created.set(DeployerDataComponents.ORDER_STOCK_TYPE_DATA, new OrderStockTypeData(index, isFinal));

        // 6. Decrement request, we fulfilled it
        requestedAmountQueue.add(thisBoxAmount);
        request.subtract(thisBoxAmount);
        if(request.isEmpty())
            queuedRequests.removeFirst();

        // 7. Setting the package in the queue
        if(!heldBox.isEmpty()) {
            queuedExitingPackages.add(new BigItemStack(created, 1));
        } else {
            heldBox = created;
            animationInward = false;
            animationTicks = CYCLE;
            attemptToCharge();
        }
        ((PackagerBlockEntityAccessor) this).getAdvancement().awardPlayer(AllAdvancements.PACKAGER);
        triggerStockCheck();
        notifyUpdate();
    }

    public void attemptToCharge() {
        IEnergyStorage targetInv = targetInventory.getInventory();
        if (targetInv == null || !targetInv.canExtract() || targetInv.getEnergyStored() == 0)
            return;
        IEnergyStorage batteryStorage = heldBox.getCapability(Capabilities.EnergyStorage.ITEM);
        if (batteryStorage == null) return;
        if(batteryStorage.getEnergyStored() == batteryStorage.getMaxEnergyStored())
            return;
        isCharging = true;

        int toExtract = requestedAmountQueue.getFirst();
        int extracted = targetInv.extractEnergy(toExtract, false);
        batteryStorage.receiveEnergy(extracted, false);
        playChargeEffect();
        requestedAmountQueue.set(0, toExtract - extracted);
        if(requestedAmountQueue.getFirst() == 0) {
            requestedAmountQueue.removeFirst();
            isCharging = false;
        }

        notifyUpdate();
    }

    public void attemptToRedstoneCharge() {
        IEnergyStorage targetInv = targetInventory.getInventory();
        if (targetInv == null || !targetInv.canExtract() || targetInv.getEnergyStored() == 0)
            return;
        IEnergyStorage batteryStorage = heldBox.getCapability(Capabilities.EnergyStorage.ITEM);
        if (batteryStorage == null) return;
        if(batteryStorage.getEnergyStored() == batteryStorage.getMaxEnergyStored())
            return;
        isCharging = true;

        int toExtract = batteryStorage.getMaxEnergyStored() - batteryStorage.getEnergyStored();
        int extracted = targetInv.extractEnergy(toExtract, false);
        batteryStorage.receiveEnergy(extracted, false);
        playChargeEffect();

        if(batteryStorage.getEnergyStored() == batteryStorage.getMaxEnergyStored() || extracted == 0) {
            isCharging = false;
        }
        notifyUpdate();
    }


    private void playChargeEffect() {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.WAX_OFF,
                    worldPosition.getX() + 0.5,
                    worldPosition.getY() + 0.7,
                    worldPosition.getZ() + 0.5,
                    9,
                    0.2,
                    0.2,
                    0.2,
                    0.05
            );
            level.playSound(null, worldPosition,
                    SoundEvents.COPPER_BREAK,
                    SoundSource.BLOCKS,
                    0.5f,
                    1.5f + level.random.nextFloat() * 0.5f
            );
        }
    }

    private void playDischargeEffect() {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.WAX_ON,
                    worldPosition.getX() + 0.5,
                    worldPosition.getY() + 0.7,
                    worldPosition.getZ() + 0.5,
                    9,
                    0.2,
                    0.2,
                    0.2,
                    0.05
            );
            level.playSound(null, worldPosition,
                    SoundEvents.COPPER_BULB_BREAK,
                    SoundSource.BLOCKS,
                    0.5f,
                    1.5f + level.random.nextFloat() * 0.5f
            );
        }
    }

    @Override
    public boolean safeUnwrapBox(ItemStack box, boolean simulate) {
        if(!previouslyUnwrapped.isEmpty() && previouslyUnwrapped != box)
            return false;

        IEnergyStorage batteryStorage = box.getCapability(Capabilities.EnergyStorage.ITEM);
        if (batteryStorage == null)
            return false;

        if (batteryStorage.getEnergyStored() == 0)
            return true;

        IEnergyStorage targetInv = targetInventory.getInventory();
        if (targetInv == null || !targetInv.canReceive())
            return false;

        if (simulate)
            return targetInv.receiveEnergy(batteryStorage.getEnergyStored(), true) > 0;

        isUnwrappingEnergy = true;

        int received = targetInv.receiveEnergy(batteryStorage.getEnergyStored(), false);
        if(received == 0) {
            isUnwrappingEnergy = false;
            notifyUpdate();
            return false;
        }
        batteryStorage.extractEnergy(received, false);
        playDischargeEffect();
        if(previouslyUnwrapped != box) {
            animationInward = true;
            animationTicks = CYCLE;
        }
        previouslyUnwrapped = box;

        if(batteryStorage.getEnergyStored() == 0) {
            isUnwrappingEnergy = false;
            notifyUpdate();
            return true;
        }
        notifyUpdate();
        return false;
    }
}
