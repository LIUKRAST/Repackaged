package net.liukrast.repackaged.mixin;

import com.simibubi.create.content.logistics.box.PackageEntity;
import net.liukrast.deployer.lib.helper.client.DeployerGoggleInformation;
import net.liukrast.repackaged.Repackaged;
import net.liukrast.repackaged.RepackagedConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(PackageEntity.class)
public class PackageEntityMixin implements DeployerGoggleInformation {

    @Shadow
    public ItemStack box;

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if(!RepackagedConfig.Client.PACKAGE_GOGGLE_INFO.getAsBoolean()) return DeployerGoggleInformation.super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        var li = box.getTooltipLines(Item.TooltipContext.of(Minecraft.getInstance().level), Minecraft.getInstance().player, TooltipFlag.NORMAL);
        for(var c : li) {
            Repackaged.CONSTANTS.langBuilder().add(c).forGoggles(tooltip, 0);
        }
        return true;
    }
}
