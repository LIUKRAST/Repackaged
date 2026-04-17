package net.liukrast.repackaged.content.fluid;

import com.simibubi.create.content.fluids.FluidInstance;
import com.simibubi.create.content.fluids.FluidMesh;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.visual.util.SmartRecycler;
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

public class BottleChainVisual implements PackageVisualExtension.ChainConveyor {
    private final SmartRecycler<Key, FluidInstance> stream;
    private final SmartRecycler<Key, TransformedInstance> surface;

    public BottleChainVisual(VisualizationContext context, ChainConveyorBlockEntity ignored, float ignored1) {
        stream = new SmartRecycler<>(key -> context.instancerProvider().instancer(AllInstanceTypes.FLUID, VisualHelpers.radiusStream(key.sprite, key.radius))
                .createInstance());
        surface = new SmartRecycler<>(key -> context.instancerProvider().instancer(InstanceTypes.TRANSFORMED, FluidMesh.surface(key.sprite, key.radius/2))
                .createInstance());
    }

    @Override
    public void beginFrame$start() {
        stream.resetCount();
        surface.resetCount();
    }

    @Override
    public void beginFrame$end() {
        stream.discardExtra();
        surface.discardExtra();
    }

    public FluidStack getFluidStack(ItemStack stack) {
        return stack.getOrDefault(RepackagedDataComponents.BOTTLE_CONTENTS, SimpleFluidContent.EMPTY).copy();
    }

    @Override
    public TransformedInstance[] createBuffer(ChainConveyorPackage box, ChainConveyorPackage.ChainConveyorPackagePhysicsData physicsData, PackageVisualExtension.PostProcessor postProcessor) {
        if(RepackagedItems.STANDARD_BOTTLES.stream().noneMatch(def -> box.item.is(def.get()))) return EMPTY;
        if(!(box.item.getItem() instanceof PackageItem packageItem)) return EMPTY;
        var fs = getFluidStack(box.item);
        if(fs.isEmpty()) return EMPTY;

        TransformedInstance[] buffers = new TransformedInstance[2];
        IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(fs.getFluid());
        var atlas = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
        TextureAtlasSprite texture = atlas.apply(clientFluid.getStillTexture(fs));

        var key = new Key(texture, packageItem.style.width()/16f);

        var stream = this.stream.get(key);
        stream.vScale = -(texture.getV1() - texture.getV0());
        stream.v0 = texture.getV0() + (texture.getV1() - texture.getV0());

        stream.progress = packageItem.style.height()/16f * fs.getAmount()/1000f;
        stream.colorArgb(clientFluid.getTintColor(fs));
        buffers[0] = stream;


        var surface = this.surface.get(key);
        surface.colorArgb(clientFluid.getTintColor(fs));
        buffers[1] = surface;
        float scaleFactor = -.01f;

        postProcessor.subscribe(stream, str -> {
            str.scale(1+scaleFactor);
            str.scale(0.5f, 1, 0.5f);
            str.translate(0.5, scaleFactor/2, 0.5);
        });

        postProcessor.subscribe(surface, sur -> {
            float savedScale = stream.progress;
            sur.scale(1+scaleFactor, 1, 1+scaleFactor);
            sur.translate(0.5, savedScale + scaleFactor/2, 0.5);
        });

        return buffers;
    }

    @Override
    public void _delete() {
        stream.delete();
        surface.delete();
    }

    private record Key(TextureAtlasSprite sprite, float radius) {}
}
