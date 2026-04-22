package net.liukrast.repackaged.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.logistics.packager.PackagerBlock;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import net.liukrast.repackaged.content.energy.BatteryChargerBlockEntity;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PackagerBlock.class)
public class PackagerBlockMixin {

    @ModifyExpressionValue(
            method = "lambda$useItemOn$0",
            at = @At(
                    value = "FIELD",
                    target = "Lcom/simibubi/create/content/logistics/packager/PackagerBlockEntity;heldBox:Lnet/minecraft/world/item/ItemStack;",
                    opcode = Opcodes.GETFIELD
            )
    )
    private static ItemStack lambda$useItemOn$0(ItemStack original, @Local(argsOnly = true) PackagerBlockEntity parent) {
        if(!(parent instanceof BatteryChargerBlockEntity bc)) return original;
        if(!bc.isUnwrappingEnergy()) return original;
        return parent.previouslyUnwrapped;
    }

    @WrapOperation(
            method = "lambda$useItemOn$0",
            at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/logistics/packager/PackagerBlockEntity;heldBox:Lnet/minecraft/world/item/ItemStack;", opcode = Opcodes.PUTFIELD)
    )
    private static void lambda$useItemOn$0(PackagerBlockEntity instance, ItemStack value, Operation<Void> original) {
        if(!(instance instanceof BatteryChargerBlockEntity bc)) original.call(instance, value);
        else if(!bc.isUnwrappingEnergy()) original.call(instance, value);
        else instance.previouslyUnwrapped = value;
    }
}
