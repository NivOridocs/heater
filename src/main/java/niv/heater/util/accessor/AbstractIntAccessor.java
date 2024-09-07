package niv.heater.util.accessor;

import java.lang.reflect.Field;

import niv.heater.util.NumberType;

public abstract class AbstractIntAccessor extends NumberAccessor {
    protected AbstractIntAccessor(Field field) {
        super(field);
    }

    @Override
    public NumberType getType() {
        return NumberType.INT;
    }

    @Override
    public long getLong(Object target) {
        return getInt(target);
    }

    @Override
    public float getFloat(Object target) {
        return getInt(target);
    }

    @Override
    public double getDouble(Object target) {
        return getInt(target);
    }

    @Override
    public void setLong(Object target, long value) {
        setInt(target, (int) value);
    }

    @Override
    public void setFloat(Object target, float value) {
        setInt(target, (int) value);
    }

    @Override
    public void setDouble(Object target, double value) {
        setInt(target, (int) value);
    }
}
