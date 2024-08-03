package niv.heater.api;

public interface Furnace {

    int getBurnTime();

    void setBurnTime(int value);

    int getFuelTime();

    void setFuelTime(int value);

    static int compare(Furnace f1, Furnace f2) {
        return Integer.compare(f2.getBurnTime(), f1.getBurnTime());
    }
}
