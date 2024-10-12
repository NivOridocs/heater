package niv.heater;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class Tags {

    public static final TagKey<Block> HEATERS = createTag("heaters");
    public static final TagKey<Block> PIPES = createTag("pipes");
    public static final TagKey<Block> THERMOSTATS = createTag("thermostats");

    private Tags() {
    }

    private static final TagKey<Block> createTag(String name) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.tryBuild(Heater.MOD_ID, name));
    }
}
