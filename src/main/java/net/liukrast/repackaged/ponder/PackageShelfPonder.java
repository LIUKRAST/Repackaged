package net.liukrast.repackaged.ponder;

import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.box.PackageStyles;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import com.simibubi.create.infrastructure.ponder.scenes.highLogistics.PonderHilo;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.foundation.instruction.RotateSceneInstruction;
import net.liukrast.deployer.lib.helper.ponder.Ponder;
import net.liukrast.repackaged.content.logistics.PackageShelfBlock;
import net.liukrast.repackaged.content.logistics.PackageShelfBlockEntity;
import net.liukrast.repackaged.registry.RepackagedBlocks;
import net.liukrast.repackaged.registry.RepackagedStockInventoryTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

import static net.liukrast.deployer.lib.helper.PonderSceneHelpers.displayText;
import static net.liukrast.deployer.lib.helper.PonderSceneHelpers.simpleInit;

public class PackageShelfPonder implements Ponder {
    @Override
    public String getSchematicPath() {
        return "high_logistics/package_shelf";
    }

    @Override
    public void create(SceneBuilder builder, SceneBuildingUtil util) {
        var scene = simpleInit(builder, util, "package_shelf");

        BlockPos shelf = util.grid().at(5,2,2);
        BlockPos lever = util.grid().at(5,2,1);

        var order = scene.world().showIndependentSection(util.select().fromTo(3,1,3,2,1,3), Direction.DOWN);
        scene.idle(20);

        displayText(scene, util.grid().at(2,1,3), 80, false);
        scene.world().showIndependentSection(util.select().position(5,4,2), Direction.UP);

        ItemStack bottle = RepackagedStockInventoryTypes.FLUID.get().packageHandler().getRandomBox();
        ItemStack box = PackageStyles.getDefaultBox();
        //bottle.set(RepackagedDataComponents.BOTTLE_CONTENTS, SimpleFluidContent.copyOf(stack));
        scene.overlay()
                .showControls(util.grid().at(2,1,3).getBottomCenter(), Pointing.DOWN, 20)
                .withItem(bottle);
        scene.idle(30);

        scene.overlay()
                .showControls(util.grid().at(2,1,3).getBottomCenter(), Pointing.DOWN, 20)
                .withItem(box);
        scene.idle(40);

        displayText(scene, util.grid().at(2,1,3), 80, false);
        scene.idle(20);
        scene.world().hideIndependentSection(order, Direction.UP);
        scene.idle(10);
        scene.world().showIndependentSection(util.select().fromTo(6,1,2,0,3,2), Direction.DOWN);
        scene.world().showIndependentSection(util.select().fromTo(6,1,3,5,2,3), Direction.DOWN);
        scene.world().showIndependentSection(util.select().fromTo(0,1,4,5,2,4), Direction.DOWN);
        scene.world().showIndependentSection(util.select().position(0,1,3), Direction.DOWN);

        scene.addKeyframe();
        scene.world()
                .setKineticSpeed(util.select()
                        .everywhere(), 16f);

        scene.world()
                .setKineticSpeed(util.select().fromTo(0,1,4,5,2,4), -16f);
        scene.idle(10);
        scene.world().createItemOnBelt(util.grid().at(1,1,4), Direction.WEST, box);
        scene.idle(100);
        scene.world()
                .removeItemsFromBelt(util.grid()
                        .at(4, 1, 4));
        scene.world()
                .flapFunnel(util.grid()
                        .at(4,2,4), false);
        scene.idle(20);

        displayText(scene, shelf, 60, false);

        scene.world().showIndependentSection(util.select().position(5,2,1), Direction.SOUTH);
        scene.idle(10);
        scene.world().toggleRedstonePower(util.select().fromTo(lever, shelf));

        scene.effects()
                .indicateRedstone(lever);

        scene.idle(10);
        scene.world()
                .setKineticSpeed(util.select().fromTo(0,1,4,5,2,4), -16f);
        scene.idle(10);
        scene.world().createItemOnBelt(util.grid().at(1,1,4), Direction.WEST, bottle);
        scene.idle(100);
        scene.world()
                .removeItemsFromBelt(util.grid()
                        .at(4, 1, 4));
        scene.world()
                .flapFunnel(util.grid()
                        .at(4,2,4), false);
        scene.addInstruction(new RotateSceneInstruction(30, 0, true));
        scene.idle(10);
        setInQueue(scene, shelf, box);
        PonderHilo.packagerCreate(scene, shelf, bottle);
        displayText(scene, shelf.above(), 100, true);

        scene.addInstruction(new RotateSceneInstruction(-30, 0, true));
        setInQueue(scene, shelf);
        PonderHilo.packagerCreate(scene, shelf, box);
        scene.world().createItemOnBelt(util.grid().at(4,1,2), Direction.EAST, bottle);
        scene.idle(30);
        PonderHilo.packagerClear(scene, shelf);
        scene.world().createItemOnBelt(util.grid().at(4,1,2), Direction.EAST, box);
        displayText(scene, util.grid().at(4,2,2), 60, false);
        scene.idle(40);
        displayText(scene, shelf.above(), 80, true);

        scene.world().setBlock(shelf.above(), RepackagedBlocks.PACKAGE_SHELF.get().defaultBlockState().setValue(PackageShelfBlock.TYPE, PackageShelfBlock.Type.MIDDLE), true);
        scene.world().setBlock(shelf.above(2), RepackagedBlocks.PACKAGE_SHELF.get().defaultBlockState().setValue(PackageShelfBlock.TYPE, PackageShelfBlock.Type.TOP), true);
        displayText(scene, shelf.above(), 60, false);
    }

    public static void setInQueue(CreateSceneBuilder scene, BlockPos shelfPos, ItemStack... stacks) {
        scene.world().modifyBlockEntity(shelfPos, PackageShelfBlockEntity.class, be -> {
            be.queuedExitingPackages.clear();
            Arrays.stream(stacks).map(BigItemStack::new).forEach(stack -> be.queuedExitingPackages.add(stack));
        });
    }
}
