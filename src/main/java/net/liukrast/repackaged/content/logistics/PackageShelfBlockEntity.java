package net.liukrast.repackaged.content.logistics;

import com.simibubi.create.content.logistics.packager.repackager.RepackagerBlockEntity;
import net.liukrast.repackaged.registry.RepackagedBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class PackageShelfBlockEntity extends RepackagerBlockEntity {

    public PackageShelfBlockEntity(BlockPos pos, BlockState state) {
        super(RepackagedBlockEntityTypes.PACKAGE_SHELF.get(), pos, state);
        repackageHelper = new PackageShelfHelper();
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        //TODO: Our inventory is special
    }
}
