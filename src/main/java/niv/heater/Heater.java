package niv.heater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
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

    public static final Item HEATER_ITEM;

    public static final BlockEntityType<HeaterBlockEntity> HEATER_BLOCK_ENTITY;

    public static final ScreenHandlerType<HeaterScreenHandler> HEATER_SCREEN_HANDLER;

    public static final Block HEAT_PIPE_BLOCK;

    public static final Item HEAT_PIPE_ITEM;

    static {
        MOD_ID = "heater";
        var id = new Identifier(MOD_ID, "heater");
        HEATER_BLOCK = Registry.register(
                Registries.BLOCK, id,
                new HeaterBlock(FabricBlockSettings.copyOf(Blocks.FURNACE)));
        HEATER_ITEM = Registry.register(
                Registries.ITEM, id,
                new BlockItem(HEATER_BLOCK, new FabricItemSettings()));
        HEATER_BLOCK_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE, id,
                FabricBlockEntityTypeBuilder.create(HeaterBlockEntity::new, HEATER_BLOCK).build(null));
        HEATER_SCREEN_HANDLER = Registry.register(
                Registries.SCREEN_HANDLER, id,
                new ScreenHandlerType<>(HeaterScreenHandler::new, FeatureFlags.VANILLA_FEATURES));

        id = new Identifier(MOD_ID, "heat_pipe");
        HEAT_PIPE_BLOCK = Registry.register(
                Registries.BLOCK, id,
                new HeatPipeBlock(FabricBlockSettings.copyOf(Blocks.COPPER_BLOCK)));
        HEAT_PIPE_ITEM = Registry.register(
                Registries.ITEM, id,
                new BlockItem(HEAT_PIPE_BLOCK, new FabricItemSettings()));
    }

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LOGGER.info("Initialize");

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL)
                .register(content -> content.addBefore(Items.FURNACE, HEATER_ITEM, HEAT_PIPE_ITEM));
    }
}
