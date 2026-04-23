package net.liukrast.repackaged;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.liukrast.deployer.lib.helper.ponder.SmartPonderRegistrationHelper;
import net.liukrast.repackaged.ponder.BatteryChargerPonder;
import net.liukrast.repackaged.ponder.PackageShelfPonder;
import net.liukrast.repackaged.ponder.PackagerConnectorPonder;
import net.liukrast.repackaged.ponder.StackedPackagersPonder;
import net.liukrast.repackaged.registry.RepackagedBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class RepackagedPonderPlugin implements PonderPlugin {
    @Override
    public String getModId() {
        return Repackaged.CONSTANTS.getModId();
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        var HELPER = SmartPonderRegistrationHelper.of(helper.withKeyFunction(BuiltInRegistries.ITEM::getKey));
        var stacked = new StackedPackagersPonder();
        HELPER.forComponents(RepackagedBlocks.BATTERY_CHARGER.asItem())
                .addPonder(new BatteryChargerPonder())
                .addPonder(stacked);
        HELPER.forComponents(RepackagedBlocks.FLUID_PACKAGER.asItem(), AllBlocks.PACKAGER.asItem())
                .addPonder(stacked);
        HELPER.forComponents(RepackagedBlocks.PACKAGE_SHELF.asItem())
                .addPonder(new PackageShelfPonder());
        HELPER.forComponents(RepackagedBlocks.PACKAGER_CONNECTOR.asItem())
                .addPonder(new PackagerConnectorPonder());

    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        var HELPER = helper.withKeyFunction(BuiltInRegistries.ITEM::getKey);

        HELPER.addToTag(AllCreatePonderTags.HIGH_LOGISTICS)
                .add(RepackagedBlocks.BATTERY_CHARGER.asItem())
                .add(RepackagedBlocks.PACKAGE_SHELF.asItem())
                .add(RepackagedBlocks.PACKAGER_CONNECTOR.asItem());
    }
}
