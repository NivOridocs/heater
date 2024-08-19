package niv.heater.util;

import java.lang.reflect.Field;
import java.util.Optional;

@SuppressWarnings("java:S3011")
public abstract class Accessor {
    protected final Field field;

    private Accessor(Field field) {
        this.field = field;
    }

    protected abstract int get(Object target) throws IllegalArgumentException, IllegalAccessException;

    protected abstract void set(Object target, int value) throws IllegalArgumentException, IllegalAccessException;

    public static final Optional<Accessor> of(Field field) {
        return Optional.<Accessor>empty()
                .or(() -> IntAccessor.tryOf(field))
                .or(() -> LongAccessor.tryOf(field))
                .or(() -> FloatAccessor.tryOf(field))
                .or(() -> DoubleAccessor.tryOf(field))
                .or(() -> NumberAccessor.tryOf(field));
    }

    private static final class IntAccessor extends Accessor {
        protected IntAccessor(Field field) {
            super(field);
        }

        @Override
        protected int get(Object target) throws IllegalArgumentException, IllegalAccessException {
            return this.field.getInt(target);
        }

        @Override
        protected void set(Object target, int value) throws IllegalArgumentException, IllegalAccessException {
            this.field.setInt(target, value);
        }

        private static final Optional<IntAccessor> tryOf(Field field) {
            if (int.class.equals(field.getType())) {
                return Optional.of(new IntAccessor(field));
            } else {
                return Optional.empty();
            }
        }
    }

    private static final class LongAccessor extends Accessor {
        protected LongAccessor(Field field) {
            super(field);
        }

        @Override
        protected int get(Object target) throws IllegalArgumentException, IllegalAccessException {
            return (int) this.field.getLong(target);
        }

        @Override
        protected void set(Object target, int value) throws IllegalArgumentException, IllegalAccessException {
            this.field.setLong(target, value);
        }

        private static final Optional<LongAccessor> tryOf(Field field) {
            if (long.class.equals(field.getType())) {
                return Optional.of(new LongAccessor(field));
            } else {
                return Optional.empty();
            }
        }
    }

    private static final class FloatAccessor extends Accessor {
        protected FloatAccessor(Field field) {
            super(field);
        }

        @Override
        protected int get(Object target) throws IllegalArgumentException, IllegalAccessException {
            return (int) this.field.getFloat(target);
        }

        @Override
        protected void set(Object target, int value) throws IllegalArgumentException, IllegalAccessException {
            this.field.setFloat(target, value);
        }

        private static final Optional<FloatAccessor> tryOf(Field field) {
            if (float.class.equals(field.getType())) {
                return Optional.of(new FloatAccessor(field));
            } else {
                return Optional.empty();
            }
        }
    }

    private static final class DoubleAccessor extends Accessor {
        protected DoubleAccessor(Field field) {
            super(field);
        }

        @Override
        protected int get(Object target) throws IllegalArgumentException, IllegalAccessException {
            return (int) this.field.getDouble(target);
        }

        @Override
        protected void set(Object target, int value) throws IllegalArgumentException, IllegalAccessException {
            this.field.setDouble(target, value);
        }

        private static final Optional<DoubleAccessor> tryOf(Field field) {
            if (double.class.equals(field.getType())) {
                return Optional.of(new DoubleAccessor(field));
            } else {
                return Optional.empty();
            }
        }
    }

    private static final class NumberAccessor<T extends Number> extends Accessor {

        private final Class<T> type;

        protected NumberAccessor(Field field, Class<T> type) {
            super(field);
            this.type = type;
        }

        @Override
        protected int get(Object target) throws IllegalArgumentException, IllegalAccessException {
            return type.cast(this.field.get(target)).intValue();
        }

        @Override
        protected void set(Object target, int value) throws IllegalArgumentException, IllegalAccessException {
            this.field.set(target, value);
        }

        @SuppressWarnings("java:S1452")
        private static final Optional<NumberAccessor<? extends Number>> tryOf(Field field) {
            return Optional.<NumberAccessor<? extends Number>>empty()
                    .or(() -> tryOf(field, Integer.class))
                    .or(() -> tryOf(field, Long.class))
                    .or(() -> tryOf(field, Float.class))
                    .or(() -> tryOf(field, Double.class));
        }

        private static final Optional<NumberAccessor<? extends Number>> tryOf(
                Field field, Class<? extends Number> type) {
            if (type.equals(field.getType())) {
                return Optional.of(new NumberAccessor<>(field, type));
            } else {
                return Optional.empty();
            }
        }
    }
}
