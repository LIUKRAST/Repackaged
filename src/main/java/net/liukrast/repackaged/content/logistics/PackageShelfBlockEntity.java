package net.liukrast.repackaged.content.logistics;

import com.simibubi.create.compat.computercraft.events.RepackageEvent;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.packager.repackager.RepackagerBlockEntity;
import net.createmod.catnip.codecs.CatnipCodecUtils;
import net.createmod.catnip.nbt.NBTHelper;
import net.liukrast.deployer.lib.logistics.packager.GenericPackageItem;
import net.liukrast.deployer.lib.mixinExtensions.RBEExtension;
import net.liukrast.repackaged.registry.RepackagedBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.List;

public class PackageShelfBlockEntity extends RepackagerBlockEntity {
    private int size = 2;

    public PackageShelfBlockEntity(BlockPos pos, BlockState state) {
        super(RepackagedBlockEntityTypes.PACKAGE_SHELF.get(), pos, state);
        repackageHelper = new PackageShelfHelper(this::getSize);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        size = compound.getInt("PackageSize");
        if(!clientPacket) return;
        queuedExitingPackages = NBTHelper.readCompoundList(compound.getList("QueuedExitingPackages", Tag.TAG_COMPOUND),
                c -> CatnipCodecUtils.decode(BigItemStack.CODEC, registries, c)
                        .orElseThrow());
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putInt("PackageSize", size);
        if(!clientPacket) return;
        compound.put("QueuedExitingPackages", NBTHelper.writeCompoundList(queuedExitingPackages, bis -> {
            if (CatnipCodecUtils.encode(BigItemStack.CODEC, registries, bis)
                    .orElse(new CompoundTag()) instanceof CompoundTag ct)
                return ct;
            return new CompoundTag();
        }));
    }
    public void updateShelfSize() {
        int height = 1;
        BlockPos current = this.worldPosition.above();
        assert level != null;
        while (level.getBlockState(current).getBlock() instanceof PackageShelfBlock) {
            height++;
            if(level.getBlockState(current).getValue(PackageShelfBlock.TYPE) == PackageShelfBlock.Type.TOP)
                break;
            current = current.above();
        }
        size = Math.max(2, height);
        int toRemove = queuedExitingPackages.size()-size+1;
        if(toRemove > 0) dropExcessItems(toRemove);
        notifyUpdate();
    }

    private void dropExcessItems(int toRemove) {
        assert level != null;
        for(int i = 0; i < toRemove; i++) {
            var bigStack = queuedExitingPackages.getLast();
            for (int j = 0; j < bigStack.count; j++)
                Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                        bigStack.stack.copy());
            queuedExitingPackages.removeLast();
        }
    }

    public int getSize() {
        return size;
    }

    // Method is different because superclass is modified through mixin
    @Override
    protected void attemptToRepackage(IItemHandler targetInv) {
        assert level != null;
        repackageHelper.clear();
        int completedOrderId = -1;

        for (int slot = 0; slot < targetInv.getSlots(); slot++) {
            ItemStack extracted = targetInv.extractItem(slot, 1, true);
            if (extracted.isEmpty() || !PackageItem.isPackage(extracted))
                continue;
            boolean fragmented = extracted.getItem() instanceof GenericPackageItem ?
                    ((RBEExtension)this).deployer$getGenericRepackageHelper().isFragmented(extracted)
                    : repackageHelper.isFragmented(extracted);

            if (!fragmented) {
                targetInv.extractItem(slot, 1, false);
                heldBox = extracted.copy();
                animationInward = false;
                animationTicks = CYCLE;
                notifyUpdate();
                return;
            }

            completedOrderId = repackageHelper.addPackageFragment(extracted);
            if (completedOrderId != -1)
                break;
        }

        if (completedOrderId == -1)
            return;

        List<BigItemStack> boxesToExport = repackageHelper.repack(completedOrderId, level.getRandom());

        for (int slot = 0; slot < targetInv.getSlots(); slot++) {
            ItemStack extracted = targetInv.extractItem(slot, 1, true);
            if (extracted.isEmpty() || !PackageItem.isPackage(extracted))
                continue;
            if (PackageItem.getOrderId(extracted) != completedOrderId)
                continue;
            targetInv.extractItem(slot, 1, false);
        }

        if (boxesToExport.isEmpty())
            return;

        if (computerBehaviour.hasAttachedComputer()) {
            for (BigItemStack box : boxesToExport) {
                computerBehaviour.prepareComputerEvent(new RepackageEvent(box.stack, box.count));
            }
        }
        queuedExitingPackages.addAll(boxesToExport);
        notifyUpdate();
    }
}
