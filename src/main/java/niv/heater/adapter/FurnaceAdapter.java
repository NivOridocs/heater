package niv.heater.adapter;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.getIfBlank;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.fabricmc.fabric.mixin.lookup.BlockEntityTypeAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import niv.heater.Heater;
import niv.heater.api.Furnace;
import niv.heater.util.Accessor;
import niv.heater.util.ForwardingFurnace;

public class FurnaceAdapter implements Predicate<BlockEntityType<?>>, Function<BlockEntity, Optional<Furnace>> {

    private static record Accessors(Accessor burnTime, Accessor fuelTime) {
    }

    public static final Codec<FurnaceAdapter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.BLOCK_ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter(r -> r.type),
            Codec.STRING.fieldOf("lit_time").forGetter(r -> r.litTime),
            Codec.STRING.fieldOf("lit_duration").forGetter(r -> r.litDuration))
            .apply(instance, FurnaceAdapter::new));

    public static final ResourceKey<Registry<FurnaceAdapter>> REGISTRY = ResourceKey
            .createRegistryKey(ResourceLocation.tryBuild(Heater.MOD_ID, "adapters/furnace"));

    private final BlockEntityType<?> type;

    private final String litTime;

    private final String litDuration;

    private final Optional<Accessors> accessors;

    public FurnaceAdapter(BlockEntityType<?> type, String litTime, String litDuration) {
        this.type = requireNonNull(type);
        this.litTime = requireNonNull(getIfBlank(litTime, () -> null));
        this.litDuration = requireNonNull(getIfBlank(litDuration, () -> null));

        this.accessors = genAccessors();
    }

    @SuppressWarnings("java:S1452")
    public BlockEntityType<?> getType() {
        return type;
    }

    @Override
    public boolean test(BlockEntityType<?> type) {
        return this.type == type;
    }

    @Override
    public Optional<Furnace> apply(BlockEntity entity) {
        return accessors.map(value -> new ForwardingFurnace(entity, value.burnTime(), value.fuelTime()));
    }

    private Optional<Accessors> genAccessors() {
        Class<?> clazz = ((BlockEntityTypeAccessor) this.type).getBlocks()
                .stream().findAny()
                .map(Block::defaultBlockState)
                .map(state -> this.type.create(BlockPos.ZERO, state).getClass())
                .orElse(null);
        if (clazz != null) {
            var litTimeField = Optional.ofNullable(FieldUtils
                    .getField(clazz, this.litTime, true))
                    .flatMap(Accessor::of);

            var litDurationField = Optional.ofNullable(FieldUtils
                    .getField(clazz, this.litDuration, true))
                    .flatMap(Accessor::of);

            if (litTimeField.isPresent() && litDurationField.isPresent()) {
                return Optional.of(new Accessors(litTimeField.get(), litDurationField.get()));
            }
        }
        return Optional.empty();
    }

    public static Stream<FurnaceAdapter> stream(LevelReader level) {
        return level == null ? Stream.empty()
                : level.registryAccess().registry(REGISTRY).stream()
                        .flatMap(Registry::stream);
    }

    public static Optional<FurnaceAdapter> of(LevelReader level, BlockEntityType<?> type) {
        return stream(level).filter(value -> value.test(type)).findFirst();
    }

    public static Optional<Furnace> of(LevelReader level, BlockEntity entity) {
        return of(level, entity == null ? null : entity.getType()).flatMap(value -> value.apply(entity));
    }

    public static boolean is(LevelReader level, BlockEntityType<?> type) {
        return stream(level).anyMatch(value -> value.test(type));
    }
}
