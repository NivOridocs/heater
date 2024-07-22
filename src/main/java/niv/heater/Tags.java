package niv.heater;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class Tags {

    public static final TagKey<Block> HEATERS = create("heaters");
    public static final TagKey<Block> PIPES = create("pipes");
    public static final TagKey<Block> THERMOSTATS = create("thermostats");

    public static final TagKey<Block> FURNACES = create("furnaces");

    public static final class Connectable {

        public static final TagKey<Block> PIPES = create("connectable/pipes");

        private Connectable() {
        }
    }

    public static final class Propagable {

        public static final TagKey<Block> HEATERS = create("propagable/heaters");
        public static final TagKey<Block> PIPES = create("propagable/pipes");
        public static final TagKey<Block> THERMOSTATS = create("propagable/thermostats");

        private Propagable() {
        }
    }

    private Tags() {
    }

    private static final TagKey<Block> create(String name) {
        return TagKey.create(Registries.BLOCK, new ResourceLocation(Heater.MOD_ID, name));
    }
}
