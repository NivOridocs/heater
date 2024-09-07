package niv.heater.api;

public interface Furnace {

    boolean isBurning();

    void addBurnTime(int value);

    void setFuelTime(int value);

    int compareFuelTime(int value);

    int compareDeltaTime(int value);

    Number getComparable();
}
