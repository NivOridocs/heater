package niv.heater.util;

import java.lang.reflect.Field;
import java.util.Optional;

import niv.heater.util.accessor.DoubleAccessor;
import niv.heater.util.accessor.FloatAccessor;
import niv.heater.util.accessor.IntegerAccessor;
import niv.heater.util.accessor.LongAccessor;
import niv.heater.util.accessor.NumberAccessor;
import niv.heater.util.accessor.PrimitiveDoubleAccessor;
import niv.heater.util.accessor.PrimitiveFloatAccessor;
import niv.heater.util.accessor.PrimitiveIntAccessor;
import niv.heater.util.accessor.PrimitiveLongAccessor;

public abstract class FurnaceField {

    protected final NumberAccessor accessor;

    protected FurnaceField(NumberAccessor accessor) {
        this.accessor = accessor;
    }

    public NumberAccessor getAccessor() {
        return accessor;
    }

    public abstract Number getValue(Object target);

    public abstract int compareValue(Object target, int value);

    public abstract void addValue(Object target, int value);

    public abstract void setValue(Object target, int value);

    public static NumberType maxType(FurnaceField a, FurnaceField b) {
        return NumberAccessor.maxType(a.accessor, b.accessor);
    }

    public static final Optional<FurnaceField> of(Field field) {
        return Optional.<FurnaceField>empty()
                .or(() -> IntField.tryBuild(field))
                .or(() -> LongField.tryBuild(field))
                .or(() -> FloatField.tryBuild(field))
                .or(() -> DoubleField.tryBuild(field));
    }

    private static final class IntField extends FurnaceField {
        protected IntField(NumberAccessor accessor) {
            super(accessor);
        }

        @Override
        public Number getValue(Object target) {
            return this.accessor.getInt(target);
        }

        @Override
        public int compareValue(Object target, int value) {
            return Integer.compare(this.accessor.getInt(target), value);
        }

        @Override
        public void addValue(Object target, int value) {
            this.accessor.setInt(target, this.accessor.getInt(target) + value);
        }

        @Override
        public void setValue(Object target, int value) {
            this.accessor.setInt(target, value);
        }

        private static final Optional<IntField> tryBuild(Field field) {
            if (int.class.equals(field.getType())) {
                return Optional.of(new IntField(new PrimitiveIntAccessor(field)));
            } else if (Integer.class.equals(field.getType())) {
                return Optional.of(new IntField(new IntegerAccessor(field)));
            } else {
                return Optional.empty();
            }
        }
    }

    private static final class LongField extends FurnaceField {
        protected LongField(NumberAccessor accessor) {
            super(accessor);
        }

        @Override
        public Number getValue(Object target) {
            return this.accessor.getLong(target);
        }

        @Override
        public int compareValue(Object target, int value) {
            return Long.compare(this.accessor.getLong(target), value);
        }

        @Override
        public void addValue(Object target, int value) {
            this.accessor.setLong(target, this.accessor.getLong(target) + value);
        }

        @Override
        public void setValue(Object target, int value) {
            this.accessor.setLong(target, value);
        }

        private static final Optional<LongField> tryBuild(Field field) {
            if (long.class.equals(field.getType())) {
                return Optional.of(new LongField(new PrimitiveLongAccessor(field)));
            } else if (Long.class.equals(field.getType())) {
                return Optional.of(new LongField(new LongAccessor(field)));
            } else {
                return Optional.empty();
            }
        }
    }

    private static final class FloatField extends FurnaceField {
        protected FloatField(NumberAccessor accessor) {
            super(accessor);
        }

        @Override
        public Number getValue(Object target) {
            return this.accessor.getFloat(target);
        }

        @Override
        public int compareValue(Object target, int value) {
            return Float.compare(this.accessor.getFloat(target), value);
        }

        @Override
        public void addValue(Object target, int value) {
            this.accessor.setFloat(target, this.accessor.getFloat(target) + value);
        }

        @Override
        public void setValue(Object target, int value) {
            this.accessor.setFloat(target, value);
        }

        private static final Optional<FloatField> tryBuild(Field field) {
            if (float.class.equals(field.getType())) {
                return Optional.of(new FloatField(new PrimitiveFloatAccessor(field)));
            } else if (Float.class.equals(field.getType())) {
                return Optional.of(new FloatField(new FloatAccessor(field)));
            } else {
                return Optional.empty();
            }
        }
    }

    private static final class DoubleField extends FurnaceField {
        protected DoubleField(NumberAccessor accessor) {
            super(accessor);
        }

        @Override
        public Number getValue(Object target) {
            return this.accessor.getDouble(target);
        }

        @Override
        public int compareValue(Object target, int value) {
            return Double.compare(this.accessor.getDouble(target), value);
        }

        @Override
        public void addValue(Object target, int value) {
            this.accessor.setDouble(target, this.accessor.getDouble(target) + value);
        }

        @Override
        public void setValue(Object target, int value) {
            this.accessor.setDouble(target, value);
        }

        private static final Optional<DoubleField> tryBuild(Field field) {
            if (double.class.equals(field.getType())) {
                return Optional.of(new DoubleField(new PrimitiveDoubleAccessor(field)));
            } else if (Double.class.equals(field.getType())) {
                return Optional.of(new DoubleField(new DoubleAccessor(field)));
            } else {
                return Optional.empty();
            }
        }
    }
}
