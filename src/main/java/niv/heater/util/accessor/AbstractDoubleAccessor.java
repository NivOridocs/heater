package niv.heater.util.accessor;

import java.lang.reflect.Field;

import niv.heater.util.NumberType;

public abstract class AbstractDoubleAccessor extends NumberAccessor {
    protected AbstractDoubleAccessor(Field field) {
        super(field);
    }

    @Override
    public NumberType getType() {
        return NumberType.DOUBLE;
    }

    @Override
    public int getInt(Object target) {
        return (int) getDouble(target);
    }

    @Override
    public long getLong(Object target) {
        return (long) getDouble(target);
    }

    @Override
    public float getFloat(Object target) {
        return (float) getDouble(target);
    }

    @Override
    public void setInt(Object target, int value) {
        setDouble(target, value);
    }

    @Override
    public void setLong(Object target, long value) {
        setDouble(target, value);
    }

    @Override
    public void setFloat(Object target, float value) {
        setDouble(target, value);
    }
}
