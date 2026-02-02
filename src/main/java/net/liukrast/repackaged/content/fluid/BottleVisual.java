package net.liukrast.repackaged.content.fluid;

import com.simibubi.create.content.fluids.FluidMesh;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.visual.util.SmartRecycler;
import net.liukrast.repackaged.registry.RepackagedDataComponents;
import net.liukrast.repackaged.registry.RepackagedItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.SimpleFluidContent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class BottleVisual {
    private static final TransformedInstance[] EMPTY = new TransformedInstance[]{};
    private final SmartRecycler<TextureAtlasSprite, TransformedInstance> surface;

    public BottleVisual(VisualizationContext context, ChainConveyorBlockEntity be, float pt) {
        surface = new SmartRecycler<>(key -> context.instancerProvider()
                .instancer(InstanceTypes.TRANSFORMED, FluidMesh.surface(key, 1)).createInstance());
    }

    public void beginFrame$Start() {
        surface.resetCount();
    }

    public void beginFrame$End() {
        surface.discardExtra();
    }

    public TransformedInstance[] createBuffer(
            ChainConveyorPackage box,
            ChainConveyorPackage.ChainConveyorPackagePhysicsData physicsData
    ) {
        if(RepackagedItems.STANDARD_BOTTLES.stream().noneMatch(def -> box.item.is(def.get()))) return EMPTY;
        var fs = box.item.getOrDefault(RepackagedDataComponents.BOTTLE_CONTENTS, SimpleFluidContent.EMPTY).copy();
        if(fs.isEmpty()) return EMPTY;

        TransformedInstance[] buffers = new TransformedInstance[6];
        IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(fs.getFluid());
        var atlas = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
        TextureAtlasSprite stillTexture = atlas.apply(clientFluid.getStillTexture(fs));

        for(int i = 0; i < 6; i++) {
            var t = surface.get(stillTexture);
            t.colorArgb(clientFluid.getTintColor(fs));
            buffers[i] = t;
        }

        return buffers;
    }

    public void _delete() {
        surface.delete();
    }
}
