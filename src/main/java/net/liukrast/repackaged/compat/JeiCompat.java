package net.liukrast.repackaged.compat;

import com.simibubi.create.content.logistics.redstoneRequester.RedstoneRequesterScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.liukrast.repackaged.Repackaged;
import net.liukrast.repackaged.compat.jei.GhostFluidHandler;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.system.NonnullDefault;

@SuppressWarnings("unused")
@NonnullDefault
@JeiPlugin
public class JeiCompat implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return Repackaged.CONSTANTS.id("jei_plugin");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGhostIngredientHandler(RedstoneRequesterScreen.class, new GhostFluidHandler());
    }
}
