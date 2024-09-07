package niv.heater.util.accessor;

import java.lang.reflect.Field;

import niv.heater.util.FieldExtra;

public class PrimitiveLongAccessor extends AbstractLongAccessor {
    public PrimitiveLongAccessor(Field field) {
        super(field);
    }

    @Override
    public long getLong(Object target) {
        return FieldExtra.getLong(field, target);
    }

    @Override
    public void setLong(Object target, long value) {
        FieldExtra.setLong(field, target, value);
    }
}
