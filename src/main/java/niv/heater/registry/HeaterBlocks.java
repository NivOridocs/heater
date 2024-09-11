package niv.heater.registry;

import static net.minecraft.world.level.block.Blocks.COPPER_BLOCK;
import static net.minecraft.world.level.block.Blocks.FURNACE;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.EXPOSED;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.OXIDIZED;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.UNAFFECTED;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.WEATHERED;
import static net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy;
import static niv.heater.Heater.MOD_ID;

import com.google.common.collect.ImmutableMap;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import niv.heater.api.Worded;
import niv.heater.block.HeatPipeBlock;
import niv.heater.block.HeaterBlock;
import niv.heater.block.ThermostatBlock;
import niv.heater.block.WeatheringHeatPipeBlock;
import niv.heater.block.WeatheringHeaterBlock;
import niv.heater.block.WeatheringThermostatBlock;

public class HeaterBlocks {
    private HeaterBlocks() {
    }

    public static final HeaterBlock WAXED_HEATER;
    public static final HeaterBlock WAXED_EXPOSED_HEATER;
    public static final HeaterBlock WAXED_WEATHERED_HEATER;
    public static final HeaterBlock WAXED_OXIDIZED_HEATER;

    public static final ImmutableMap<WeatherState, HeaterBlock> WAXED_HEATERS;

    public static final WeatheringHeaterBlock HEATER;
    public static final WeatheringHeaterBlock EXPOSED_HEATER;
    public static final WeatheringHeaterBlock WEATHERED_HEATER;
    public static final WeatheringHeaterBlock OXIDIZED_HEATER;

    public static final ImmutableMap<WeatherState, WeatheringHeaterBlock> HEATERS;

    public static final HeatPipeBlock WAXED_HEAT_PIPE;
    public static final HeatPipeBlock WAXED_EXPOSED_HEAT_PIPE;
    public static final HeatPipeBlock WAXED_WEATHERED_HEAT_PIPE;
    public static final HeatPipeBlock WAXED_OXIDIZED_HEAT_PIPE;

    public static final ImmutableMap<WeatherState, HeatPipeBlock> WAXED_HEAT_PIPES;

    public static final WeatheringHeatPipeBlock HEAT_PIPE;
    public static final WeatheringHeatPipeBlock EXPOSED_HEAT_PIPE;
    public static final WeatheringHeatPipeBlock WEATHERED_HEAT_PIPE;
    public static final WeatheringHeatPipeBlock OXIDIZED_HEAT_PIPE;

    public static final ImmutableMap<WeatherState, WeatheringHeatPipeBlock> HEAT_PIPES;

    public static final ThermostatBlock WAXED_THERMOSTAT;
    public static final ThermostatBlock WAXED_EXPOSED_THERMOSTAT;
    public static final ThermostatBlock WAXED_WEATHERED_THERMOSTAT;
    public static final ThermostatBlock WAXED_OXIDIZED_THERMOSTAT;

    public static final ImmutableMap<WeatherState, ThermostatBlock> WAXED_THERMOSTATS;

    public static final WeatheringThermostatBlock THERMOSTAT;
    public static final WeatheringThermostatBlock EXPOSED_THERMOSTAT;
    public static final WeatheringThermostatBlock WEATHERED_THERMOSTAT;
    public static final WeatheringThermostatBlock OXIDIZED_THERMOSTAT;

    public static final ImmutableMap<WeatherState, WeatheringThermostatBlock> THERMOSTATS;

