package net.liukrast.repackaged.content.fluid;

import com.simibubi.create.content.fluids.FluidMesh;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage;
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
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class BottleVisual {
    //TODO: Add width/height for box
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
            ChainConveyorPackage.ChainConveyorPackagePhysicsData physicsData,
            PostProcessor postProcessor
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
            int finalI = i;
            var t = surface.get(stillTexture);
            t.colorArgb(clientFluid.getTintColor(fs));
            postProcessor.subscribe(t, ins -> subscribePostProcessor(ins, finalI, fs.getAmount()));
            buffers[i] = t;
        }

        return buffers;
    }

    private void subscribePostProcessor(TransformedInstance buffer, int index, int amount) {
        float width = 0.5f;
        float height = 0.5f;
        var side = Direction.values()[index];

        width -= 1 / 256f;

        float fillFactor = (float) amount / 1000;

        buffer.translateY(-19f / 16);

        if (side.getAxis().isHorizontal()) {
            buffer.translateY(fillFactor * height / 2);
            buffer.scaleY(fillFactor / 2);
        } else {
            buffer.translateY(fillFactor * height / 2 + height / 2);
        }

        float horizontalOffset = width / 4 + width;
        horizontalOffset *= side.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1 : -1;
        if (side.getAxis() == Direction.Axis.X) {
            buffer.translateX(horizontalOffset);
        } else if (side.getAxis() == Direction.Axis.Z) {
            buffer.translateZ(horizontalOffset);
        }

        buffer.scale(.5f);

        buffer.rotateTo(Direction.UP, side);

        if (side.getAxis().isVertical())
            buffer.scale(width);

        if (side.getAxis() == Direction.Axis.X) {
            buffer.scaleZ(width);
        } else if (side.getAxis() == Direction.Axis.Z) {
            buffer.scaleX(width);
        }
    }

    public void _delete() {
        surface.delete();
    }

    public static class PostProcessor {
        private final Map<TransformedInstance, List<Consumer<TransformedInstance>>> map = new HashMap<>();

        public <T extends TransformedInstance> void subscribe(T instance, Consumer<T> consumer) {
            //noinspection unchecked
            map.computeIfAbsent(instance, k -> new ArrayList<>())
                    .add(t -> consumer.accept((T) t));
        }

        @ApiStatus.Internal
        public <T extends TransformedInstance> boolean consume(T instance) {
            var li = map.get(instance);
            if(li == null) return false;
            li.forEach(consumer -> consumer.accept(instance));
            return true;
        }
    }
}
