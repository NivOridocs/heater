package niv.heater;

import static net.minecraft.core.registries.BuiltInRegistries.BLOCK_ENTITY_TYPE;
import static net.minecraft.core.registries.BuiltInRegistries.CREATIVE_MODE_TAB;
import static net.minecraft.core.registries.BuiltInRegistries.MENU;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import niv.heater.adapter.FurnaceAdapter;
import niv.heater.block.HeatPipeBlock;
import niv.heater.block.HeaterBlock;
import niv.heater.block.ThermostatBlock;
import niv.heater.block.WeatheringHeatPipeBlock;
import niv.heater.block.WeatheringHeaterBlock;
import niv.heater.block.WeatheringThermostatBlock;
import niv.heater.block.entity.HeaterBlockEntity;
import niv.heater.screen.HeaterMenu;
import niv.heater.util.FurnacesBinder;
import niv.heater.util.WeatherStateExtra;

@SuppressWarnings("java:S2440")
public class Heater implements ModInitializer {

    public static final String MOD_ID = "heater";

    public static final String MOD_NAME = "Heater";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final String TAB_NAME = "creative.heater.tab";

    @Override
    @SuppressWarnings("java:S1192")
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LOGGER.info("Initialize");

        registerAll("heater",
                HeaterBlock.BLOCKS.get()::get, HeaterBlock.ITEMS.get()::get,
                WeatheringHeaterBlock.BLOCKS.get()::get, WeatheringHeaterBlock.ITEMS.get()::get);

        registerAll("heat_pipe",
                HeatPipeBlock.BLOCKS.get()::get, HeatPipeBlock.ITEMS.get()::get,
                WeatheringHeatPipeBlock.BLOCKS.get()::get, WeatheringHeatPipeBlock.ITEMS.get()::get);

        registerAll("thermostat",
                ThermostatBlock.BLOCKS.get()::get, ThermostatBlock.ITEMS.get()::get,
                WeatheringThermostatBlock.BLOCKS.get()::get, WeatheringThermostatBlock.ITEMS.get()::get);

        DynamicRegistries.register(FurnaceAdapter.REGISTRY, FurnaceAdapter.CODEC);

        var id = new ResourceLocation(MOD_ID, MOD_ID);

        Registry.register(BLOCK_ENTITY_TYPE, id, HeaterBlockEntity.TYPE);
        Registry.register(MENU, id, HeaterMenu.TYPE);
        Registry.register(CREATIVE_MODE_TAB, id.withPath("tab"), FabricItemGroup.builder()
                .icon(HeaterBlock.UNAFFECTED_ITEM::getDefaultInstance)
                .title(Component.translatable(TAB_NAME))
                .displayItems((parameters, output) -> {
                    WeatheringHeaterBlock.ITEMS.get().values().forEach(output::accept);
                    HeaterBlock.ITEMS.get().values().forEach(output::accept);
                    WeatheringThermostatBlock.ITEMS.get().values().forEach(output::accept);
                    ThermostatBlock.ITEMS.get().values().forEach(output::accept);
                    WeatheringHeatPipeBlock.ITEMS.get().values().forEach(output::accept);
                    HeatPipeBlock.ITEMS.get().values().forEach(output::accept);
                }).build());

        ItemStorage.SIDED.registerForBlockEntity(HeaterBlockEntity::getInventoryStorage, HeaterBlockEntity.TYPE);

        CommonLifecycleEvents.TAGS_LOADED.register(new FurnacesBinder());
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
