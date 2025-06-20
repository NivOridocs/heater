package niv.heater.registry;

import static net.minecraft.world.level.block.Blocks.COPPER_BLOCK;
import static net.minecraft.world.level.block.Blocks.FURNACE;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.EXPOSED;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.OXIDIZED;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.UNAFFECTED;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.WEATHERED;
import static net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy;
import static niv.heater.Heater.MOD_ID;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;

import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import niv.heater.block.HeatPipeBlock;
import niv.heater.block.HeaterBlock;
import niv.heater.block.ThermostatBlock;
import niv.heater.block.WeatheringHeatPipeBlock;
import niv.heater.block.WeatheringHeaterBlock;
import niv.heater.block.WeatheringThermostatBlock;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

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

    private static final String HEATER_NAME = "heater";
    private static final String HEAT_PIPE_NAME = "heat_pipe";
    private static final String THERMOSTAT_NAME = "thermostat";

    static {
        WAXED_HEATER = registerWaxed(HEATER_NAME, HeaterBlock::new, UNAFFECTED, ofFullCopy(FURNACE));
        WAXED_EXPOSED_HEATER = registerWaxed(HEATER_NAME, HeaterBlock::new, EXPOSED, ofFullCopy(FURNACE));
        WAXED_WEATHERED_HEATER = registerWaxed(HEATER_NAME, HeaterBlock::new, WEATHERED, ofFullCopy(FURNACE));
        WAXED_OXIDIZED_HEATER = registerWaxed(HEATER_NAME, HeaterBlock::new, OXIDIZED, ofFullCopy(FURNACE));

        WAXED_HEATERS = ImmutableMap.<WeatherState, HeaterBlock>builder()
                .put(UNAFFECTED, WAXED_HEATER)
                .put(EXPOSED, WAXED_EXPOSED_HEATER)
                .put(WEATHERED, WAXED_WEATHERED_HEATER)
                .put(OXIDIZED, WAXED_OXIDIZED_HEATER)
                .build();

        HEATER = registerUnwaxed(HEATER_NAME, WeatheringHeaterBlock::new, UNAFFECTED, ofFullCopy(FURNACE));
        EXPOSED_HEATER = registerUnwaxed(HEATER_NAME, WeatheringHeaterBlock::new, EXPOSED, ofFullCopy(FURNACE));
        WEATHERED_HEATER = registerUnwaxed(HEATER_NAME, WeatheringHeaterBlock::new, WEATHERED, ofFullCopy(FURNACE));
        OXIDIZED_HEATER = registerUnwaxed(HEATER_NAME, WeatheringHeaterBlock::new, OXIDIZED, ofFullCopy(FURNACE));

        HEATERS = ImmutableMap.<WeatherState, WeatheringHeaterBlock>builder()
                .put(UNAFFECTED, HEATER)
                .put(EXPOSED, EXPOSED_HEATER)
                .put(WEATHERED, WEATHERED_HEATER)
                .put(OXIDIZED, OXIDIZED_HEATER)
                .build();

        OxidizableBlocksRegistry.registerWaxableBlockPair(HEATER, WAXED_HEATER);
        OxidizableBlocksRegistry.registerWaxableBlockPair(EXPOSED_HEATER, WAXED_EXPOSED_HEATER);
        OxidizableBlocksRegistry.registerWaxableBlockPair(WEATHERED_HEATER, WAXED_WEATHERED_HEATER);
        OxidizableBlocksRegistry.registerWaxableBlockPair(OXIDIZED_HEATER, WAXED_OXIDIZED_HEATER);

        OxidizableBlocksRegistry.registerOxidizableBlockPair(HEATER, EXPOSED_HEATER);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(EXPOSED_HEATER, WEATHERED_HEATER);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(WEATHERED_HEATER, OXIDIZED_HEATER);

        WAXED_HEAT_PIPE = registerWaxed(HEAT_PIPE_NAME, HeatPipeBlock::new, UNAFFECTED, ofFullCopy(COPPER_BLOCK));
        WAXED_EXPOSED_HEAT_PIPE = registerWaxed(HEAT_PIPE_NAME, HeatPipeBlock::new, EXPOSED, ofFullCopy(COPPER_BLOCK));
        WAXED_WEATHERED_HEAT_PIPE = registerWaxed(HEAT_PIPE_NAME, HeatPipeBlock::new, WEATHERED, ofFullCopy(COPPER_BLOCK));
        WAXED_OXIDIZED_HEAT_PIPE = registerWaxed(HEAT_PIPE_NAME, HeatPipeBlock::new, OXIDIZED, ofFullCopy(COPPER_BLOCK));

        WAXED_HEAT_PIPES = ImmutableMap.<WeatherState, HeatPipeBlock>builder()
                .put(UNAFFECTED, WAXED_HEAT_PIPE)
                .put(EXPOSED, WAXED_EXPOSED_HEAT_PIPE)
                .put(WEATHERED, WAXED_WEATHERED_HEAT_PIPE)
                .put(OXIDIZED, WAXED_OXIDIZED_HEAT_PIPE)
                .build();

        HEAT_PIPE = registerUnwaxed(HEAT_PIPE_NAME, WeatheringHeatPipeBlock::new, UNAFFECTED, ofFullCopy(COPPER_BLOCK));
        EXPOSED_HEAT_PIPE = registerUnwaxed(HEAT_PIPE_NAME, WeatheringHeatPipeBlock::new, EXPOSED, ofFullCopy(COPPER_BLOCK));
        WEATHERED_HEAT_PIPE = registerUnwaxed(HEAT_PIPE_NAME, WeatheringHeatPipeBlock::new, WEATHERED, ofFullCopy(COPPER_BLOCK));
        OXIDIZED_HEAT_PIPE = registerUnwaxed(HEAT_PIPE_NAME, WeatheringHeatPipeBlock::new, OXIDIZED, ofFullCopy(COPPER_BLOCK));

        HEAT_PIPES = ImmutableMap.<WeatherState, WeatheringHeatPipeBlock>builder()
                .put(UNAFFECTED, HEAT_PIPE)
                .put(EXPOSED, EXPOSED_HEAT_PIPE)
                .put(WEATHERED, WEATHERED_HEAT_PIPE)
                .put(OXIDIZED, OXIDIZED_HEAT_PIPE)
                .build();

        OxidizableBlocksRegistry.registerWaxableBlockPair(HEAT_PIPE, WAXED_HEAT_PIPE);
        OxidizableBlocksRegistry.registerWaxableBlockPair(EXPOSED_HEAT_PIPE, WAXED_EXPOSED_HEAT_PIPE);
        OxidizableBlocksRegistry.registerWaxableBlockPair(WEATHERED_HEAT_PIPE, WAXED_WEATHERED_HEAT_PIPE);
        OxidizableBlocksRegistry.registerWaxableBlockPair(OXIDIZED_HEAT_PIPE, WAXED_OXIDIZED_HEAT_PIPE);

        OxidizableBlocksRegistry.registerOxidizableBlockPair(HEAT_PIPE, EXPOSED_HEAT_PIPE);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(EXPOSED_HEAT_PIPE, WEATHERED_HEAT_PIPE);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(WEATHERED_HEAT_PIPE, OXIDIZED_HEAT_PIPE);

        WAXED_THERMOSTAT = registerWaxed(THERMOSTAT_NAME, ThermostatBlock::new, UNAFFECTED, ofFullCopy(COPPER_BLOCK));
        WAXED_EXPOSED_THERMOSTAT = registerWaxed(THERMOSTAT_NAME, ThermostatBlock::new, EXPOSED, ofFullCopy(COPPER_BLOCK));
        WAXED_WEATHERED_THERMOSTAT = registerWaxed(THERMOSTAT_NAME, ThermostatBlock::new, WEATHERED, ofFullCopy(COPPER_BLOCK));
        WAXED_OXIDIZED_THERMOSTAT = registerWaxed(THERMOSTAT_NAME, ThermostatBlock::new, OXIDIZED, ofFullCopy(COPPER_BLOCK));

        WAXED_THERMOSTATS = ImmutableMap.<WeatherState, ThermostatBlock>builder()
                .put(UNAFFECTED, WAXED_THERMOSTAT)
                .put(EXPOSED, WAXED_EXPOSED_THERMOSTAT)
                .put(WEATHERED, WAXED_WEATHERED_THERMOSTAT)
                .put(OXIDIZED, WAXED_OXIDIZED_THERMOSTAT)
                .build();

        THERMOSTAT = registerUnwaxed(THERMOSTAT_NAME, WeatheringThermostatBlock::new, UNAFFECTED, ofFullCopy(COPPER_BLOCK));
        EXPOSED_THERMOSTAT = registerUnwaxed(THERMOSTAT_NAME, WeatheringThermostatBlock::new, EXPOSED, ofFullCopy(COPPER_BLOCK));
        WEATHERED_THERMOSTAT = registerUnwaxed(THERMOSTAT_NAME, WeatheringThermostatBlock::new, WEATHERED, ofFullCopy(COPPER_BLOCK));
        OXIDIZED_THERMOSTAT = registerUnwaxed(THERMOSTAT_NAME, WeatheringThermostatBlock::new, OXIDIZED, ofFullCopy(COPPER_BLOCK));

        THERMOSTATS = ImmutableMap.<WeatherState, WeatheringThermostatBlock>builder()
                .put(UNAFFECTED, THERMOSTAT)
                .put(EXPOSED, EXPOSED_THERMOSTAT)
                .put(WEATHERED, WEATHERED_THERMOSTAT)
                .put(OXIDIZED, OXIDIZED_THERMOSTAT)
                .build();

        OxidizableBlocksRegistry.registerWaxableBlockPair(THERMOSTAT, WAXED_THERMOSTAT);
        OxidizableBlocksRegistry.registerWaxableBlockPair(EXPOSED_THERMOSTAT, WAXED_EXPOSED_THERMOSTAT);
        OxidizableBlocksRegistry.registerWaxableBlockPair(WEATHERED_THERMOSTAT, WAXED_WEATHERED_THERMOSTAT);
        OxidizableBlocksRegistry.registerWaxableBlockPair(OXIDIZED_THERMOSTAT, WAXED_OXIDIZED_THERMOSTAT);

        OxidizableBlocksRegistry.registerOxidizableBlockPair(THERMOSTAT, EXPOSED_THERMOSTAT);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(EXPOSED_THERMOSTAT, WEATHERED_THERMOSTAT);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(WEATHERED_THERMOSTAT, OXIDIZED_THERMOSTAT);
    }

    private static final <T extends Block> T registerWaxed(String name,
            BiFunction<WeatherState, Properties, T> builder,
            WeatherState weathering, Properties properties) {
        return register("waxed_" + toString(weathering) + name,
                p -> builder.apply(weathering, p), properties);
    }

    private static final <T extends Block> T registerUnwaxed(String name,
            BiFunction<WeatherState, Properties, T> builder,
            WeatherState weathering, Properties properties) {
        return register(toString(weathering) + name,
                p -> builder.apply(weathering, p), properties);
    }

    private static final <T extends Block> T register(String name,
            Function<Properties, T> constructor,
            Properties properties) {
        var blockKey = ResourceKey.create(Registries.BLOCK, ResourceLocation.tryBuild(MOD_ID, name));
        var block = constructor.apply(properties.setId(blockKey));
        block = Registry.register(BuiltInRegistries.BLOCK, blockKey, block);

        var itemKey = ResourceKey.create(Registries.ITEM, ResourceLocation.tryBuild(MOD_ID, name));
        Registry.register(BuiltInRegistries.ITEM, itemKey,
                new BlockItem(block, new Item.Properties().useBlockDescriptionPrefix().setId(itemKey)));

        return block;
    }

    private static final String toString(WeatherState weathering) {
        switch (weathering) {
            case UNAFFECTED:
                return "";
            case EXPOSED:
                return "exposed_";
            case WEATHERED:
                return "weathered_";
            case OXIDIZED:
                return "oxidized_";
            default:
                throw new IllegalArgumentException(Objects.toString(weathering));
        }
    }

    public static final void initialize() {
        // Trigger static initialization
    }
}
