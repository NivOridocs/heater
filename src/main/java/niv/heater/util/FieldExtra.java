package niv.heater.util;

import java.lang.reflect.Field;

@SuppressWarnings("java:S3011")
public final class FieldExtra {
    private FieldExtra() {
    }

    public static final <T extends Number> T getNumber(Class<T> type, Field field, Object target) {
        try {
            return type.cast(field.get(target));
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static final int getInt(Field field, Object target) {
        try {
            return field.getInt(target);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static final long getLong(Field field, Object target) {
        try {
            return field.getLong(target);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static final float getFloat(Field field, Object target) {
        try {
            return field.getFloat(target);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static final double getDouble(Field field, Object target) {
        try {
            return field.getDouble(target);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static final <T extends Number> void setNumber(Field field, Object target, T value) {
        try {
            field.set(target, value);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static final void setInt(Field field, Object target, int value) {
        try {
            field.setInt(target, value);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static final void setLong(Field field, Object target, long value) {
        try {
            field.setLong(target, value);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static final void setFloat(Field field, Object target, float value) {
        try {
            field.setFloat(target, value);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static final void setDouble(Field field, Object target, double value) {
        try {
            field.setDouble(target, value);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
