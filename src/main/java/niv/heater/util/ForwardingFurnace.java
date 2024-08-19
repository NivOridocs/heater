package niv.heater.util;

import java.util.Objects;

import net.minecraft.world.level.block.entity.BlockEntity;
import niv.heater.api.Furnace;

public class ForwardingFurnace implements Furnace {
    private final BlockEntity target;
    private final Accessor burnTime;
    private final Accessor fuelTime;

    public ForwardingFurnace(BlockEntity target, Accessor burnTime, Accessor fuelTime) {
        this.target = Objects.requireNonNull(target);
        this.burnTime = Objects.requireNonNull(burnTime);
        this.fuelTime = Objects.requireNonNull(fuelTime);
    }

    @Override
    public int getBurnTime() {
        try {
            return burnTime.get(target);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void setBurnTime(int value) {
        try {
            burnTime.set(target, value);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public int getFuelTime() {
        try {
            return fuelTime.get(target);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void setFuelTime(int value) {
        try {
            fuelTime.set(target, value);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
