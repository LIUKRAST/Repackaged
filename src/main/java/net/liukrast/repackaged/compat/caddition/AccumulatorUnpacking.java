package net.liukrast.repackaged.compat.caddition;

import com.mrh0.createaddition.energy.InternalEnergyStorage;
import net.liukrast.deployer.lib.logistics.packager.AbstractPackagerBlockEntity;
import net.liukrast.deployer.lib.logistics.packager.GenericUnpackingHandler;
import net.liukrast.deployer.lib.logistics.stockTicker.GenericOrderContained;
import net.liukrast.repackaged.content.energy.Energy;
import net.liukrast.repackaged.content.energy.EnergyStack;
import net.liukrast.repackaged.registry.RepackagedStockInventoryTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public enum AccumulatorUnpacking implements GenericUnpackingHandler<Energy, EnergyStack, IEnergyStorage> {
    INSTANCE;

    @Override
    public boolean unpack(Level level, BlockPos pos, BlockState state, Direction side, List<EnergyStack> items, @Nullable GenericOrderContained<EnergyStack> orderContext, boolean simulate, AbstractPackagerBlockEntity<Energy, EnergyStack, IEnergyStorage> packager) {
        BlockEntity targetBE = level.getBlockEntity(pos);
        if(targetBE == null) return false;

        IEnergyStorage energyStorage = level.getCapability(RepackagedStockInventoryTypes.ENERGY.get().getBlockCapability(), pos, state, targetBE, null);
        if(!(energyStorage instanceof InternalEnergyStorage internal))
            return false;

        int total = 0;
        for(EnergyStack stack : items) {
            total+=stack.getAmount();
        }
        if(internal.getSpace() < total) return false;
        internal.internalProduceEnergy(total);
        return true;
    }
}
