package niv.heater;

import static net.minecraft.core.registries.BuiltInRegistries.BLOCK;
import static net.minecraft.core.registries.BuiltInRegistries.BLOCK_ENTITY_TYPE;
import static net.minecraft.core.registries.BuiltInRegistries.CREATIVE_MODE_TAB;
import static net.minecraft.core.registries.BuiltInRegistries.ITEM;
import static net.minecraft.core.registries.BuiltInRegistries.MENU;
import static net.minecraft.world.level.block.Blocks.COPPER_BLOCK;
import static net.minecraft.world.level.block.Blocks.FURNACE;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.EXPOSED;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.OXIDIZED;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.UNAFFECTED;
import static net.minecraft.world.level.block.WeatheringCopper.WeatherState.WEATHERED;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import niv.heater.block.HeatPipeBlock;
import niv.heater.block.HeaterBlock;
import niv.heater.block.ThermostatBlock;
import niv.heater.block.WeatheringHeatPipeBlock;
import niv.heater.block.WeatheringHeaterBlock;
import niv.heater.block.WeatheringThermostatBlock;
import niv.heater.block.entity.HeaterBlockEntity;
import niv.heater.screen.HeaterMenu;

public class Heater implements ModInitializer {

    public static final String MOD_ID;

    public static final Logger LOGGER = LoggerFactory.getLogger("Heater");

    public static final Block HEATER_BLOCK;
    public static final Block EXPOSED_HEATER_BLOCK;
    public static final Block WEATHERED_HEATER_BLOCK;
    public static final Block OXIDIZED_HEATER_BLOCK;

    public static final Block WAXED_HEATER_BLOCK;
    public static final Block WAXED_EXPOSED_HEATER_BLOCK;
    public static final Block WAXED_WEATHERED_HEATER_BLOCK;
    public static final Block WAXED_OXIDIZED_HEATER_BLOCK;

    public static final Block HEAT_PIPE_BLOCK;
    public static final Block EXPOSED_HEAT_PIPE_BLOCK;
    public static final Block WEATHERED_HEAT_PIPE_BLOCK;
    public static final Block OXIDIZED_HEAT_PIPE_BLOCK;

    public static final Block WAXED_HEAT_PIPE_BLOCK;
    public static final Block WAXED_EXPOSED_HEAT_PIPE_BLOCK;
    public static final Block WAXED_WEATHERED_HEAT_PIPE_BLOCK;
    public static final Block WAXED_OXIDIZED_HEAT_PIPE_BLOCK;

    public static final Block THERMOSTAT_BLOCK;
    public static final Block EXPOSED_THERMOSTAT_BLOCK;
    public static final Block WEATHERED_THERMOSTAT_BLOCK;
    public static final Block OXIDIZED_THERMOSTAT_BLOCK;

    public static final Block WAXED_THERMOSTAT_BLOCK;
    public static final Block WAXED_EXPOSED_THERMOSTAT_BLOCK;
    public static final Block WAXED_WEATHERED_THERMOSTAT_BLOCK;
    public static final Block WAXED_OXIDIZED_THERMOSTAT_BLOCK;

    public static final Item HEATER_ITEM;
    public static final Item EXPOSED_HEATER_ITEM;
    public static final Item WEATHERED_HEATER_ITEM;
    public static final Item OXIDIZED_HEATER_ITEM;

    public static final Item WAXED_HEATER_ITEM;
    public static final Item WAXED_EXPOSED_HEATER_ITEM;
    public static final Item WAXED_WEATHERED_HEATER_ITEM;
    public static final Item WAXED_OXIDIZED_HEATER_ITEM;

    public static final Item HEAT_PIPE_ITEM;
    public static final Item EXPOSED_HEAT_PIPE_ITEM;
    public static final Item WEATHERED_HEAT_PIPE_ITEM;
    public static final Item OXIDIZED_HEAT_PIPE_ITEM;

    public static final Item WAXED_HEAT_PIPE_ITEM;
    public static final Item WAXED_EXPOSED_HEAT_PIPE_ITEM;
    public static final Item WAXED_WEATHERED_HEAT_PIPE_ITEM;
    public static final Item WAXED_OXIDIZED_HEAT_PIPE_ITEM;

    public static final Item THERMOSTAT_ITEM;
    public static final Item EXPOSED_THERMOSTAT_ITEM;
    public static final Item WEATHERED_THERMOSTAT_ITEM;
    public static final Item OXIDIZED_THERMOSTAT_ITEM;

