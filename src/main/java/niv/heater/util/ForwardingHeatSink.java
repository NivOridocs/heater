package niv.heater.util;

import java.lang.reflect.Field;
import java.util.Objects;

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
}
