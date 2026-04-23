package net.liukrast.repackaged.datagen;

import net.liukrast.repackaged.Repackaged;
import net.liukrast.repackaged.content.logistics.PackagerConnectorBlock;
import net.liukrast.repackaged.registry.RepackagedBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class RepackagedBlockStateProvider extends BlockStateProvider {
    public RepackagedBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Repackaged.CONSTANTS.getModId(), exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        var multi = getMultipartBuilder(RepackagedBlocks.PACKAGER_CONNECTOR.get());
        for(Direction dir : Direction.values()) {
            multi.part()
                    .modelFile(models().getExistingFile(Repackaged.CONSTANTS.id("block/packager_connector/block")))
                    .rotationX(dir == Direction.DOWN ? 180 : dir.getAxis().isHorizontal() ? 90 : 0)
                    .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360)
                    .addModel()
                    .condition(PackagerConnectorBlock.FACING, dir)
                    .end();

            for(Direction connections : Direction.values()) {
                if(connections.getAxis().isVertical()) continue;
                int rotationX = dir == Direction.DOWN ? 180 : dir.getAxis().isHorizontal() ? 90 : 0;
                int rotationY = dir.getAxis().isVertical() ? 0 : ((int) dir.toYRot()) + 180;
                boolean barrel = connections.getAxis() == Direction.Axis.X;
                if(dir.getAxis().isHorizontal()) {
                    rotationX+= switch (connections) {
                        case SOUTH, EAST -> 180;
                        default -> 0;
                    };
                    rotationY+= switch (connections) {
                        case SOUTH, EAST -> 180;
                        default -> 0;
                    };
                } else if(connections.getAxisDirection() == Direction.AxisDirection.NEGATIVE) rotationY += 180;
                for(String key : new String[]{"in", "out"}) {
                    var t = multi.part()
                            .modelFile(models().getExistingFile(Repackaged.CONSTANTS.id("block/packager_connector/" + key + (barrel ? "_barrel" : ""))))
                            .rotationX(rotationX % 360)
                            .rotationY(rotationY % 360)
                            .addModel()
                            .condition(PackagerConnectorBlock.FACING, dir);
                    if(key.equals("out"))
                        t.condition(PackagerConnectorBlock.POINTING, connections).end();
                    else t.condition(PackagerConnectorBlock.get(connections), true).end();
                }
            }

        }

    }
}
