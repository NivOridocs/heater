package niv.heater.adapter;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.getIfBlank;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import niv.heater.Heater;
import niv.heater.api.Furnace;
import niv.heater.util.ForwardingFurnace;

public class FurnaceAdapter implements Predicate<BlockEntityType<?>>, Function<BlockEntity, Optional<Furnace>> {

    public static final Codec<FurnaceAdapter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.BLOCK_ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter(r -> r.type),
            Codec.STRING.fieldOf("lit_time").forGetter(r -> r.litTime),
            Codec.STRING.fieldOf("lit_duration").forGetter(r -> r.litDuration))
            .apply(instance, FurnaceAdapter::new));

    public static final ResourceKey<Registry<FurnaceAdapter>> REGISTRY = ResourceKey
            .createRegistryKey(new ResourceLocation(Heater.MOD_ID, "adapters/furnace"));

    private final BlockEntityType<?> type;

    private final String litTime;

    private final String litDuration;

    public FurnaceAdapter(BlockEntityType<?> type, String litTime, String litDuration) {
        this.type = requireNonNull(type);
        this.litTime = requireNonNull(getIfBlank(litTime, () -> null));
        this.litDuration = requireNonNull(getIfBlank(litDuration, () -> null));
    }

    @Override
    public boolean test(BlockEntityType<?> type) {
        return this.type == type;
    }

    @Override
    public Optional<Furnace> apply(BlockEntity entity) {
        return get(entity.getClass()).map(constructor -> constructor.apply(entity));
    }

    @SuppressWarnings("java:S3011")
    private Optional<Function<? super BlockEntity, Furnace>> get(Class<?> clazz) {
        while (clazz != null
                && BlockEntity.class.isAssignableFrom(clazz)
                && !clazz.getName().startsWith("net.minecraft")) {
            try {
                var litTimeField = clazz.getDeclaredField(this.litTime);
                var litDurationField = clazz.getDeclaredField(this.litDuration);

                litTimeField.setAccessible(true);
                litDurationField.setAccessible(true);

                return Optional.of(entry -> new ForwardingFurnace(entry, litTimeField, litDurationField));
            } catch (NoSuchFieldException ex) {
                clazz = clazz.getSuperclass();
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
}
