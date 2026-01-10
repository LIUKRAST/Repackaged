package net.liukrast.repackaged.registry;

import com.google.common.collect.ImmutableList;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.liukrast.deployer.lib.logistics.packager.CustomPackageStyle;
import net.liukrast.repackaged.RepackagedConstants;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.stream.Stream;

import static com.simibubi.create.AllPartialModels.*;

public class RepackagedPackageStyles {
    private RepackagedPackageStyles() {}

    @ApiStatus.Internal
    @Unmodifiable
    public static final List<CustomPackageStyle> BOTTLE_STYLES = ImmutableList.of(
            new CustomPackageStyle(RepackagedConstants.id("bottle"), "copper", 8, 12, 19f, false),
            new CustomPackageStyle(RepackagedConstants.id("bottle"), "copper", 10, 14, 20f, false)
    );

    @ApiStatus.Internal
    @Unmodifiable
    public static final List<CustomPackageStyle> BATTERY_STYLES = ImmutableList.of(
            new CustomPackageStyle(RepackagedConstants.id("battery"), "brass", 10, 12, 18f, false)
    );

    static {
        Stream.concat(
                BOTTLE_STYLES.stream(),
                BATTERY_STYLES.stream()
        ).forEach(style -> {
            ResourceLocation key = style.getItemId();
            PartialModel model = PartialModel.of(RepackagedConstants.id("item/" + key.getPath()));
            PACKAGES.put(key, model);
            if (!style.rare())
                PACKAGES_TO_HIDE_AS.add(model);
            PACKAGE_RIGGING.put(key, PartialModel.of(style.getRiggingModel()));
        });
    }

    @ApiStatus.Internal
    public static void init() {}
}
