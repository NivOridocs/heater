package niv.heater.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import niv.heater.Heater;

public class HeaterTags {

    public static final TagKey<Block> HEATERS = create("heaters");
    public static final TagKey<Block> PIPES = create("pipes");
    public static final TagKey<Block> THERMOSTATS = create("thermostats");

    public static final TagKey<Block> FURNACES = create("furnaces");

    public static final class Connectable {

        public static final TagKey<Block> PIPES = create("connectable/pipes");

        private Connectable() {
        }

    }

    private HeaterTags() {
    }

    private static final TagKey<Block> create(String name) {
        return TagKey.create(Registries.BLOCK, new ResourceLocation(Heater.MOD_ID, name));
    }

}
