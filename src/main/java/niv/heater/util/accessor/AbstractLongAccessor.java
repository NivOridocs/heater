package niv.heater.util.accessor;

import java.lang.reflect.Field;

import niv.heater.util.NumberType;

public abstract class AbstractLongAccessor extends NumberAccessor {
    protected AbstractLongAccessor(Field field) {
        super(field);
    }

    @Override
    public NumberType getType() {
        return NumberType.LONG;
    }

    @Override
    public int getInt(Object target) {
        return (int) getLong(target);
    }

    @Override
    public float getFloat(Object target) {
        return getLong(target);
    }

    @Override
    public double getDouble(Object target) {
        return getLong(target);
    }

    @Override
    public void setInt(Object target, int value) {
        setLong(target, value);
    }

    @Override
    public void setFloat(Object target, float value) {
        setLong(target, (long) value);
    }

    @Override
    public void setDouble(Object target, double value) {
        setLong(target, (long) value);
    }
}
