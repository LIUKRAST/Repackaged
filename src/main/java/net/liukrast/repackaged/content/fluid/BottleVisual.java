package net.liukrast.repackaged.content.fluid;

import com.simibubi.create.content.fluids.FluidInstance;
import com.simibubi.create.content.fluids.FluidMesh;
import com.simibubi.create.content.logistics.box.PackageEntity;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import net.liukrast.deployer.lib.helper.VisualHelpers;
import net.liukrast.deployer.lib.helper.client.PackageVisualExtension;
import net.liukrast.repackaged.registry.RepackagedDataComponents;
import net.liukrast.repackaged.registry.RepackagedItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;

public class BottleVisual implements PackageVisualExtension.Entity {
    private final FluidInstance stream;
    private final TransformedInstance surface;
    private final int height;
    private final TextureAtlasSprite texture;

    public BottleVisual(VisualizationContext context, PackageEntity e, float ignored) {
        float radius = 0.5f;
        if(e.box.getItem() instanceof PackageItem packageItem) {
            radius = packageItem.style.width()/16f;
            height = packageItem.style.height();
        } else height = 16;
        var fs = getFluidStack(e.box);
        IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(fs.getFluid());
        var atlas = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
        this.texture = atlas.apply(clientFluid.getStillTexture(fs));
        stream = context.instancerProvider().instancer(AllInstanceTypes.FLUID, VisualHelpers.radiusStream(texture, radius)).createInstance();
        surface = context.instancerProvider().instancer(InstanceTypes.TRANSFORMED, FluidMesh.surface(texture, radius/2)).createInstance();
        stream.colorArgb(clientFluid.getTintColor(fs));
        surface.colorArgb(clientFluid.getTintColor(fs));
    }

    public FluidStack getFluidStack(ItemStack stack) {
        return stack.getOrDefault(RepackagedDataComponents.BOTTLE_CONTENTS, SimpleFluidContent.EMPTY).copy();
    }

    @Override
    public void beginFrame(DynamicVisual.Context context, PackageEntity packageEntity) {
        var fs = getFluidStack(packageEntity.box);
        float progress = height/16f * fs.getAmount()/1000f;
        float scaleFactor = -0.01f;

        stream.vScale = -(texture.getV1() - texture.getV0());
        stream.v0 = texture.getV0() + (texture.getV1() - texture.getV0());
        stream.progress = progress;


        stream.scale(0.5f, 1, 0.5f);
        stream.scale(1+scaleFactor);


        surface.scale(1+scaleFactor);
        surface.translateY(progress);
        surface.translateY(scaleFactor/2);

        stream.translate(1-scaleFactor, 0, 1-scaleFactor);
        surface.translate(0.5-scaleFactor/2, 0, 0.5-scaleFactor/2);
    }

    @Override
    public TransformedInstance[] createBuffer(PackageEntity packageEntity) {
        return new TransformedInstance[]{stream, surface};
    }

    @Override
    public void _delete() {
        stream.delete();
        surface.delete();
    }
}