    static {
        WAXED_HEATER = register(new HeaterBlock(UNAFFECTED, ofFullCopy(FURNACE)));
        WAXED_EXPOSED_HEATER = register(new HeaterBlock(UNAFFECTED, ofFullCopy(FURNACE)));
        WAXED_WEATHERED_HEATER = register(new HeaterBlock(UNAFFECTED, ofFullCopy(FURNACE)));
        WAXED_OXIDIZED_HEATER = register(new HeaterBlock(UNAFFECTED, ofFullCopy(FURNACE)));

        WAXED_HEATERS = ImmutableMap.<WeatherState, HeaterBlock>builder()
                .put(UNAFFECTED, WAXED_HEATER)
                .put(EXPOSED, WAXED_EXPOSED_HEATER)
                .put(WEATHERED, WAXED_WEATHERED_HEATER)
                .put(OXIDIZED, WAXED_OXIDIZED_HEATER)
                .build();

        HEATER = register(new WeatheringHeaterBlock(UNAFFECTED, ofFullCopy(FURNACE)));
        EXPOSED_HEATER = register(new WeatheringHeaterBlock(UNAFFECTED, ofFullCopy(FURNACE)));
        WEATHERED_HEATER = register(new WeatheringHeaterBlock(UNAFFECTED, ofFullCopy(FURNACE)));
        OXIDIZED_HEATER = register(new WeatheringHeaterBlock(UNAFFECTED, ofFullCopy(FURNACE)));

        HEATERS = ImmutableMap.<WeatherState, WeatheringHeaterBlock>builder()
                .put(UNAFFECTED, HEATER)
                .put(EXPOSED, EXPOSED_HEATER)
                .put(WEATHERED, WEATHERED_HEATER)
                .put(OXIDIZED, OXIDIZED_HEATER)
                .build();

        WAXED_HEAT_PIPE = register(new HeatPipeBlock(UNAFFECTED, ofFullCopy(COPPER_BLOCK)));
        WAXED_EXPOSED_HEAT_PIPE = register(new HeatPipeBlock(UNAFFECTED, ofFullCopy(COPPER_BLOCK)));
        WAXED_WEATHERED_HEAT_PIPE = register(new HeatPipeBlock(UNAFFECTED, ofFullCopy(COPPER_BLOCK)));
        WAXED_OXIDIZED_HEAT_PIPE = register(new HeatPipeBlock(UNAFFECTED, ofFullCopy(COPPER_BLOCK)));

        WAXED_HEAT_PIPES = ImmutableMap.<WeatherState, HeatPipeBlock>builder()
                .put(UNAFFECTED, WAXED_HEAT_PIPE)
                .put(EXPOSED, WAXED_EXPOSED_HEAT_PIPE)
                .put(WEATHERED, WAXED_WEATHERED_HEAT_PIPE)
                .put(OXIDIZED, WAXED_OXIDIZED_HEAT_PIPE)
                .build();

        HEAT_PIPE = register(new WeatheringHeatPipeBlock(UNAFFECTED, ofFullCopy(COPPER_BLOCK)));
        EXPOSED_HEAT_PIPE = register(new WeatheringHeatPipeBlock(UNAFFECTED, ofFullCopy(COPPER_BLOCK)));
        WEATHERED_HEAT_PIPE = register(new WeatheringHeatPipeBlock(UNAFFECTED, ofFullCopy(COPPER_BLOCK)));
        OXIDIZED_HEAT_PIPE = register(new WeatheringHeatPipeBlock(UNAFFECTED, ofFullCopy(COPPER_BLOCK)));

        HEAT_PIPES = ImmutableMap.<WeatherState, WeatheringHeatPipeBlock>builder()
                .put(UNAFFECTED, HEAT_PIPE)
                .put(EXPOSED, EXPOSED_HEAT_PIPE)
                .put(WEATHERED, WEATHERED_HEAT_PIPE)
                .put(OXIDIZED, OXIDIZED_HEAT_PIPE)
                .build();

        WAXED_THERMOSTAT = register(new ThermostatBlock(UNAFFECTED, ofFullCopy(COPPER_BLOCK)));
        WAXED_EXPOSED_THERMOSTAT = register(new ThermostatBlock(UNAFFECTED, ofFullCopy(COPPER_BLOCK)));
        WAXED_WEATHERED_THERMOSTAT = register(new ThermostatBlock(UNAFFECTED, ofFullCopy(COPPER_BLOCK)));
        WAXED_OXIDIZED_THERMOSTAT = register(new ThermostatBlock(UNAFFECTED, ofFullCopy(COPPER_BLOCK)));

        WAXED_THERMOSTATS = ImmutableMap.<WeatherState, ThermostatBlock>builder()
                .put(UNAFFECTED, WAXED_THERMOSTAT)
                .put(EXPOSED, WAXED_EXPOSED_THERMOSTAT)
                .put(WEATHERED, WAXED_WEATHERED_THERMOSTAT)
                .put(OXIDIZED, WAXED_OXIDIZED_THERMOSTAT)
                .build();

        THERMOSTAT = register(new WeatheringThermostatBlock(UNAFFECTED, ofFullCopy(COPPER_BLOCK)));
        EXPOSED_THERMOSTAT = register(new WeatheringThermostatBlock(UNAFFECTED, ofFullCopy(COPPER_BLOCK)));
        WEATHERED_THERMOSTAT = register(new WeatheringThermostatBlock(UNAFFECTED, ofFullCopy(COPPER_BLOCK)));
        OXIDIZED_THERMOSTAT = register(new WeatheringThermostatBlock(UNAFFECTED, ofFullCopy(COPPER_BLOCK)));

        THERMOSTATS = ImmutableMap.<WeatherState, WeatheringThermostatBlock>builder()
                .put(UNAFFECTED, THERMOSTAT)
                .put(EXPOSED, EXPOSED_THERMOSTAT)
                .put(WEATHERED, WEATHERED_THERMOSTAT)
                .put(OXIDIZED, OXIDIZED_THERMOSTAT)
                .build();
    }

    private static final <T extends Block & Worded> T register(T block) {
        return register(block, toId(block.getWords()));
    }

    private static final <T extends Block> T register(T block, String id) {
        var key = ResourceLocation.tryBuild(MOD_ID, id);
        Registry.register(BuiltInRegistries.ITEM, key, new BlockItem(block, new Item.Properties()));
        return Registry.register(BuiltInRegistries.BLOCK, key, block);
    }

    private static final String toId(String... words) {
        return String.join("_", words).toLowerCase();
    }

    public static final void initialize() {
        // Trigger static initialization
    }
}
