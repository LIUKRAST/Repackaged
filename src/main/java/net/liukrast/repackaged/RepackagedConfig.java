package net.liukrast.repackaged;

import net.neoforged.neoforge.common.ModConfigSpec;

public class RepackagedConfig {
    private RepackagedConfig() {}

    public static class Client {
        private Client() {}
        private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

        static final ModConfigSpec SPEC = BUILDER.build();
    }

    public static class Server {
        private Server() {}
        private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
        public static final ModConfigSpec.BooleanValue VANILLA_CRAFTER_UNPACKING = BUILDER
                .comment("Makes so vanilla crafters respect order context from packaging requests")
                .define("vanillaCrafterUnpacking", true);

        public static final ModConfigSpec.BooleanValue BOX_ORDER_FIX = BUILDER
                .comment("Makes so boxes always unpack in the correct order, even without passing through a re-packager")
                .define("boxOrderFix", true);

        public static final ModConfigSpec.IntValue MAX_BATTERY_ENERGY = BUILDER
                .comment("Defines the max amount of energy a battery can contain")
                .comment("REQUIRES RESTART!")
                .defineInRange("maxBatteryEnergy", 500_000, 1, Integer.MAX_VALUE);

        static final ModConfigSpec SPEC = BUILDER.build();
    }
}
