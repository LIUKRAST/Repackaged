package net.liukrast.repackaged.content.logistics;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.simibubi.create.AllItems;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.kinetics.crafter.CrafterHelper;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBox;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.edgeInteraction.EdgeInteractionHandler;
import com.simibubi.create.foundation.blockEntity.behaviour.edgeInteraction.EdgeInteractionRenderer;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.theme.Color;
import net.liukrast.repackaged.RepackagedLang;
import net.liukrast.repackaged.registry.RepackagedBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class PackageShelfBehaviour {
    @OnlyIn(Dist.CLIENT)
    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null || mc.level == null || !(mc.hitResult instanceof BlockHitResult result)) return;

        ClientLevel world = mc.level;
        BlockPos pos = result.getBlockPos();
        Player player = mc.player;
        ItemStack heldItem = player.getMainHandItem();
        BlockState state = world.getBlockState(pos);
        var y = result.getLocation().y - pos.getY();

        if(player.isShiftKeyDown())
            return;

        if(!state.is(RepackagedBlocks.PACKAGE_SHELF))
            return;

        var state1 = world.getBlockState(y < 0.5 ? pos.below() : pos.above());

        if(!state1.is(RepackagedBlocks.PACKAGE_SHELF)) return;
        boolean connect;
        if(state.getValue(PackageShelfBlock.TYPE) != PackageShelfBlock.Type.MIDDLE) {
            var tt = state1.getValue(PackageShelfBlock.TYPE);
            if(state.getValue(PackageShelfBlock.TYPE) == PackageShelfBlock.Type.TOP) {
                if(y < 0.5) return;
                if(tt != PackageShelfBlock.Type.BOTTOM)
                    return;
            } else {
                if(y >= 0.5) return;
                if(tt != PackageShelfBlock.Type.TOP)
                    return;
            }
            connect = true;
        } else {
            connect = false;
            if(state1.getValue(PackageShelfBlock.TYPE) != PackageShelfBlock.Type.MIDDLE) return;
        }


        if(!state1.is(RepackagedBlocks.PACKAGE_SHELF)) return;

        if(!AllItems.WRENCH.isIn(heldItem))
            return;
        var dir = result.getDirection();
        if(dir.getAxis().isVertical())
            return;


        var closestEdge = y < 0.5 ? Direction.DOWN : Direction.UP;
        AABB bb = getBB(pos, closestEdge);
        boolean hit = bb.contains(result.getLocation());
        Vec3 offset = Vec3.atLowerCornerOf(closestEdge.getNormal())
                .scale(.5)
                .add(Vec3.atLowerCornerOf(dir.getNormal())
                        .scale(.469))
                .add(VecHelper.CENTER_OF_ORIGIN);

        ValueBox box = new ValueBox(CommonComponents.EMPTY, bb, pos).passive(!hit)
                .transform(new EdgeValueBoxTransform(offset))
                .wideOutline();
        Outliner.getInstance().showOutline("edge", box)
                .highlightFace(dir);

        if (!hit)
            return;

        List<MutableComponent> tip = new ArrayList<>();
        tip.add(RepackagedLang.translateDirect("logistics.shelf.connected"));
        tip.add(RepackagedLang.translateDirect(connect
                ? "logistics.shelf.click_to_merge"
                : "logistics.shelf.click_to_separate"));
        CreateClient.VALUE_SETTINGS_HANDLER.showHoverTip(tip);
    }

    private static AABB getBB(BlockPos pos, Direction direction) {
        AABB bb = new AABB(pos);
        Vec3i vec = direction.getNormal();
        int x = vec.getX();
        int y = vec.getY();
        int z = vec.getZ();
        double margin = 10 / 16f;
        double absX = Math.abs(x) * margin;
        double absY = Math.abs(y) * margin;
        double absZ = Math.abs(z) * margin;

        bb = bb.contract(absX, absY, absZ);
        bb = bb.move(absX / 2d, absY / 2d, absZ / 2d);
        bb = bb.move(x / 2d, y / 2d, z / 2d);
        bb = bb.inflate(1 / 256f);
        return bb;
    }

    private static class EdgeValueBoxTransform extends ValueBoxTransform.Sided {

        private final Vec3 add;

        public EdgeValueBoxTransform(Vec3 add) {
            this.add = add;
        }

        @Override
        protected Vec3 getSouthLocation() {
            return Vec3.ZERO;
        }

        @Override
        public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
            return add;
        }

        @Override
        public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
            super.rotate(level, pos, state, ms);
        }

    }
}
