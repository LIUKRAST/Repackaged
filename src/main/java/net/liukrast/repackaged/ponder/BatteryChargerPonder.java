package net.liukrast.repackaged.ponder;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import com.simibubi.create.infrastructure.ponder.scenes.highLogistics.PonderHilo;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import static net.liukrast.deployer.lib.helper.PonderSceneHelpers.*;

import net.createmod.ponder.api.scene.Selection;
import net.liukrast.deployer.lib.helper.ponder.AnimatePartialInstruction;
import net.liukrast.deployer.lib.helper.ponder.PartialElement;
import net.liukrast.deployer.lib.helper.ponder.Ponder;
import net.liukrast.repackaged.registry.RepackagedItems;
import net.liukrast.repackaged.registry.RepackagedPartialModels;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class BatteryChargerPonder implements Ponder {

    @Override
    public String getSchematicPath() {
        return "high_logistics/battery_charger";
    }

    @Override
    public void create(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = simpleInit(builder, util, "battery_charger");

        BlockPos chargerPos = util.grid()
                .at(5,2,2);

        ElementLink<PartialElement> battery1 = createPartialModel(scene, RepackagedPartialModels.TEMPLATE_BATTERY, new Vec3(3,1,3));

        ElementLink<WorldSectionElement> charger = scene.world()
                .showIndependentSection(util.select().position(chargerPos), Direction.SOUTH);
        scene.world().moveSection(charger, util.vector().of(-2, -1, 0), 0);

        scene.idle(20);
        displayText(builder, new BlockPos(3,1,3), 40, false);
        scene.idle(10);
        displayText(builder, new BlockPos(3,1,2), 80, false);

        //createPartialModel(scene, RepackagedPartialModels.TEMPLATE_BATTERY, new Vec3(2, 1, 2), 15, Direction.UP);
        BlockPos lever = util.grid().at(5,3,2);
        ElementLink<WorldSectionElement> leverL = scene.world()
                .showIndependentSection(util.select().position(lever), Direction.DOWN);
        scene.world().moveSection(leverL, util.vector().of(-2, -1, 0), 0);
        scene.idle(30);

        scene.world().toggleRedstonePower(util.select().fromTo(lever, chargerPos));

        scene.effects()
                .indicateRedstone(lever.west(2)
                        .below());

        scene.idle(10);
        ItemStack box = RepackagedItems.STANDARD_BATTERIES.getFirst().toStack();
        PonderHilo.packagerCreate(scene, chargerPos, box);
        scene.idle(30);

        displayText(builder, new BlockPos(3,1,3), 80, true);
        scene.idle(10);
        displayText(builder, new BlockPos(3,1,2), 80, false);

        for(int i = 0; i < 4; i++) {

            scene.effects().emitParticles(
                    new BlockPos(3, 1, 2).getCenter(),
                    scene.effects().particleEmitterWithinBlockSpace(ParticleTypes.WAX_OFF, Vec3.ZERO),
                    25, 1
            );
            scene.idle(20);
        }

        scene.idle(20);


        scene.world().moveSection(leverL, util.vector().of(2,1,0), 10);
        scene.world().moveSection(charger, util.vector().of(2,1,0), 10);

        scene.addInstruction(AnimatePartialInstruction.move(battery1, util.vector().of(2,1,0), 10));
        scene.world().showSection(util.select().fromTo(0, 1, 0, 6, 1, 6), Direction.UP);


        Selection largeCog = util.select()
                .position(7, 0, 3);
        scene.world()
                .showSection(largeCog, Direction.UP);
        scene.world().showIndependentSection(util.select().fromTo(1,2,2,1,2,3), Direction.UP);

        scene.world().showIndependentSection(util.select().position(4,2,2), Direction.EAST);
        createPartialModel(scene, RepackagedPartialModels.TEMPLATE_BATTERY, new Vec3(1,2,4));
        scene.idle(10);
        scene.world()
                .setKineticSpeed(util.select()
                        .everywhere(), 16f);
        scene.idle(30);
        displayText(builder, new BlockPos(4, 2, 2), 80, true);
        displayText(builder, new BlockPos(4, 2, 2), 80, false);
        scene.effects().emitParticles(
                new BlockPos(chargerPos).getCenter(),
                scene.effects().particleEmitterWithinBlockSpace(ParticleTypes.WAX_OFF, Vec3.ZERO),
                25, 1
        );
        scene.idle(20);

        scene.world().createItemOnBelt(util.grid().at(4,1,2), Direction.EAST, box);
        PonderHilo.packagerClear(scene, chargerPos);
        scene.idle(20);

        scene.world()
                .toggleRedstonePower(util.select()
                        .fromTo(5, 2, 2, 5, 3, 2));
        scene.idle(70);
        scene.world()
                .removeItemsFromBelt(util.grid()
                        .at(1, 1, 2));
        scene.world()
                .flapFunnel(util.grid()
                        .at(1, 2, 2), false);
        PonderHilo.packagerUnpack(scene, util.grid().at(1,2,3), box);
        scene.idle(10);
        PonderHilo.packagerCreate(scene, util.grid().at(1,2,3), box);

        for(int i = 0; i < 4; i++) {

            scene.effects().emitParticles(
                    util.grid().at(1,2,3).getCenter(),
                    scene.effects().particleEmitterWithinBlockSpace(ParticleTypes.WAX_ON, Vec3.ZERO),
                    25, 1
            );
            scene.idle(20);
        }
        PonderHilo.packagerUnpack(scene, util.grid().at(1,2,3), box);
        scene.idle(20);
        displayText(builder, chargerPos, 80, false);

    }
}
