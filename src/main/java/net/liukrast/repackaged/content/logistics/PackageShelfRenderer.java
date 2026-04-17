package net.liukrast.repackaged.content.logistics;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.packager.PackagerBlock;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static com.simibubi.create.content.logistics.packager.PackagerRenderer.getHatchModel;
import static com.simibubi.create.content.logistics.packager.PackagerRenderer.getTrayModel;

public class PackageShelfRenderer extends SmartBlockEntityRenderer<PackageShelfBlockEntity> {
    public PackageShelfRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(PackageShelfBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

        ItemStack renderedBox = be.getRenderedBox();
        float trayOffset = be.getTrayOffset(partialTicks);
        BlockState blockState = be.getBlockState();
        Direction facing = blockState.getValue(PackagerBlock.FACING)
                .getOpposite();

        if (!VisualizationManager.supportsVisualization(be.getLevel())) {
            var hatchModel = getHatchModel(be);

            SuperByteBuffer sbb = CachedBuffers.partial(hatchModel, blockState);
            sbb.translate(Vec3.atLowerCornerOf(facing.getNormal())
                            .scale(.49999f))
                    .rotateYCenteredDegrees(AngleHelper.horizontalAngle(facing))
                    .rotateXCenteredDegrees(AngleHelper.verticalAngle(facing))
                    .light(light)
                    .renderInto(ms, buffer.getBuffer(RenderType.solid()));

            sbb = CachedBuffers.partial(getTrayModel(blockState), blockState);
            sbb.translate(Vec3.atLowerCornerOf(facing.getNormal())
                            .scale(trayOffset))
                    .rotateYCenteredDegrees(facing.toYRot())
                    .light(light)
                    .renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));
        }

        if (!renderedBox.isEmpty()) {
            ms.pushPose();
            var msr = TransformStack.of(ms);
            msr.translate(Vec3.atLowerCornerOf(facing.getNormal())
                            .scale(trayOffset))
                    .translate(.5f, .5f, .5f)
                    .rotateYDegrees(facing.toYRot())
                    .translate(0, 10 / 16f, 0)
                    .scale(1.49f, 1.49f, 1.49f);
            Minecraft.getInstance()
                    .getItemRenderer()
                    .renderStatic(null, renderedBox, ItemDisplayContext.FIXED, false, ms, buffer, be.getLevel(), light,
                            overlay, 0);
            ms.popPose();
        }

        var que = be.queuedExitingPackages;
        var item = be.heldBox.getItem();
        ms.pushPose();
        if(item instanceof PackageItem pi)
            ms.translate(0, pi.style.height()/16f, 0);
        ms.translate(.5f, .5f, .5f);
        ms.translate(0, 7 / 16f, 0);

        ms.scale(1.49f, 1.49f, 1.49f);
        for (com.simibubi.create.content.logistics.BigItemStack itemStack : que) {
            ms.mulPose(Axis.YP.rotationDegrees(50));
            var stack = itemStack.stack;
            if (!(stack.getItem() instanceof PackageItem pi)) continue;
            Minecraft.getInstance()
                    .getItemRenderer()
                    .renderStatic(null, stack, ItemDisplayContext.FIXED, false, ms, buffer, be.getLevel(), light, overlay, 0);
            ms.translate(0, pi.style.height() / 32f, 0);
        }
        ms.popPose();
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(@NotNull PackageShelfBlockEntity blockEntity) {
        return super.getRenderBoundingBox(blockEntity).expandTowards(0, blockEntity.getSize()-1, 0);
    }
}
