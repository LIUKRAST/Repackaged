package net.liukrast.repackaged.content.fluid;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.liukrast.deployer.lib.logistics.board.AbstractPanelBehaviour;
import net.liukrast.deployer.lib.logistics.board.PanelType;
import net.liukrast.repackaged.registry.RepackagedItems;
import net.liukrast.repackaged.registry.RepackagedPartialModels;
import net.minecraft.world.item.Item;

public class FluidPanelBehaviour extends AbstractPanelBehaviour {
    public FluidPanelBehaviour(PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        super(type, be, slot);
    }

    @Override
    public void addConnections(PanelConnectionBuilder panelConnectionBuilder) {

    }

    @Override
    public Item getItem() {
        return RepackagedItems.FLUID_GAUGE.get();
    }

    @Override
    public PartialModel getModel(FactoryPanelBlock.PanelState panelState, FactoryPanelBlock.PanelType panelType) {
        return RepackagedPartialModels.FLUID_PANEL;
    }
}
