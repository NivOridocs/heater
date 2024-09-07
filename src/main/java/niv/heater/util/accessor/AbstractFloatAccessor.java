package niv.heater.util.accessor;

import java.lang.reflect.Field;

import niv.heater.util.NumberType;

public abstract class AbstractFloatAccessor extends NumberAccessor {
    protected AbstractFloatAccessor(Field field) {
        super(field);
    }

    @Override
    public NumberType getType() {
        return NumberType.FLOAT;
    }

    @Override
    public int getInt(Object target) {
        return (int) getFloat(target);
    }

    @Override
    public long getLong(Object target) {
        return (long) getFloat(target);
    }

    @Override
    public double getDouble(Object target) {
        return getFloat(target);
    }

    @Override
    public void setInt(Object target, int value) {
        setFloat(target, value);
    }

    @Override
    public void setLong(Object target, long value) {
        setFloat(target, value);
    }

    @Override
    public void setDouble(Object target, double value) {
        setFloat(target, (float) value);
    }
}
