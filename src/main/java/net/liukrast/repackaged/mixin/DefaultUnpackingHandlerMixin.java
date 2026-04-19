package net.liukrast.repackaged.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.impl.unpacking.DefaultUnpackingHandler;
import net.liukrast.repackaged.RepackagedConfig;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Mixin(DefaultUnpackingHandler.class)
public class DefaultUnpackingHandlerMixin {
    @ModifyExpressionValue(
            method = "unpack",
            at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;", ordinal = 0)
    )
    private Iterator<ItemStack> unpack(
            Iterator<ItemStack> original,
            @Local(argsOnly = true) PackageOrderWithCrafts orderContext,
            @Local(argsOnly = true) List<ItemStack> items,
            @Local(name = "targetInv") IItemHandler targetInv
    ) {
        if(!RepackagedConfig.Server.BOX_ORDER_FIX.getAsBoolean()) return original;
        if(orderContext == null) return original;
        if(orderContext.orderedStacks().isEmpty()) return original;
        List<ItemStack> copy = new ArrayList<>(items);
        for (BigItemStack elem : orderContext.orderedStacks().stacks()) {
            int remaining = elem.count;

            for (Iterator<ItemStack> it = copy.iterator(); it.hasNext() && remaining > 0; ) {
                ItemStack inCopy = it.next();

                if (!ItemStack.isSameItemSameComponents(inCopy, elem.stack)) continue;

                int taken = Math.min(inCopy.getCount(), remaining);
                inCopy.shrink(taken);
                remaining -= taken;

                if (inCopy.isEmpty()) it.remove();
            }

            if (remaining < elem.count) {
                ItemStack toInsert = elem.stack.copyWithCount(elem.count - remaining);
                ItemHandlerHelper.insertItemStacked(targetInv, toInsert, false);
            }
        }
        return Collections.emptyIterator();
    }
}