    public static final Item WAXED_THERMOSTAT_ITEM;
    public static final Item WAXED_EXPOSED_THERMOSTAT_ITEM;
    public static final Item WAXED_WEATHERED_THERMOSTAT_ITEM;
    public static final Item WAXED_OXIDIZED_THERMOSTAT_ITEM;

    public static final BlockEntityType<HeaterBlockEntity> HEATER_BLOCK_ENTITY;

    public static final MenuType<HeaterMenu> HEATER_MENU;

    public static final CreativeModeTab HEATER_TAB;

    static {
        MOD_ID = "heater";

        final var heater = "heater";
        final var heatPipe = "heat_pipe";
        final var thermostat = "thermostat";

        final var exposed = "exposed_";
        final var weathered = "weathered_";
        final var oxidized = "oxidized_";
        final var waxed = "waxed_";

        var id = new ResourceLocation(MOD_ID, "");

        id = id.withPath(heater);
        HEATER_BLOCK = Registry.register(BLOCK, id,
                new WeatheringHeaterBlock(UNAFFECTED, FabricBlockSettings.copyOf(FURNACE)));
        EXPOSED_HEATER_BLOCK = Registry.register(BLOCK, id.withPrefix(exposed),
                new WeatheringHeaterBlock(EXPOSED, FabricBlockSettings.copyOf(FURNACE)));
        WEATHERED_HEATER_BLOCK = Registry.register(BLOCK, id.withPrefix(weathered),
                new WeatheringHeaterBlock(WEATHERED, FabricBlockSettings.copyOf(FURNACE)));
        OXIDIZED_HEATER_BLOCK = Registry.register(BLOCK, id.withPrefix(oxidized),
                new WeatheringHeaterBlock(OXIDIZED, FabricBlockSettings.copyOf(FURNACE)));

        WAXED_HEATER_BLOCK = Registry.register(BLOCK, id.withPrefix(waxed),
                new HeaterBlock(UNAFFECTED, FabricBlockSettings.copyOf(FURNACE)));
        WAXED_EXPOSED_HEATER_BLOCK = Registry.register(BLOCK, id.withPrefix(waxed + exposed),
                new HeaterBlock(EXPOSED, FabricBlockSettings.copyOf(FURNACE)));
        WAXED_WEATHERED_HEATER_BLOCK = Registry.register(BLOCK, id.withPrefix(waxed + weathered),
                new HeaterBlock(WEATHERED, FabricBlockSettings.copyOf(FURNACE)));
        WAXED_OXIDIZED_HEATER_BLOCK = Registry.register(BLOCK, id.withPrefix(waxed + oxidized),
                new HeaterBlock(OXIDIZED, FabricBlockSettings.copyOf(FURNACE)));

        id = id.withPath(heatPipe);
        HEAT_PIPE_BLOCK = Registry.register(BLOCK, id,
                new WeatheringHeatPipeBlock(UNAFFECTED, FabricBlockSettings.copyOf(COPPER_BLOCK)));
        EXPOSED_HEAT_PIPE_BLOCK = Registry.register(BLOCK, id.withPrefix(exposed),
                new WeatheringHeatPipeBlock(EXPOSED, FabricBlockSettings.copyOf(COPPER_BLOCK)));
        WEATHERED_HEAT_PIPE_BLOCK = Registry.register(BLOCK, id.withPrefix(weathered),
                new WeatheringHeatPipeBlock(WEATHERED, FabricBlockSettings.copyOf(COPPER_BLOCK)));
        OXIDIZED_HEAT_PIPE_BLOCK = Registry.register(BLOCK, id.withPrefix(oxidized),
                new WeatheringHeatPipeBlock(OXIDIZED, FabricBlockSettings.copyOf(COPPER_BLOCK)));

        WAXED_HEAT_PIPE_BLOCK = Registry.register(BLOCK, id.withPrefix(waxed),
                new HeatPipeBlock(UNAFFECTED, FabricBlockSettings.copyOf(COPPER_BLOCK)));
        WAXED_EXPOSED_HEAT_PIPE_BLOCK = Registry.register(BLOCK, id.withPrefix(waxed + exposed),
                new HeatPipeBlock(EXPOSED, FabricBlockSettings.copyOf(COPPER_BLOCK)));
        WAXED_WEATHERED_HEAT_PIPE_BLOCK = Registry.register(BLOCK, id.withPrefix(waxed + weathered),
                new HeatPipeBlock(WEATHERED, FabricBlockSettings.copyOf(COPPER_BLOCK)));
        WAXED_OXIDIZED_HEAT_PIPE_BLOCK = Registry.register(BLOCK, id.withPrefix(waxed + oxidized),
                new HeatPipeBlock(OXIDIZED, FabricBlockSettings.copyOf(COPPER_BLOCK)));

        id = id.withPath(thermostat);
        THERMOSTAT_BLOCK = Registry.register(BLOCK, id,
                new WeatheringThermostatBlock(UNAFFECTED, FabricBlockSettings.copyOf(COPPER_BLOCK)));
        EXPOSED_THERMOSTAT_BLOCK = Registry.register(BLOCK, id.withPrefix(exposed),
                new WeatheringThermostatBlock(EXPOSED, FabricBlockSettings.copyOf(COPPER_BLOCK)));
        WEATHERED_THERMOSTAT_BLOCK = Registry.register(BLOCK, id.withPrefix(weathered),
                new WeatheringThermostatBlock(WEATHERED, FabricBlockSettings.copyOf(COPPER_BLOCK)));
        OXIDIZED_THERMOSTAT_BLOCK = Registry.register(BLOCK, id.withPrefix(oxidized),
                new WeatheringThermostatBlock(OXIDIZED, FabricBlockSettings.copyOf(COPPER_BLOCK)));

        WAXED_THERMOSTAT_BLOCK = Registry.register(BLOCK, id.withPrefix(waxed),
                new ThermostatBlock(UNAFFECTED, FabricBlockSettings.copyOf(COPPER_BLOCK)));
        WAXED_EXPOSED_THERMOSTAT_BLOCK = Registry.register(BLOCK, id.withPrefix(waxed + exposed),
                new ThermostatBlock(EXPOSED, FabricBlockSettings.copyOf(COPPER_BLOCK)));
        WAXED_WEATHERED_THERMOSTAT_BLOCK = Registry.register(BLOCK, id.withPrefix(waxed + weathered),
                new ThermostatBlock(WEATHERED, FabricBlockSettings.copyOf(COPPER_BLOCK)));
        WAXED_OXIDIZED_THERMOSTAT_BLOCK = Registry.register(BLOCK, id.withPrefix(waxed + oxidized),
                new ThermostatBlock(OXIDIZED, FabricBlockSettings.copyOf(COPPER_BLOCK)));

        id = id.withPath(heater);
        HEATER_ITEM = Registry.register(ITEM, id,
                new BlockItem(HEATER_BLOCK, new FabricItemSettings()));
        EXPOSED_HEATER_ITEM = Registry.register(ITEM, id.withPrefix(exposed),
                new BlockItem(EXPOSED_HEATER_BLOCK, new FabricItemSettings()));
        WEATHERED_HEATER_ITEM = Registry.register(ITEM, id.withPrefix(weathered),
                new BlockItem(WEATHERED_HEATER_BLOCK, new FabricItemSettings()));
        OXIDIZED_HEATER_ITEM = Registry.register(ITEM, id.withPrefix(oxidized),
                new BlockItem(OXIDIZED_HEATER_BLOCK, new FabricItemSettings()));

        WAXED_HEATER_ITEM = Registry.register(ITEM, id.withPrefix(waxed),
                new BlockItem(WAXED_HEATER_BLOCK, new FabricItemSettings()));
        WAXED_EXPOSED_HEATER_ITEM = Registry.register(ITEM, id.withPrefix(waxed + exposed),
                new BlockItem(WAXED_EXPOSED_HEATER_BLOCK, new FabricItemSettings()));
        WAXED_WEATHERED_HEATER_ITEM = Registry.register(ITEM, id.withPrefix(waxed + weathered),
                new BlockItem(WAXED_WEATHERED_HEATER_BLOCK, new FabricItemSettings()));
        WAXED_OXIDIZED_HEATER_ITEM = Registry.register(ITEM, id.withPrefix(waxed + oxidized),
                new BlockItem(WAXED_OXIDIZED_HEATER_BLOCK, new FabricItemSettings()));

        id = id.withPath(heatPipe);
        HEAT_PIPE_ITEM = Registry.register(ITEM, id,
                new BlockItem(HEAT_PIPE_BLOCK, new FabricItemSettings()));
        EXPOSED_HEAT_PIPE_ITEM = Registry.register(ITEM, id.withPrefix(exposed),
                new BlockItem(EXPOSED_HEAT_PIPE_BLOCK, new FabricItemSettings()));
        WEATHERED_HEAT_PIPE_ITEM = Registry.register(ITEM, id.withPrefix(weathered),
                new BlockItem(WEATHERED_HEAT_PIPE_BLOCK, new FabricItemSettings()));
        OXIDIZED_HEAT_PIPE_ITEM = Registry.register(ITEM, id.withPrefix(oxidized),
                new BlockItem(OXIDIZED_HEAT_PIPE_BLOCK, new FabricItemSettings()));

        WAXED_HEAT_PIPE_ITEM = Registry.register(ITEM, id.withPrefix(waxed),
                new BlockItem(WAXED_HEAT_PIPE_BLOCK, new FabricItemSettings()));
        WAXED_EXPOSED_HEAT_PIPE_ITEM = Registry.register(ITEM, id.withPrefix(waxed + exposed),
                new BlockItem(WAXED_EXPOSED_HEAT_PIPE_BLOCK, new FabricItemSettings()));
        WAXED_WEATHERED_HEAT_PIPE_ITEM = Registry.register(ITEM, id.withPrefix(waxed + weathered),
                new BlockItem(WAXED_WEATHERED_HEAT_PIPE_BLOCK, new FabricItemSettings()));
        WAXED_OXIDIZED_HEAT_PIPE_ITEM = Registry.register(ITEM, id.withPrefix(waxed + oxidized),
                new BlockItem(WAXED_OXIDIZED_HEAT_PIPE_BLOCK, new FabricItemSettings()));

        id = id.withPath(thermostat);
        THERMOSTAT_ITEM = Registry.register(ITEM, id,
                new BlockItem(THERMOSTAT_BLOCK, new FabricItemSettings()));
        EXPOSED_THERMOSTAT_ITEM = Registry.register(ITEM, id.withPrefix(exposed),
                new BlockItem(EXPOSED_THERMOSTAT_BLOCK, new FabricItemSettings()));
        WEATHERED_THERMOSTAT_ITEM = Registry.register(ITEM, id.withPrefix(weathered),
                new BlockItem(WEATHERED_THERMOSTAT_BLOCK, new FabricItemSettings()));
        OXIDIZED_THERMOSTAT_ITEM = Registry.register(ITEM, id.withPrefix(oxidized),
                new BlockItem(OXIDIZED_THERMOSTAT_BLOCK, new FabricItemSettings()));

        WAXED_THERMOSTAT_ITEM = Registry.register(ITEM, id.withPrefix(waxed),
                new BlockItem(WAXED_THERMOSTAT_BLOCK, new FabricItemSettings()));
        WAXED_EXPOSED_THERMOSTAT_ITEM = Registry.register(ITEM, id.withPrefix(waxed + exposed),
                new BlockItem(WAXED_EXPOSED_THERMOSTAT_BLOCK, new FabricItemSettings()));
        WAXED_WEATHERED_THERMOSTAT_ITEM = Registry.register(ITEM, id.withPrefix(waxed + weathered),
                new BlockItem(WAXED_WEATHERED_THERMOSTAT_BLOCK, new FabricItemSettings()));
        WAXED_OXIDIZED_THERMOSTAT_ITEM = Registry.register(ITEM, id.withPrefix(waxed + oxidized),
                new BlockItem(WAXED_OXIDIZED_THERMOSTAT_BLOCK, new FabricItemSettings()));

        id = id.withPath(heater);
        HEATER_BLOCK_ENTITY = Registry.register(BLOCK_ENTITY_TYPE, id,
                FabricBlockEntityTypeBuilder.create(HeaterBlockEntity::new,
                        HEATER_BLOCK, EXPOSED_HEATER_BLOCK,
                        WEATHERED_HEATER_BLOCK, OXIDIZED_HEATER_BLOCK,
                        WAXED_HEATER_BLOCK, WAXED_EXPOSED_HEATER_BLOCK,
                        WAXED_WEATHERED_HEATER_BLOCK, WAXED_OXIDIZED_HEATER_BLOCK)
                        .build(null));

        HEATER_MENU = Registry.register(MENU, id,
                new MenuType<>(HeaterMenu::new, FeatureFlags.VANILLA_SET));

        HEATER_TAB = FabricItemGroup.builder()
                .icon(HEATER_ITEM::getDefaultInstance)
                .title(Component.translatable("itemGroup.heater.tab"))
                .displayItems((parameters, output) -> {
                    output.accept(HEATER_ITEM);
                    output.accept(EXPOSED_HEATER_ITEM);
                    output.accept(WEATHERED_HEATER_ITEM);
                    output.accept(OXIDIZED_HEATER_ITEM);

                    output.accept(WAXED_HEATER_ITEM);
                    output.accept(WAXED_EXPOSED_HEATER_ITEM);
                    output.accept(WAXED_WEATHERED_HEATER_ITEM);
                    output.accept(WAXED_OXIDIZED_HEATER_ITEM);

                    output.accept(THERMOSTAT_ITEM);
                    output.accept(EXPOSED_THERMOSTAT_ITEM);
                    output.accept(WEATHERED_THERMOSTAT_ITEM);
                    output.accept(OXIDIZED_THERMOSTAT_ITEM);

                    output.accept(WAXED_THERMOSTAT_ITEM);
                    output.accept(WAXED_EXPOSED_THERMOSTAT_ITEM);
                    output.accept(WAXED_WEATHERED_THERMOSTAT_ITEM);
                    output.accept(WAXED_OXIDIZED_THERMOSTAT_ITEM);

                    output.accept(HEAT_PIPE_ITEM);
                    output.accept(EXPOSED_HEAT_PIPE_ITEM);
                    output.accept(WEATHERED_HEAT_PIPE_ITEM);
                    output.accept(OXIDIZED_HEAT_PIPE_ITEM);

                    output.accept(WAXED_HEAT_PIPE_ITEM);
                    output.accept(WAXED_EXPOSED_HEAT_PIPE_ITEM);
                    output.accept(WAXED_WEATHERED_HEAT_PIPE_ITEM);
                    output.accept(WAXED_OXIDIZED_HEAT_PIPE_ITEM);
                }).build();
        Registry.register(CREATIVE_MODE_TAB, id.withPath("tab"), HEATER_TAB);
    }

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LOGGER.info("Initialize");

