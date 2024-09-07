package niv.heater.util.accessor;

import java.lang.reflect.Field;

import niv.heater.util.NumberType;

public abstract class NumberAccessor {
    protected final Field field;

    protected NumberAccessor(Field field) {
        this.field = field;
    }

    public abstract NumberType getType();

    public abstract int getInt(Object target);

    public abstract long getLong(Object target);

    public abstract float getFloat(Object target);

    public abstract double getDouble(Object target);

    public abstract void setInt(Object target, int value);

    public abstract void setLong(Object target, long value);

    public abstract void setFloat(Object target, float value);

    public abstract void setDouble(Object target, double value);

    public static NumberType maxType(NumberAccessor a, NumberAccessor b) {
        return NumberType.maxType(a.getType(), b.getType());
    }
}
