package net.liukrast.repackaged.registry;

import com.mojang.serialization.Codec;
import net.liukrast.deployer.lib.helper.MinecraftHelpers;
import net.liukrast.deployer.lib.logistics.GenericPackageOrderData;
import net.liukrast.deployer.lib.logistics.stockTicker.GenericOrderContained;
import net.liukrast.repackaged.RepackagedConstants;
import net.liukrast.repackaged.content.energy.EnergyStack;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RepackagedDataComponents {
    private RepackagedDataComponents() {}

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, RepackagedConstants.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SimpleFluidContent>> BOTTLE_CONTENTS = DATA_COMPONENTS.register("bottle_contents", () -> DataComponentType.<SimpleFluidContent>builder()
            .persistent(SimpleFluidContent.CODEC)
            .networkSynchronized(SimpleFluidContent.STREAM_CODEC)
            .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GenericPackageOrderData<FluidStack>>> BOTTLE_ORDER_DATA = DATA_COMPONENTS.register("bottle_order_data", () -> MinecraftHelpers.createOrderData(RepackagedStockInventoryTypes.FLUID::get).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GenericOrderContained<FluidStack>>> BOTTLE_ORDER_CONTEXT = DATA_COMPONENTS.register("bottle_order_context", () -> MinecraftHelpers.createContext(RepackagedStockInventoryTypes.FLUID::get).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> BATTERY_CONTENTS = DATA_COMPONENTS.register("battery_contents", () -> DataComponentType.<Integer>builder()
            .persistent(Codec.INT)
            .networkSynchronized(ByteBufCodecs.INT)
            .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GenericPackageOrderData<EnergyStack>>> BATTERY_ORDER_DATA = DATA_COMPONENTS.register("battery_order_data", () -> MinecraftHelpers.createOrderData(RepackagedStockInventoryTypes.ENERGY::get).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GenericOrderContained<EnergyStack>>> BATTERY_ORDER_CONTEXT = DATA_COMPONENTS.register("battery_order_context", () -> MinecraftHelpers.createContext(RepackagedStockInventoryTypes.ENERGY::get).build());

    public static void register(IEventBus eventBus) {
        DATA_COMPONENTS.register(eventBus);
    }
}
