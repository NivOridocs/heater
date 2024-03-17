package niv.heater.block.entity;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import net.minecraft.world.level.block.entity.BlockEntity;

@SuppressWarnings("java:S3011")
public class ForwardingHeatSink implements HeatSink {

    private final BlockEntity target;
    private final Field burnTime;
    private final Field fuelTime;

    public ForwardingHeatSink(BlockEntity target, Field burnTime, Field fuelTime) {
        this.target = Objects.requireNonNull(target);
        this.burnTime = Objects.requireNonNull(burnTime);
        this.fuelTime = Objects.requireNonNull(fuelTime);
    }

    @Override
    public int getBurnTime() {
        try {
            return burnTime.getInt(target);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void setBurnTime(int value) {
        try {
            burnTime.setInt(target, value);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public int getFuelTime() {
        try {
            return fuelTime.getInt(target);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void setFuelTime(int value) {
        try {
            fuelTime.setInt(target, value);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static final boolean isForwardable(BlockEntity entity) {
        return get(entity.getClass()).isPresent();
    }

    public static final Optional<HeatSink> getHeatSink(BlockEntity entity) {
        return get(entity.getClass()).map(constructor -> constructor.apply(entity));
    }

    private static final Optional<Function<? super BlockEntity, HeatSink>> get(Class<?> clazz) {
        while (clazz != null
                && BlockEntity.class.isAssignableFrom(clazz)
                && !clazz.getName().startsWith("net.minecraft")) {
            try {
                var burnTime = clazz.getDeclaredField("burnTime");
                var fuelTime = clazz.getDeclaredField("fuelTime");

                burnTime.setAccessible(true);
                fuelTime.setAccessible(true);

                return Optional.of(entry -> new ForwardingHeatSink(entry, burnTime, fuelTime));
            } catch (NoSuchFieldException ex) {
                clazz = clazz.getSuperclass();
            }
        }
        return Optional.empty();
    }

}
