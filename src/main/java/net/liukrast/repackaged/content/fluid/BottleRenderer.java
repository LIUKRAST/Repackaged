package net.liukrast.repackaged.content.fluid;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.createmod.catnip.platform.NeoForgeCatnipServices;
import net.liukrast.deployer.lib.logistics.packager.GenericPackageItem;
import net.liukrast.repackaged.registry.RepackagedDataComponents;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;

public class BottleRenderer extends CustomRenderedItemModelRenderer {

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        renderer.render(model.getOriginalModel(), light);
        render(stack, ms, buffer, light);
    }

    public static void render(ItemStack stack, PoseStack ms, MultiBufferSource buffer, int light) {
        if(!(stack.getItem() instanceof GenericPackageItem gpi)) return;
        FluidStack fs = stack.getOrDefault(RepackagedDataComponents.BOTTLE_CONTENTS, SimpleFluidContent.EMPTY).copy();
        if(fs.isEmpty()) return;
        float w = gpi.style.width()/16f;
        float h = gpi.style.height()/16f * fs.getAmount()/1000;

        float x = -gpi.style.width()/32f;
        float y = -1/2f;

        float shrink = 0.01f;

        ms.pushPose();
        NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox(fs, x+shrink, y+shrink, x+shrink, w+x-shrink, h+y-shrink, w+x-shrink, buffer,
                ms, light, false, true);
        ms.popPose();
    }
}
