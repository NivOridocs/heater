package niv.heater.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import niv.heater.Heater;

public class HeaterTags {

    public static final TagKey<Block> HEATERS = TagKey.create(Registries.BLOCK,
            new ResourceLocation(Heater.MOD_ID, "heaters"));

    public static final TagKey<Block> HEAT_PIPES = TagKey.create(Registries.BLOCK,
            new ResourceLocation(Heater.MOD_ID, "heat_pipes"));

    public static final TagKey<Block> THERMOSTATS = TagKey.create(Registries.BLOCK,
            new ResourceLocation(Heater.MOD_ID, "thermostats"));

    private HeaterTags() {
    }

}
