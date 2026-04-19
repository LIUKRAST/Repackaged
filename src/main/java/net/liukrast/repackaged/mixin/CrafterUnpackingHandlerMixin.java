package net.liukrast.repackaged.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.impl.unpacking.CrafterUnpackingHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CrafterUnpackingHandler.class)
public class CrafterUnpackingHandlerMixin {
    @ModifyArg(
            method = "unpack",
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/api/packager/unpacking/UnpackingHandler;unpack(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Ljava/util/List;Lcom/simibubi/create/content/logistics/stockTicker/PackageOrderWithCrafts;Z)Z"),
            index = 5
    )
    private @Nullable PackageOrderWithCrafts unpack(@Nullable PackageOrderWithCrafts original, @Local(argsOnly = true) PackageOrderWithCrafts orderContext) {
        return orderContext;
    }
}
