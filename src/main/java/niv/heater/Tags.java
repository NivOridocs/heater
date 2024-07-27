package niv.heater;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class Tags {

    public static final TagKey<Block> HEATERS = createTag("heaters");
    public static final TagKey<Block> PIPES = createTag("pipes");
    public static final TagKey<Block> THERMOSTATS = createTag("thermostats");

    public static final TagKey<Block> COMMUNITY_FURNACES = createCommonTag("furnaces");
    public static final TagKey<Block> COMMON_FURNACES = createCommonTag("player_workstations/furnaces");

    public static final class Connectable {

        public static final TagKey<Block> PIPES = createTag("connectable/pipes");

        private Connectable() {
        }
    }

    public static final class Propagable {

        public static final TagKey<Block> HEATERS = createTag("propagable/heaters");
        public static final TagKey<Block> PIPES = createTag("propagable/pipes");
        public static final TagKey<Block> THERMOSTATS = createTag("propagable/thermostats");

        private Propagable() {
        }
    }

    private Tags() {
    }

    private static final TagKey<Block> createTag(String name) {
        return TagKey.create(Registries.BLOCK, new ResourceLocation(Heater.MOD_ID, name));
    }

    private static final TagKey<Block> createCommonTag(String name) {
        return TagKey.create(Registries.BLOCK, new ResourceLocation("c", name));
    }
}
