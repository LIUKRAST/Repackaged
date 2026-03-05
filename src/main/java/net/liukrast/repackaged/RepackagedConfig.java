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

        static final ModConfigSpec SPEC = BUILDER.build();
    }
}