        OxidizableBlocksRegistry.registerOxidizableBlockPair(HEATER_BLOCK, EXPOSED_HEATER_BLOCK);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(EXPOSED_HEATER_BLOCK, WEATHERED_HEATER_BLOCK);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(WEATHERED_HEATER_BLOCK, OXIDIZED_HEATER_BLOCK);

        OxidizableBlocksRegistry.registerWaxableBlockPair(HEATER_BLOCK, WAXED_HEATER_BLOCK);
        OxidizableBlocksRegistry.registerWaxableBlockPair(EXPOSED_HEATER_BLOCK, WAXED_EXPOSED_HEATER_BLOCK);
        OxidizableBlocksRegistry.registerWaxableBlockPair(WEATHERED_HEATER_BLOCK, WAXED_WEATHERED_HEATER_BLOCK);
        OxidizableBlocksRegistry.registerWaxableBlockPair(OXIDIZED_HEATER_BLOCK, WAXED_OXIDIZED_HEATER_BLOCK);

        OxidizableBlocksRegistry.registerOxidizableBlockPair(HEAT_PIPE_BLOCK, EXPOSED_HEAT_PIPE_BLOCK);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(EXPOSED_HEAT_PIPE_BLOCK, WEATHERED_HEAT_PIPE_BLOCK);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(WEATHERED_HEAT_PIPE_BLOCK, OXIDIZED_HEAT_PIPE_BLOCK);

