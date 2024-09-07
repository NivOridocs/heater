package niv.heater.util;

import java.util.Objects;
import java.util.function.IntUnaryOperator;

import net.minecraft.world.level.block.entity.BlockEntity;
import niv.heater.api.Furnace;

public class ForwardingFurnace implements Furnace {
    private final BlockEntity target;
    private final FurnaceField burnTime;
    private final FurnaceField fuelTime;
    private final IntUnaryOperator deltaTimeComparator;

    public ForwardingFurnace(BlockEntity target, FurnaceField burnTime, FurnaceField fuelTime) {
        this.target = Objects.requireNonNull(target);
        this.burnTime = Objects.requireNonNull(burnTime);
        this.fuelTime = Objects.requireNonNull(fuelTime);

        this.deltaTimeComparator = getDeltaTimeComparator();
    }

    @Override
    public boolean isBurning() {
        return this.burnTime.compareValue(target, 0) > 0;
    }

    @Override
    public void addBurnTime(int value) {
        this.burnTime.addValue(target, value);
    }

    @Override
    public void setFuelTime(int value) {
        this.fuelTime.setValue(target, value);
    }

    @Override
    public int compareFuelTime(int value) {
        return this.fuelTime.compareValue(target, value);
    }

    @Override
    public int compareDeltaTime(int value) {
        return deltaTimeComparator.applyAsInt(value);
    }

    @Override
    public Number getComparable() {
        return this.burnTime.getValue(target);
    }

    private IntUnaryOperator getDeltaTimeComparator() {
        switch (FurnaceField.maxType(this.burnTime, this.fuelTime)) {
            case INT:
                return this::compareIntDeltaTime;
            case LONG:
                return this::compareLongDeltaTime;
            case FLOAT:
                return this::compareFloatDeltaTime;
            case DOUBLE:
                return this::compareDoubleDeltaTime;
            default:
                return null;
        }
    }

    private int compareIntDeltaTime(int value) {
        return Integer.compare(
                this.fuelTime.getAccessor().getInt(target) - this.burnTime.getAccessor().getInt(target), value);
    }

    private int compareLongDeltaTime(int value) {
        return Long.compare(
                this.fuelTime.getAccessor().getLong(target) - this.burnTime.getAccessor().getLong(target), value);
    }

    private int compareFloatDeltaTime(int value) {
        return Float.compare(
                this.fuelTime.getAccessor().getFloat(target) - this.burnTime.getAccessor().getFloat(target), value);
    }

    private int compareDoubleDeltaTime(int value) {
        return Double.compare(
                this.fuelTime.getAccessor().getDouble(target) - this.burnTime.getAccessor().getDouble(target), value);
    }
}
