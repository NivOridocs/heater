package niv.heater.registry;

import static niv.heater.Heater.MOD_ID;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;

public class HeaterTabs {
    private HeaterTabs() {
    }

    public static final String TAB_NAME;

    public static final CreativeModeTab HEATER;

    static {
        TAB_NAME = "creative.heater.tab";

        HEATER = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ResourceLocation.tryBuild(MOD_ID, "tab"),
                FabricItemGroup.builder()
                        .icon(HeaterBlocks.WAXED_HEATER.asItem()::getDefaultInstance)
                        .title(Component.translatable(TAB_NAME))
                        .displayItems((parameters, output) -> {
                            HeaterBlocks.HEATERS.values().stream().map(Block::asItem).forEach(output::accept);
                            HeaterBlocks.WAXED_HEATERS.values().stream().map(Block::asItem).forEach(output::accept);
                            HeaterBlocks.THERMOSTATS.values().stream().map(Block::asItem).forEach(output::accept);
                            HeaterBlocks.WAXED_THERMOSTATS.values().stream().map(Block::asItem).forEach(output::accept);
                            HeaterBlocks.HEAT_PIPES.values().stream().map(Block::asItem).forEach(output::accept);
                            HeaterBlocks.WAXED_HEAT_PIPES.values().stream().map(Block::asItem).forEach(output::accept);
                        }).build());
    }

    public static final void initialize() {
        // Trigger static initialization
    }
}
