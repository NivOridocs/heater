package niv.heater;

import static net.minecraft.core.registries.BuiltInRegistries.BLOCK_ENTITY_TYPE;
import static net.minecraft.core.registries.BuiltInRegistries.CREATIVE_MODE_TAB;
import static net.minecraft.core.registries.BuiltInRegistries.MENU;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import niv.heater.adapter.HeatSinkAdapter;
import niv.heater.block.entity.HeaterBlockEntity;
import niv.heater.registry.RegisterBlocks;
import niv.heater.screen.HeaterMenu;

@SuppressWarnings("java:S2440")
public class Heater implements ModInitializer {

    public static final String MOD_ID = "heater";

    public static final String MOD_NAME = "Heater";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LOGGER.info("Initialize");

        RegisterBlocks.register();

        DynamicRegistries.register(HeatSinkAdapter.REGISTRY, HeatSinkAdapter.CODEC);

        var id = new ResourceLocation(MOD_ID, MOD_ID);

        Registry.register(BLOCK_ENTITY_TYPE, id, HeaterBlockEntity.TYPE);
        Registry.register(MENU, id, HeaterMenu.TYPE);
        Registry.register(CREATIVE_MODE_TAB, id.withPath("tab"), HeaterMenu.TAB);
    }
}
