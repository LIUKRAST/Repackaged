package net.liukrast.repackaged.content.logistics;

import com.simibubi.create.content.logistics.packager.PackagerItemHandler;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.liukrast.repackaged.registry.RepackagedBlockEntityTypes;
import net.liukrast.repackaged.registry.RepackagedBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PackagerConnectorBlockEntity extends SmartBlockEntity {
    private boolean protection = false;
    public PackagerConnectorBlockEntity(BlockPos pos, BlockState state) {
        super(RepackagedBlockEntityTypes.PACKAGER_CONNECTOR.get(), pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        assert level != null;
        if(level.isClientSide)
            return;
        var container = getTargetContainer();
        if(container == null || container.extractItem(0, 1, true).isEmpty())
            return;
        sendToNext(container);
    }

    public void sendToNext(PackagerItemHandler handler) {
        assert level != null;
        var be = getPointing();
        if(be == null)
            return;

        protection = true;
        be.receiveHandler(handler);
        protection = false;
    }

    public @Nullable PackagerConnectorBlockEntity getPointing() {
        assert level != null;
        var pointing = getBlockState().getValue(PackagerConnectorBlock.POINTING);
        if(pointing.getAxis().isVertical())
            return null;

        var direction = getBlockState().getValue(PackagerConnectorBlock.FACING);
        var relative = RepackagedBlocks.PACKAGER_CONNECTOR.get().getRealDirectionOut(direction, pointing);
        var otherBE = level.getBlockEntity(getBlockPos()
                .relative(relative));
        if(!(otherBE instanceof PackagerConnectorBlockEntity be))
            return null;
        return be;
    }

    public @Nullable PackagerItemHandler getTargetContainer() {
        assert level != null;
        var direction = getBlockState().getValue(PackagerConnectorBlock.FACING).getOpposite();
        var handler = level.getCapability(Capabilities.ItemHandler.BLOCK, getBlockPos().relative(direction), direction.getOpposite());
        if(!(handler instanceof PackagerItemHandler packageHandler))
            return null;
        return packageHandler;
    }

    public void receiveHandler(PackagerItemHandler itemHandler) {
        if(protection) return;

        var container = getTargetContainer();
        if(container == null) {
            sendToNext(itemHandler);
            return;
        }

        var stack = itemHandler.extractItem(0, 1, false);
        if(stack.isEmpty()) return;
        container.insertItem(0, stack, false);
    }
}
