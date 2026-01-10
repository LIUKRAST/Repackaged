package net.liukrast.repackaged.content.fluid;

import com.simibubi.create.foundation.blockEntity.behaviour.inventory.CapManipulationBehaviourBase;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.TankManipulationBehaviour;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.liukrast.deployer.lib.logistics.packager.AbstractPackagerBlockEntity;
import net.liukrast.deployer.lib.logistics.packager.StockInventoryType;
import net.liukrast.repackaged.registry.RepackagedBlockEntityTypes;
import net.liukrast.repackaged.registry.RepackagedStockInventoryTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidPackagerBlockEntity extends AbstractPackagerBlockEntity<Fluid, FluidStack, IFluidHandler> {
    public FluidPackagerBlockEntity(BlockPos pos, BlockState state) {
        super(RepackagedBlockEntityTypes.FLUID_PACKAGER.get(), pos, state);
    }

    @Override
    protected CapManipulationBehaviourBase<IFluidHandler, ? extends CapManipulationBehaviourBase<?, ?>> createTargetInventory() {
        return new TankManipulationBehaviour(this, CapManipulationBehaviourBase.InterfaceProvider.oppositeOfBlockFacing())
                .withFilter(this::supportsBlockEntity);
    }

    @Override
    public StockInventoryType<Fluid, FluidStack, IFluidHandler> getStockType() {
        return RepackagedStockInventoryTypes.FLUID.get();
    }

    @Override
    public PartialModel getHatchModel(boolean isHatchOpen, PartialModel original) {
        return super.getHatchModel(isHatchOpen, original);
    }
}
