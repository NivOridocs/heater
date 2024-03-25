package niv.heater.adapter;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.getIfBlank;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import niv.heater.Heater;
import niv.heater.util.ForwardingHeatSink;
import niv.heater.util.HeatSink;

public class HeatSinkAdapter implements Predicate<BlockEntityType<?>>, Function<BlockEntity, Optional<HeatSink>> {

    public static final Codec<HeatSinkAdapter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.BLOCK_ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter(r -> r.type),
            Codec.STRING.fieldOf("lit_time").forGetter(r -> r.litTime),
            Codec.STRING.fieldOf("lit_duration").forGetter(r -> r.litDuration))
            .apply(instance, HeatSinkAdapter::new));

    private final BlockEntityType<?> type;

    private final String litTime;

    private final String litDuration;

    public HeatSinkAdapter(BlockEntityType<?> type, String litTime, String litDuration) {
        this.type = requireNonNull(type);
        this.litTime = requireNonNull(getIfBlank(litTime, () -> null));
        this.litDuration = requireNonNull(getIfBlank(litDuration, () -> null));
    }

    @Override
    public boolean test(BlockEntityType<?> type) {
        return this.type == type;
    }

    @Override
    public Optional<HeatSink> apply(BlockEntity entity) {
        return get(entity.getClass()).map(constructor -> constructor.apply(entity));
    }

    @SuppressWarnings("java:S3011")
    private Optional<Function<? super BlockEntity, HeatSink>> get(Class<?> clazz) {
        while (clazz != null
                && BlockEntity.class.isAssignableFrom(clazz)
                && !clazz.getName().startsWith("net.minecraft")) {
            try {
                var litTimeField = clazz.getDeclaredField(this.litTime);
                var litDurationField = clazz.getDeclaredField(this.litDuration);

                litTimeField.setAccessible(true);
                litDurationField.setAccessible(true);

                return Optional.of(entry -> new ForwardingHeatSink(entry, litTimeField, litDurationField));
            } catch (NoSuchFieldException ex) {
                clazz = clazz.getSuperclass();
            }
        }
        return Optional.empty();
    }

    public static Optional<HeatSinkAdapter> of(LevelAccessor levelAccessor, BlockEntityType<?> type) {
        if (levelAccessor instanceof Level level) {
            return level.registryAccess()
                    .registryOrThrow(Heater.HEAT_SINK_ADAPTER).stream()
                    .filter(adapter -> adapter.test(type)).findFirst();
        } else {
            return Optional.empty();
        }
    }
}
