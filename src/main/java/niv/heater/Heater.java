package niv.heater;

import static net.minecraft.block.Blocks.COPPER_BLOCK;
import static net.minecraft.block.Blocks.FURNACE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

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

    public static final BlockEntityType<HeaterBlockEntity> HEATER_BLOCK_ENTITY;

    public static final ScreenHandlerType<HeaterScreenHandler> HEATER_SCREEN_HANDLER;

    static {
        MOD_ID = "heater";

        final var heater = "heater";
        final var heatPipe = "heat_pipe";

        final var exposed = "exposed_";
        final var weathered = "weathered_";
        final var oxidized = "oxidized_";
        final var waxed = "waxed_";

        var id = new Identifier(MOD_ID, "");

        id = id.withPath(heater);
        HEATER_BLOCK = Registry.register(Registries.BLOCK, id,
                new HeaterBlock(FabricBlockSettings.copyOf(FURNACE)));
        EXPOSED_HEATER_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(exposed),
                new HeaterBlock(FabricBlockSettings.copyOf(FURNACE)));
        WEATHERED_HEATER_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(weathered),
                new HeaterBlock(FabricBlockSettings.copyOf(FURNACE)));
        OXIDIZED_HEATER_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(oxidized),
                new HeaterBlock(FabricBlockSettings.copyOf(FURNACE)));

        WAXED_HEATER_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(waxed),
                new HeaterBlock(FabricBlockSettings.copyOf(FURNACE)));
        WAXED_EXPOSED_HEATER_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(waxed + exposed),
                new HeaterBlock(FabricBlockSettings.copyOf(FURNACE)));
        WAXED_WEATHERED_HEATER_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(waxed + weathered),
                new HeaterBlock(FabricBlockSettings.copyOf(FURNACE)));
        WAXED_OXIDIZED_HEATER_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(waxed + oxidized),
                new HeaterBlock(FabricBlockSettings.copyOf(FURNACE)));

        id = id.withPath(heatPipe);
        HEAT_PIPE_BLOCK = Registry.register(Registries.BLOCK, id,
                new HeatPipeBlock(FabricBlockSettings.copyOf(COPPER_BLOCK)));
        EXPOSED_HEAT_PIPE_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(exposed),
                new HeatPipeBlock(FabricBlockSettings.copyOf(COPPER_BLOCK)));
        WEATHERED_HEAT_PIPE_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(weathered),
                new HeatPipeBlock(FabricBlockSettings.copyOf(COPPER_BLOCK)));
        OXIDIZED_HEAT_PIPE_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(oxidized),
                new HeatPipeBlock(FabricBlockSettings.copyOf(COPPER_BLOCK)));

        WAXED_HEAT_PIPE_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(waxed),
                new HeatPipeBlock(FabricBlockSettings.copyOf(COPPER_BLOCK)));
        WAXED_EXPOSED_HEAT_PIPE_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(waxed + exposed),
                new HeatPipeBlock(FabricBlockSettings.copyOf(COPPER_BLOCK)));
        WAXED_WEATHERED_HEAT_PIPE_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(waxed + weathered),
                new HeatPipeBlock(FabricBlockSettings.copyOf(COPPER_BLOCK)));
        WAXED_OXIDIZED_HEAT_PIPE_BLOCK = Registry.register(Registries.BLOCK, id.withPrefixedPath(waxed + oxidized),
                new HeatPipeBlock(FabricBlockSettings.copyOf(COPPER_BLOCK)));

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
    }

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LOGGER.info("Initialize");

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL)
                .register(content -> content.addBefore(Items.FURNACE,
                        HEATER_ITEM, EXPOSED_HEATER_ITEM,
                        WEATHERED_HEATER_ITEM, OXIDIZED_HEATER_ITEM,
                        WAXED_HEATER_ITEM, WAXED_EXPOSED_HEATER_ITEM,
                        WAXED_WEATHERED_HEATER_ITEM, WAXED_OXIDIZED_HEATER_ITEM,
                        HEAT_PIPE_ITEM, EXPOSED_HEAT_PIPE_ITEM,
                        WEATHERED_HEAT_PIPE_ITEM, OXIDIZED_HEAT_PIPE_ITEM,
                        WAXED_HEAT_PIPE_ITEM, WAXED_EXPOSED_HEAT_PIPE_ITEM,
                        WAXED_WEATHERED_HEAT_PIPE_ITEM, WAXED_OXIDIZED_HEAT_PIPE_ITEM));
    }
}
