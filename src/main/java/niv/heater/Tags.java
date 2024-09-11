package niv.heater;

import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class Tags {

    public static final TagKey<Block> HEATERS = createTag("heaters");
    public static final TagKey<Block> PIPES = createTag("pipes");
    public static final TagKey<Block> THERMOSTATS = createTag("thermostats");

    public static final class Connectable {

        public static final TagKey<Block> PIPES = createTag("connectable/pipes");

        public static final Supplier<ImmutableList<TagKey<Block>>> ALL = Suppliers
                .memoize(() -> ImmutableList.<TagKey<Block>>builderWithExpectedSize(1)
                        .add(Connectable.PIPES).build());

        private Connectable() {
        }
    }

    public static final class Propagable {

        public static final TagKey<Block> HEATERS = createTag("propagable/heaters");
        public static final TagKey<Block> PIPES = createTag("propagable/pipes");
        public static final TagKey<Block> THERMOSTATS = createTag("propagable/thermostats");

        public static final Supplier<ImmutableList<TagKey<Block>>> ALL = Suppliers
                .memoize(() -> ImmutableList.<TagKey<Block>>builderWithExpectedSize(3)
                        .add(Propagable.HEATERS).add(Propagable.PIPES).add(Propagable.THERMOSTATS).build());

        private Propagable() {
        }
    }

    private Tags() {
    }

    private static final TagKey<Block> createTag(String name) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.tryBuild(Heater.MOD_ID, name));
    }
}
