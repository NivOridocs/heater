package niv.heater.registry;

import java.util.function.Function;

import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import niv.heater.Heater;
import niv.heater.block.HeatPipeBlock;
import niv.heater.block.HeaterBlock;
import niv.heater.block.ThermostatBlock;
import niv.heater.block.WeatheringHeatPipeBlock;
import niv.heater.block.WeatheringHeaterBlock;
import niv.heater.block.WeatheringThermostatBlock;
import niv.heater.util.WeatherStateExtra;

public class RegisterBlocks {

    private RegisterBlocks() {
    }

    public static final void register() {

        registerAll("heater",
                HeaterBlock.BLOCKS.get()::get, HeaterBlock.ITEMS.get()::get,
                WeatheringHeaterBlock.BLOCKS.get()::get, WeatheringHeaterBlock.ITEMS.get()::get);

        registerAll("heat_pipe",
                HeatPipeBlock.BLOCKS.get()::get, HeatPipeBlock.ITEMS.get()::get,
                WeatheringHeatPipeBlock.BLOCKS.get()::get, WeatheringHeatPipeBlock.ITEMS.get()::get);

        registerAll("thermostat",
                ThermostatBlock.BLOCKS.get()::get, ThermostatBlock.ITEMS.get()::get,
                WeatheringThermostatBlock.BLOCKS.get()::get, WeatheringThermostatBlock.ITEMS.get()::get);
    }

    private static final void registerAll(String name,
            Function<WeatherState, Block> blocks, Function<WeatherState, Item> items,
            Function<WeatherState, Block> weatheringBlocks, Function<WeatherState, Item> weatheringItems) {
        var id = new ResourceLocation(Heater.MOD_ID, "");
        for (var state : WeatherState.values()) {
            id = id.withPath(name).withPrefix(WeatherStateExtra.toPath(state));
            var unwaxed = weatheringBlocks.apply(state);
            Registry.register(BuiltInRegistries.BLOCK, id, unwaxed);
            Registry.register(BuiltInRegistries.ITEM, id, weatheringItems.apply(state));
            id = id.withPrefix("waxed_");
            var waxed = blocks.apply(state);
            Registry.register(BuiltInRegistries.BLOCK, id, waxed);
            Registry.register(BuiltInRegistries.ITEM, id, items.apply(state));
            OxidizableBlocksRegistry.registerWaxableBlockPair(unwaxed, waxed);
            WeatherStateExtra.getNext(state).map(weatheringBlocks)
                    .ifPresent(next -> OxidizableBlocksRegistry.registerOxidizableBlockPair(unwaxed, next));
        }
    }

}
