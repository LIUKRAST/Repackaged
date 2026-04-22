package net.liukrast.repackaged.ponder;

import com.simibubi.create.AllFluids;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import com.simibubi.create.infrastructure.ponder.scenes.highLogistics.PonderHilo;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.liukrast.deployer.lib.helper.ponder.Ponder;
import net.liukrast.repackaged.registry.RepackagedDataComponents;
import net.liukrast.repackaged.registry.RepackagedStockInventoryTypes;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import static net.liukrast.deployer.lib.helper.PonderSceneHelpers.*;

public class StackedPackagersPonder implements Ponder {

    @Override
    public String getSchematicPath() {
        return "high_logistics/stacked_packagers";
    }

    @Override
    public void create(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = simpleInit(builder, util, "stacked_packagers");

        var stack = new FluidStack(AllFluids.CHOCOLATE.get()
                .getSource(), 1000);
        scene.world().showIndependentSection(util.select().fromTo(3,1,2,3,4,3), Direction.DOWN);

        scene.idle(20);

        ItemStack bottle = RepackagedStockInventoryTypes.FLUID.get().packageHandler().getRandomBox();
        bottle.set(RepackagedDataComponents.BOTTLE_CONTENTS, SimpleFluidContent.copyOf(stack));
        scene.overlay()
                .showControls(util.vector()
                        .of(3.5, 4, 2.5), Pointing.DOWN, 40)
                .withItem(bottle);

        scene.idle(40);
        PonderHilo.packagerUnpack(scene, util.grid().at(3,3,2), bottle);
        scene.idle(10);
        scene.world().createItemOnBeltLike(util.grid().at(3, 2, 2), Direction.EAST, new ItemStack(Items.STONE, 64));
        PonderHilo.packagerCreate(scene, util.grid().at(3,3,2), bottle);
        scene.idle(30);
        displayText(scene, util.grid().at(3,3,2), 60, false);
        displayText(scene, util.grid().at(3,3,2), 60, false);
        displayText(scene, util.grid().at(3,2,2), 60, true);
        PonderHilo.packagerClear(scene, util.grid().at(3,3,2));
        scene.idle(10);
        PonderHilo.packagerUnpack(scene, util.grid().at(3,1,2), bottle);

        scene.world().modifyBlockEntity(util.grid().at(3,1,3), FluidTankBlockEntity.class, be -> be.getTankInventory()
                .fill(stack, IFluidHandler.FluidAction.EXECUTE));



    }
}
