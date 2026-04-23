package net.liukrast.repackaged.registry;

import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.liukrast.repackaged.Repackaged;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RepackagedArmInteractionPointTypes {
    private static final DeferredRegister<ArmInteractionPointType> REGISTER = DeferredRegister.create(CreateBuiltInRegistries.ARM_INTERACTION_POINT_TYPE, Repackaged.CONSTANTS.getModId());

    static {
        REGISTER.register("custom_packagers", () -> new AllArmInteractionPointTypes.PackagerType() {
            @Override
            public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
                return state.is(RepackagedBlocks.FLUID_PACKAGER) || state.is(RepackagedBlocks.BATTERY_CHARGER);
            }
        });
    }

    public static void init(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