        OxidizableBlocksRegistry.registerWaxableBlockPair(HEAT_PIPE_BLOCK, WAXED_HEAT_PIPE_BLOCK);
        OxidizableBlocksRegistry.registerWaxableBlockPair(EXPOSED_HEAT_PIPE_BLOCK, WAXED_EXPOSED_HEAT_PIPE_BLOCK);
        OxidizableBlocksRegistry.registerWaxableBlockPair(WEATHERED_HEAT_PIPE_BLOCK, WAXED_WEATHERED_HEAT_PIPE_BLOCK);
        OxidizableBlocksRegistry.registerWaxableBlockPair(OXIDIZED_HEAT_PIPE_BLOCK, WAXED_OXIDIZED_HEAT_PIPE_BLOCK);

        OxidizableBlocksRegistry.registerOxidizableBlockPair(THERMOSTAT_BLOCK, EXPOSED_THERMOSTAT_BLOCK);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(EXPOSED_THERMOSTAT_BLOCK, WEATHERED_THERMOSTAT_BLOCK);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(WEATHERED_THERMOSTAT_BLOCK, OXIDIZED_THERMOSTAT_BLOCK);

        OxidizableBlocksRegistry.registerWaxableBlockPair(THERMOSTAT_BLOCK, WAXED_THERMOSTAT_BLOCK);
        OxidizableBlocksRegistry.registerWaxableBlockPair(EXPOSED_THERMOSTAT_BLOCK, WAXED_EXPOSED_THERMOSTAT_BLOCK);
        OxidizableBlocksRegistry.registerWaxableBlockPair(WEATHERED_THERMOSTAT_BLOCK, WAXED_WEATHERED_THERMOSTAT_BLOCK);
        OxidizableBlocksRegistry.registerWaxableBlockPair(OXIDIZED_THERMOSTAT_BLOCK, WAXED_OXIDIZED_THERMOSTAT_BLOCK);
    }
}
