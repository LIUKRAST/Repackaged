package net.liukrast.repackaged.registry;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.liukrast.repackaged.Repackaged;

public class RepackagedPartialModels {
    private RepackagedPartialModels() {}

    public static final PartialModel
            FLUID_PANEL = block("fluid_gauge"),
            ENERGY_PANEL = block("energy_gauge"),
            FLUID_PACKAGER_TRAY = block("fluid_packager/tray"),
            TEMPLATE_BATTERY = block("battery_template");


    private static PartialModel block(String path) {
        return PartialModel.of(Repackaged.CONSTANTS.id("block/" + path));
    }

    public static void init() {}
}
