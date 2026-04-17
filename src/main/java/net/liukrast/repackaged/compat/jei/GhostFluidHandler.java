package net.liukrast.repackaged.compat.jei;

import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.logistics.redstoneRequester.RedstoneRequesterScreen;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.neoforge.NeoForgeTypes;
import net.liukrast.deployer.lib.mixinExtensions.RRSExtension;
import net.liukrast.repackaged.content.fluid.FluidRequesterTabScreen;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedList;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class GhostFluidHandler implements IGhostIngredientHandler<RedstoneRequesterScreen> {
    @Override
    public <I> List<Target<I>> getTargetsTyped(RedstoneRequesterScreen gui, ITypedIngredient<I> ingredient, boolean doStart) {
        List<Target<I>> targets = new LinkedList<>();
        if(ingredient.getType() != NeoForgeTypes.FLUID_STACK && ingredient.getType() != VanillaTypes.ITEM_STACK) return targets;
        if(ingredient.getIngredient() instanceof ItemStack stack && FilterItemStack.of(stack).fluid(Minecraft.getInstance().level).isEmpty()) return targets;
        if(((RRSExtension)gui).deployer$getTab() instanceof FluidRequesterTabScreen fRTS) {
            for(int i = 0; i < 9; i++) {
                targets.add(new GhostTarget<>(gui.getGuiLeft(), gui.getGuiTop(), fRTS, i));
            }
        }
        return targets;
    }

    @Override
    public void onComplete() {

    }

    private static class GhostTarget<I> implements Target<I> {
        private final Rect2i area;
        private final FluidRequesterTabScreen gui;
        private final int index;

        public GhostTarget(int x, int y, FluidRequesterTabScreen gui, int index) {
            this.gui = gui;
            this.index = index;
            this.area = new Rect2i(x+index*20+27,y+28, 16, 16);
        }

        @Override
        public Rect2i getArea() {
            return area;
        }

        @Override
        public void accept(I ingredient) {
            if(ingredient instanceof FluidStack fs) {
                fs = fs.copyWithAmount(1000);
                gui.setSlot(index, fs);
            }
            if(ingredient instanceof ItemStack is) {
                gui.setSlot(index, FilterItemStack.of(is).fluid(Minecraft.getInstance().level).copyWithAmount(1000));
            }
        }
    }
}
