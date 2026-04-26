package net.liukrast.repackaged.content.fluid;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBox;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.platform.NeoForgeCatnipServices;
import net.liukrast.deployer.lib.helper.box.FluidValueBox;
import net.liukrast.deployer.lib.logistics.IPromiseVisuals;
import net.liukrast.deployer.lib.logistics.board.PanelType;
import net.liukrast.deployer.lib.logistics.board.StockPanelBehaviour;
import net.liukrast.repackaged.registry.RepackagedItems;
import net.liukrast.repackaged.registry.RepackagedPackageStyles;
import net.liukrast.repackaged.registry.RepackagedPartialModels;
import net.liukrast.repackaged.registry.RepackagedStockInventoryTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidPanelBehaviour extends StockPanelBehaviour<Fluid, FluidStack> implements IPromiseVisuals {
    private static final Multiplier[] MULTIPLIERS = {
            new Multiplier("mB", 1, 10),
            new Multiplier("B", 1_000),
            new Multiplier("KB", 1_000_000)
    };

    public FluidPanelBehaviour(PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        super(RepackagedStockInventoryTypes.FLUID.get(), type, be, slot);
    }

    @Override
    public Multiplier[] getMultiplierMode() {
        return MULTIPLIERS;
    }

    @Override
    public FluidStack parseFromHeldItem(ItemStack itemStack) {
        return FilterItemStack.of(itemStack).fluid(blockEntity.getLevel());
    }

    @Override
    public Component getHoverName() {
        return getStack().getHoverName();
    }

    @Override
    public ValueBox createBox(Component component, AABB aabb, BlockPos blockPos) {
        return new FluidValueBox(component, aabb, blockPos, getStack(), getCountLabelForValueBox());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(float v, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        FluidStack fs = getStack();
        if(fs.isEmpty()) return;
        var slotPositioning = getSlotPositioning();
        var level = blockEntity.getLevel();
        BlockState state = blockEntity.getBlockState();
        var pos = blockEntity.getBlockPos();
        if(slotPositioning.shouldRender(level, pos, state)) {
            ms.pushPose();
            slotPositioning.transform(level, pos, state, ms);
            ms.mulPose(Axis.XP.rotationDegrees(-90));
            ms.translate(-4/16f, 0, -4/16f);

            NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox(
                    fs, 0, 0, 0, 8/16f, 1/16f, 8/16f, buffer,
                    ms, light, false, true);
            ms.popPose();
        }
    }

    @Override
    public Item getItem() {
        return RepackagedItems.FLUID_GAUGE.get();
    }

    @Override
    public PartialModel getModel(FactoryPanelBlock.PanelState panelState, FactoryPanelBlock.PanelType panelType) {
        return RepackagedPartialModels.FLUID_PANEL;
    }

    @Override
    public MutableComponent formatValue(ValueSettings value) {
        return super.formatValue(value);
    }

    @Override
    public Component getPromisedComponent(int amount) {
        return filter.getHoverName().copy().append(" x" + amount + "Mb");
    }

    @Override
    public ItemStack getPromisedBox() {
        return RepackagedItems.STANDARD_BOTTLES.getFirst().toStack();
    }
}
