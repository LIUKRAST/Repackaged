package net.liukrast.repackaged.content.energy;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBox;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.liukrast.deployer.lib.logistics.IPromiseVisuals;
import net.liukrast.deployer.lib.logistics.board.PanelType;
import net.liukrast.deployer.lib.logistics.board.StockPanelBehaviour;
import net.liukrast.repackaged.registry.RepackagedItems;
import net.liukrast.repackaged.registry.RepackagedPartialModels;
import net.liukrast.repackaged.registry.RepackagedStockInventoryTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

public class EnergyPanelBehaviour extends StockPanelBehaviour<Energy, EnergyStack> implements IPromiseVisuals {
    private static final Multiplier[] MULTIPLIERS = {
            new Multiplier("⚡", 1, 10),
            new Multiplier("k⚡", 1_000),
            new Multiplier("M⚡", 1_000_000)
    };

    public EnergyPanelBehaviour(PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        super(RepackagedStockInventoryTypes.ENERGY.get(), type, be, slot);
        this.filter = new EnergyStack(0, Optional.empty());

    }

    @Override
    public Multiplier[] getMultiplierMode() {
        return MULTIPLIERS;
    }

    @Override
    public EnergyStack parseFromHeldItem(ItemStack itemStack) {
        throw new IllegalCallerException("Unreachable statement");
    }

    @Override
    public void setItem(Player player, InteractionHand hand, Direction side, BlockHitResult hitResult, boolean client) {

    }

    @Override
    public boolean isFilterEmpty() {
        return false;
    }

    @Override
    public ValueBox createBox(Component component, AABB aabb, BlockPos blockPos) {
        return new ValueBox.TextValueBox(component, aabb, blockPos, getCountLabelForValueBox());
    }

    @Override
    public MutableComponent getLabel() {
        return super.getLabel();
    }

    @Override
    public void render(float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int i1) {

    }

    @Override
    public Component getHoverName() {
        return Component.empty();
    }

    @Override
    public Item getItem() {
        return RepackagedItems.ENERGY_GAUGE.get();
    }

    @Override
    public PartialModel getModel(FactoryPanelBlock.PanelState panelState, FactoryPanelBlock.PanelType panelType) {
        return RepackagedPartialModels.ENERGY_PANEL;
    }

    @Override
    public void reset() {
        this.filter.setAmount(0);
    }

    @Override
    public Component getPromisedComponent(int amount) {
        return Component.translatable("stock_inventory_type.repackaged.energy")
                .copy().append(" x" + amount + "Mb");
    }

    @Override
    public ItemStack getPromisedBox() {
        return RepackagedItems.STANDARD_BATTERIES.getFirst().toStack();
    }

    @Override
    public int getScrollStep(boolean ctrl, boolean shift, boolean alt) {
        return ctrl ? 100 : shift ? 1000 : alt ? 1 : 10;
    }

    @Override
    public boolean shouldSnap() {
        return true;
    }
}

