package net.liukrast.repackage.registry;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.liukrast.repackage.RepackagedConstants;

public class RepackagedPartialModels {
    private RepackagedPartialModels() {}

    public static final PartialModel
            FLUID_PANEL = block("fluid_gauge"),


            FLUID_PACKAGER_TRAY = block("fluid_packager/tray");


    private static PartialModel block(String path) {
        return PartialModel.of(RepackagedConstants.id("block/" + path));
    }

    public static void init() {}
}
