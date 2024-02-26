package niv.heater;

import static net.minecraft.block.Blocks.COPPER_BLOCK;
import static net.minecraft.block.Blocks.FURNACE;
import static net.minecraft.block.Oxidizable.OxidationLevel.EXPOSED;
import static net.minecraft.block.Oxidizable.OxidationLevel.OXIDIZED;
import static net.minecraft.block.Oxidizable.OxidationLevel.UNAFFECTED;
import static net.minecraft.block.Oxidizable.OxidationLevel.WEATHERED;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import niv.heater.block.HeatPipeBlock;
import niv.heater.block.HeaterBlock;
import niv.heater.block.OxidizableHeatPipeBlock;
import niv.heater.block.OxidizableHeaterBlock;
import niv.heater.block.OxidizableThermostatBlock;
import niv.heater.block.ThermostatBlock;
import niv.heater.block.entity.HeaterBlockEntity;
import niv.heater.screen.HeaterScreenHandler;

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

    public static final ScreenHandlerType<HeaterScreenHandler> HEATER_SCREEN_HANDLER;

    public static final ItemGroup HEATER_GROUP;

    static {
        MOD_ID = "heater";

        final var heater = "heater";
        final var heatPipe = "heat_pipe";
        final var thermostat = "thermostat";

        final var exposed = "exposed_";
        final var weathered = "weathered_";
        final var oxidized = "oxidized_";
        final var waxed = "waxed_";

        var id = new Identifier(MOD_ID, "");

        id = id.withPath(heater);
        HEATER_BLOCK = Registry.register(Registries.BLOCK, id,
                new OxidizableHeaterBlock(UNAFFECTED, FabricBlockSettings.copyOf(FURNACE)));
        EXPOSED_HEATER_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(exposed),
                new OxidizableHeaterBlock(EXPOSED, FabricBlockSettings.copyOf(FURNACE)));
        WEATHERED_HEATER_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(weathered),
                new OxidizableHeaterBlock(WEATHERED, FabricBlockSettings.copyOf(FURNACE)));
        OXIDIZED_HEATER_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(oxidized),
                new OxidizableHeaterBlock(OXIDIZED, FabricBlockSettings.copyOf(FURNACE)));

        WAXED_HEATER_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(waxed),
                new HeaterBlock(UNAFFECTED, FabricBlockSettings.copyOf(FURNACE)));
        WAXED_EXPOSED_HEATER_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(waxed + exposed),
                new HeaterBlock(EXPOSED, FabricBlockSettings.copyOf(FURNACE)));
        WAXED_WEATHERED_HEATER_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(waxed + weathered),
                new HeaterBlock(WEATHERED, FabricBlockSettings.copyOf(FURNACE)));
        WAXED_OXIDIZED_HEATER_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(waxed + oxidized),
                new HeaterBlock(OXIDIZED, FabricBlockSettings.copyOf(FURNACE)));

        id = id.withPath(heatPipe);
        HEAT_PIPE_BLOCK = Registry.register(Registries.BLOCK, id,
                new OxidizableHeatPipeBlock(UNAFFECTED, FabricBlockSettings.copyOf(COPPER_BLOCK)));
        EXPOSED_HEAT_PIPE_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(exposed),
                new OxidizableHeatPipeBlock(EXPOSED, FabricBlockSettings.copyOf(COPPER_BLOCK)));
        WEATHERED_HEAT_PIPE_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(weathered),
                new OxidizableHeatPipeBlock(WEATHERED, FabricBlockSettings.copyOf(COPPER_BLOCK)));
        OXIDIZED_HEAT_PIPE_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(oxidized),
                new OxidizableHeatPipeBlock(OXIDIZED, FabricBlockSettings.copyOf(COPPER_BLOCK)));

        WAXED_HEAT_PIPE_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(waxed),
                new HeatPipeBlock(UNAFFECTED, FabricBlockSettings.copyOf(COPPER_BLOCK)));
        WAXED_EXPOSED_HEAT_PIPE_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(waxed + exposed),
                new HeatPipeBlock(EXPOSED, FabricBlockSettings.copyOf(COPPER_BLOCK)));
        WAXED_WEATHERED_HEAT_PIPE_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(waxed + weathered),
                new HeatPipeBlock(WEATHERED, FabricBlockSettings.copyOf(COPPER_BLOCK)));
        WAXED_OXIDIZED_HEAT_PIPE_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(waxed + oxidized),
                new HeatPipeBlock(OXIDIZED, FabricBlockSettings.copyOf(COPPER_BLOCK)));

        id = id.withPath(thermostat);
        THERMOSTAT_BLOCK = Registry.register(Registries.BLOCK, id,
                new OxidizableThermostatBlock(UNAFFECTED, FabricBlockSettings.copyOf(COPPER_BLOCK)));
        EXPOSED_THERMOSTAT_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(exposed),
                new OxidizableThermostatBlock(EXPOSED, FabricBlockSettings.copyOf(COPPER_BLOCK)));
        WEATHERED_THERMOSTAT_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(weathered),
                new OxidizableThermostatBlock(WEATHERED, FabricBlockSettings.copyOf(COPPER_BLOCK)));
        OXIDIZED_THERMOSTAT_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(oxidized),
                new OxidizableThermostatBlock(OXIDIZED, FabricBlockSettings.copyOf(COPPER_BLOCK)));

        WAXED_THERMOSTAT_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(waxed),
                new ThermostatBlock(UNAFFECTED, FabricBlockSettings.copyOf(COPPER_BLOCK)));
        WAXED_EXPOSED_THERMOSTAT_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(waxed + exposed),
                new ThermostatBlock(EXPOSED, FabricBlockSettings.copyOf(COPPER_BLOCK)));
        WAXED_WEATHERED_THERMOSTAT_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(waxed + weathered),
                new ThermostatBlock(WEATHERED, FabricBlockSettings.copyOf(COPPER_BLOCK)));
        WAXED_OXIDIZED_THERMOSTAT_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(waxed + oxidized),
                new ThermostatBlock(OXIDIZED, FabricBlockSettings.copyOf(COPPER_BLOCK)));

        id = id.withPath(heater);
        HEATER_ITEM = Registry.register(Registries.ITEM, id,
                new BlockItem(HEATER_BLOCK, new FabricItemSettings()));
        EXPOSED_HEATER_ITEM = Registry.register(Registries.ITEM, id.withPrefixedPath(exposed),
                new BlockItem(EXPOSED_HEATER_BLOCK, new FabricItemSettings()));
        WEATHERED_HEATER_ITEM = Registry.register(Registries.ITEM, id.withPrefixedPath(weathered),
                new BlockItem(WEATHERED_HEATER_BLOCK, new FabricItemSettings()));
        OXIDIZED_HEATER_ITEM = Registry.register(Registries.ITEM, id.withPrefixedPath(oxidized),
                new BlockItem(OXIDIZED_HEATER_BLOCK, new FabricItemSettings()));

        WAXED_HEATER_ITEM = Registry.register(Registries.ITEM, id.withPrefixedPath(waxed),
                new BlockItem(WAXED_HEATER_BLOCK, new FabricItemSettings()));
        WAXED_EXPOSED_HEATER_ITEM = Registry.register(Registries.ITEM, id.withPrefixedPath(waxed + exposed),
                new BlockItem(WAXED_EXPOSED_HEATER_BLOCK, new FabricItemSettings()));
        WAXED_WEATHERED_HEATER_ITEM = Registry.register(Registries.ITEM, id.withPrefixedPath(waxed + weathered),
                new BlockItem(WAXED_WEATHERED_HEATER_BLOCK, new FabricItemSettings()));
        WAXED_OXIDIZED_HEATER_ITEM = Registry.register(Registries.ITEM, id.withPrefixedPath(waxed + oxidized),
                new BlockItem(WAXED_OXIDIZED_HEATER_BLOCK, new FabricItemSettings()));

        id = id.withPath(heatPipe);
        HEAT_PIPE_ITEM = Registry.register(Registries.ITEM, id,
                new BlockItem(HEAT_PIPE_BLOCK, new FabricItemSettings()));
        EXPOSED_HEAT_PIPE_ITEM = Registry.register(Registries.ITEM, id.withPrefixedPath(exposed),
                new BlockItem(EXPOSED_HEAT_PIPE_BLOCK, new FabricItemSettings()));
        WEATHERED_HEAT_PIPE_ITEM = Registry.register(Registries.ITEM, id.withPrefixedPath(weathered),
                new BlockItem(WEATHERED_HEAT_PIPE_BLOCK, new FabricItemSettings()));
        OXIDIZED_HEAT_PIPE_ITEM = Registry.register(Registries.ITEM, id.withPrefixedPath(oxidized),
                new BlockItem(OXIDIZED_HEAT_PIPE_BLOCK, new FabricItemSettings()));

        WAXED_HEAT_PIPE_ITEM = Registry.register(Registries.ITEM, id.withPrefixedPath(waxed),
                new BlockItem(WAXED_HEAT_PIPE_BLOCK, new FabricItemSettings()));
        WAXED_EXPOSED_HEAT_PIPE_ITEM = Registry.register(Registries.ITEM, id.withPrefixedPath(waxed + exposed),
                new BlockItem(WAXED_EXPOSED_HEAT_PIPE_BLOCK, new FabricItemSettings()));
        WAXED_WEATHERED_HEAT_PIPE_ITEM = Registry.register(Registries.ITEM, id.withPrefixedPath(waxed + weathered),
                new BlockItem(WAXED_WEATHERED_HEAT_PIPE_BLOCK, new FabricItemSettings()));
        WAXED_OXIDIZED_HEAT_PIPE_ITEM = Registry.register(Registries.ITEM, id.withPrefixedPath(waxed + oxidized),
                new BlockItem(WAXED_OXIDIZED_HEAT_PIPE_BLOCK, new FabricItemSettings()));

        id = id.withPath(thermostat);
        THERMOSTAT_ITEM = Registry.register(Registries.ITEM, id,
                new BlockItem(THERMOSTAT_BLOCK, new FabricItemSettings()));
        EXPOSED_THERMOSTAT_ITEM = Registry.register(Registries.ITEM, id.withPrefixedPath(exposed),
                new BlockItem(EXPOSED_THERMOSTAT_BLOCK, new FabricItemSettings()));
        WEATHERED_THERMOSTAT_ITEM = Registry.register(Registries.ITEM, id.withPrefixedPath(weathered),
                new BlockItem(WEATHERED_THERMOSTAT_BLOCK, new FabricItemSettings()));
        OXIDIZED_THERMOSTAT_ITEM = Registry.register(Registries.ITEM, id.withPrefixedPath(oxidized),
                new BlockItem(OXIDIZED_THERMOSTAT_BLOCK, new FabricItemSettings()));

        WAXED_THERMOSTAT_ITEM = Registry.register(Registries.ITEM, id.withPrefixedPath(waxed),
                new BlockItem(WAXED_THERMOSTAT_BLOCK, new FabricItemSettings()));
        WAXED_EXPOSED_THERMOSTAT_ITEM = Registry.register(Registries.ITEM, id.withPrefixedPath(waxed + exposed),
                new BlockItem(WAXED_EXPOSED_THERMOSTAT_BLOCK, new FabricItemSettings()));
        WAXED_WEATHERED_THERMOSTAT_ITEM = Registry.register(Registries.ITEM, id.withPrefixedPath(waxed + weathered),
                new BlockItem(WAXED_WEATHERED_THERMOSTAT_BLOCK, new FabricItemSettings()));
        WAXED_OXIDIZED_THERMOSTAT_ITEM = Registry.register(Registries.ITEM, id.withPrefixedPath(waxed + oxidized),
                new BlockItem(WAXED_OXIDIZED_THERMOSTAT_BLOCK, new FabricItemSettings()));

        id = id.withPath(heater);
        HEATER_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, id,
                FabricBlockEntityTypeBuilder.create(HeaterBlockEntity::new,
                        HEATER_BLOCK, EXPOSED_HEATER_BLOCK,
                        WEATHERED_HEATER_BLOCK, OXIDIZED_HEATER_BLOCK,
                        WAXED_HEATER_BLOCK, WAXED_EXPOSED_HEATER_BLOCK,
                        WAXED_WEATHERED_HEATER_BLOCK, WAXED_OXIDIZED_HEATER_BLOCK)
                        .build(null));

        HEATER_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, id,
                new ScreenHandlerType<>(HeaterScreenHandler::new, FeatureFlags.VANILLA_FEATURES));

        HEATER_GROUP = FabricItemGroup.builder()
                .icon(HEATER_ITEM::getDefaultStack)
                .displayName(Text.translatable("itemGroup.heater.tab"))
                .entries((context, entries) -> {
                    entries.add(HEATER_ITEM);
                    entries.add(EXPOSED_HEATER_ITEM);
                    entries.add(WEATHERED_HEATER_ITEM);
                    entries.add(OXIDIZED_HEATER_ITEM);

                    entries.add(WAXED_HEATER_ITEM);
                    entries.add(WAXED_EXPOSED_HEATER_ITEM);
                    entries.add(WAXED_WEATHERED_HEATER_ITEM);
                    entries.add(WAXED_OXIDIZED_HEATER_ITEM);

                    entries.add(THERMOSTAT_ITEM);
                    entries.add(EXPOSED_THERMOSTAT_ITEM);
                    entries.add(WEATHERED_THERMOSTAT_ITEM);
                    entries.add(OXIDIZED_THERMOSTAT_ITEM);

                    entries.add(WAXED_THERMOSTAT_ITEM);
                    entries.add(WAXED_EXPOSED_THERMOSTAT_ITEM);
                    entries.add(WAXED_WEATHERED_THERMOSTAT_ITEM);
                    entries.add(WAXED_OXIDIZED_THERMOSTAT_ITEM);

                    entries.add(HEAT_PIPE_ITEM);
                    entries.add(EXPOSED_HEAT_PIPE_ITEM);
                    entries.add(WEATHERED_HEAT_PIPE_ITEM);
                    entries.add(OXIDIZED_HEAT_PIPE_ITEM);

                    entries.add(WAXED_HEAT_PIPE_ITEM);
                    entries.add(WAXED_EXPOSED_HEAT_PIPE_ITEM);
                    entries.add(WAXED_WEATHERED_HEAT_PIPE_ITEM);
                    entries.add(WAXED_OXIDIZED_HEAT_PIPE_ITEM);
                }).build();
        Registry.register(Registries.ITEM_GROUP, id.withPath("tab"), HEATER_GROUP);
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
