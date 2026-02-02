package net.liukrast.repackaged.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorVisual;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.transform.Translate;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import dev.engine_room.flywheel.lib.visual.SimpleTickableVisual;
import net.liukrast.repackaged.content.fluid.BottleVisual;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(ChainConveyorVisual.class)
public abstract class ChainConveyorVisualMixin extends SingleAxisRotatingVisual<ChainConveyorBlockEntity> implements SimpleDynamicVisual, SimpleTickableVisual {

    @Unique
    private BottleVisual deployer$test;

    public ChainConveyorVisualMixin(VisualizationContext context, ChainConveyorBlockEntity blockEntity, float partialTick, Model model) {
        super(context, blockEntity, partialTick, model);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(VisualizationContext context, ChainConveyorBlockEntity blockEntity, float partialTick, CallbackInfo ci) {
        deployer$test = new BottleVisual(context, blockEntity, partialTick);
    }

    @Inject(method = "beginFrame", at = @At("HEAD"))
    private void beginFrame(DynamicVisual.Context ctx, CallbackInfo ci) {
        deployer$test.beginFrame$Start();
    }

    @Inject(method = "beginFrame", at = @At("RETURN"))
    private void beginFrame$1(DynamicVisual.Context ctx, CallbackInfo ci) {
        deployer$test.beginFrame$End();
    }

    @Definition(id = "TransformedInstance", type = TransformedInstance.class)
    @Expression("new TransformedInstance[]{?,?}")
    @ModifyExpressionValue(method = "setupBoxVisual", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
    private TransformedInstance[] setupBoxVisual(
            TransformedInstance[] original,
            @Local(argsOnly = true) ChainConveyorPackage box,
            @Local(name = "physicsData") ChainConveyorPackage.ChainConveyorPackagePhysicsData physicsData,
            @Share("post_processor") LocalRef<BottleVisual.PostProcessor> postProcessor
            ) {
        postProcessor.set(new BottleVisual.PostProcessor());
        var addedBuffers = deployer$test.createBuffer(box, physicsData, postProcessor.get());
        TransformedInstance[] copy = Arrays.copyOf(original, original.length+addedBuffers.length);
        System.arraycopy(addedBuffers, 0, copy, original.length, addedBuffers.length);
        return copy;
    }

    @Inject(method = "setupBoxVisual", at = @At(value = "INVOKE", target = "Ldev/engine_room/flywheel/lib/instance/TransformedInstance;uncenter()Ldev/engine_room/flywheel/lib/transform/Translate;"))
    private void setupBoxVisual(ChainConveyorBlockEntity be,
                                ChainConveyorPackage box,
                                float partialTicks,
                                CallbackInfo ci,
                                @Local(name = "buf") TransformedInstance buf,
                                @Share("post_processor") LocalRef<BottleVisual.PostProcessor> postProcessor
    ) {

    }

    @WrapOperation(method = "setupBoxVisual", at = @At(value = "INVOKE", target = "Ldev/engine_room/flywheel/lib/instance/TransformedInstance;uncenter()Ldev/engine_room/flywheel/lib/transform/Translate;"))
    private Translate<TransformedInstance> setupBoxVisual(
            TransformedInstance instance,
            Operation<Translate<TransformedInstance>> original,
            @Share("post_processor") LocalRef<BottleVisual.PostProcessor> postProcessor
    ) {
        var post = postProcessor.get();
        if(post == null) return original.call(instance);
        if(post.consume(instance)) return instance;
        else return original.call(instance);
    }

    @Inject(method = "_delete", at = @At("RETURN"))
    private void _delete(CallbackInfo ci) {
        deployer$test._delete();
    }
}
