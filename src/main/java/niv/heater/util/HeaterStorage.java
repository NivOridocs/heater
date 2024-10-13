package niv.heater.util;

import niv.burning.api.base.SimpleBurningStorage;

public class HeaterStorage extends SimpleBurningStorage {

    public void setCurrentBurning(int value) {
        this.currentBurning = Math.max(0, Math.min(this.maxBurning, value));
    }

    public void setMaxBurning(int value) {
        this.maxBurning = Math.max(0, value);
    }
}
