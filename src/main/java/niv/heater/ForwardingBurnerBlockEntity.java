package niv.heater;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import net.minecraft.block.entity.BlockEntity;

public class ForwardingBurnerBlockEntity implements BurnerBlockEntity {

    private static final Map<Class<? extends BlockEntity>, Optional<Function<? super BlockEntity, BurnerBlockEntity>>> CACHE = new HashMap<>();

    private final BlockEntity target;
    private final Field burnTime;
    private final Field fuelTime;

    public ForwardingBurnerBlockEntity(BlockEntity target, Field burnTime, Field fuelTime) {
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

    public static final <E extends BlockEntity> boolean isForwardable(E entity) {
        return get(entity).isPresent();
    }

    public static final <E extends BlockEntity> Optional<BurnerBlockEntity> getBurnerBlockEntity(E entity) {
        return get(entity).map(constructor -> constructor.apply(entity));
    }

    private static final <E extends BlockEntity> Optional<Function<? super BlockEntity, BurnerBlockEntity>> get(
            E entity) {
        return CACHE.computeIfAbsent(entity.getClass(), ForwardingBurnerBlockEntity::compute);
    }

    private static final Optional<Function<? super BlockEntity, BurnerBlockEntity>> compute(
            Class<?> clazz) {
        try {
            var burnTime = clazz.getDeclaredField("burnTime");
            var fuelTime = clazz.getDeclaredField("fuelTime");

            burnTime.setAccessible(true);
            fuelTime.setAccessible(true);

            return Optional.of(entry -> new ForwardingBurnerBlockEntity(entry, burnTime, fuelTime));
        } catch (NoSuchFieldException ex) {
            var superclazz = clazz.getSuperclass();
            if (superclazz != null && BlockEntity.class.isAssignableFrom(superclazz)) {
                return compute(superclazz);
            } else {
                return Optional.empty();
            }
        }
    }

}
