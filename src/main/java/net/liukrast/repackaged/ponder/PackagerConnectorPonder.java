package net.liukrast.repackaged.ponder;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.infrastructure.ponder.scenes.highLogistics.PonderHilo;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.liukrast.deployer.lib.helper.ponder.AnimatePartialInstruction;
import net.liukrast.deployer.lib.helper.ponder.Ponder;
import net.liukrast.repackaged.content.logistics.PackagerConnectorBlock;
import net.liukrast.repackaged.registry.RepackagedBlocks;
import net.liukrast.repackaged.registry.RepackagedPartialModels;
import net.liukrast.repackaged.registry.RepackagedStockInventoryTypes;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

import static net.liukrast.deployer.lib.helper.PonderSceneHelpers.*;

public class PackagerConnectorPonder implements Ponder {
    @Override
    public String getSchematicPath() {
        return "high_logistics/packager_connector";
    }

    @Override
    public void create(SceneBuilder builder, SceneBuildingUtil util) {
        var scene = simpleInit(builder, util, "packager_connector");
        scene.setSceneOffsetY(-1.0F);

        scene.world().showIndependentSection(util.select().fromTo(4,1,3,2,1,4), Direction.DOWN);
        var battery1 = createPartialModel(scene, RepackagedPartialModels.TEMPLATE_BATTERY, new Vec3(2,1,4));

        scene.idle(20);

        var battery = RepackagedStockInventoryTypes.ENERGY.get().packageHandler().getRandomBox();
        PonderHilo.packagerUnpack(scene, util.grid().at(4,1,3), battery);
        scene.idle(10);
        PonderHilo.packagerCreate(scene, util.grid().at(4,1,3), battery);

        scene.idle(30);

        displayText(scene, util.grid().at(4,1,3), 100, false);

        scene.world().showIndependentSection(util.select().fromTo(4,1,2, 2,1,2), Direction.SOUTH);
        scene.idle(10);
        displayText(scene, util.grid().at(4,1,2), 50, true);

        scene.overlay()
                .showControls(new Vec3(4.5,1.5,3), Pointing.DOWN, 20)
                .withItem(AllItems.WRENCH.asStack());
        scene.world().setBlock(util.grid().at(4,1,2), RepackagedBlocks.PACKAGER_CONNECTOR.get().defaultBlockState().setValue(PackagerConnectorBlock.POINTING, Direction.WEST), true);
        scene.world().setBlock(util.grid().at(3,1,2), RepackagedBlocks.PACKAGER_CONNECTOR.get().defaultBlockState().setValue(PackagerConnectorBlock.POINTED_W, true), true);
        scene.idle(30);
        scene.overlay()
                .showControls(new Vec3(3.5,1.5,3), Pointing.DOWN, 20)
                .withItem(AllItems.WRENCH.asStack());
        scene.world().setBlock(util.grid().at(3,1,2), RepackagedBlocks.PACKAGER_CONNECTOR.get().defaultBlockState().setValue(PackagerConnectorBlock.POINTED_W, true).setValue(PackagerConnectorBlock.POINTING, Direction.WEST), true);
        scene.world().setBlock(util.grid().at(2,1,2), RepackagedBlocks.PACKAGER_CONNECTOR.get().defaultBlockState().setValue(PackagerConnectorBlock.POINTED_W, true), true);
        scene.idle(30);

        displayText(scene, util.grid().at(4,1,2), 60, true);
        PonderHilo.packagerClear(scene, util.grid().at(4,1,3));
        PonderHilo.packagerUnpack(scene, util.grid().at(3,1,3), battery);
        scene.idle(10);
        PonderHilo.packagerCreate(scene, util.grid().at(3,1,3), battery);
        scene.idle(30);
        PonderHilo.packagerClear(scene, util.grid().at(3,1,3));
        PonderHilo.packagerUnpack(scene, util.grid().at(2,1,3), battery);

        scene.idle(30);

        scene.addKeyframe();

        scene.addInstruction(AnimatePartialInstruction.move(battery1, new Vec3(-1, 0, 0), 10));
        scene.world().setBlock(util.grid().at(2,1,3), AllBlocks.ANDESITE_CASING.getDefaultState(), true);
        scene.idle(10);
        scene.world().showIndependentSection(util.select().fromTo(1,1,2,1,1,3), Direction.SOUTH);

        scene.idle(30);
        scene.overlay()
                .showControls(new Vec3(2.5,1.5,3), Pointing.DOWN, 20)
                .withItem(AllItems.WRENCH.asStack());
        scene.world().setBlock(util.grid().at(2,1,2), RepackagedBlocks.PACKAGER_CONNECTOR.get().defaultBlockState().setValue(PackagerConnectorBlock.POINTED_W, true).setValue(PackagerConnectorBlock.POINTING, Direction.WEST), true);
        scene.world().setBlock(util.grid().at(1,1,2), RepackagedBlocks.PACKAGER_CONNECTOR.get().defaultBlockState().setValue(PackagerConnectorBlock.POINTED_W, true), true);
        scene.idle(30);

        displayText(scene, util.grid().at(2,1,2), 60, false);
    }
}
