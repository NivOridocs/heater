package niv.heater.util.accessor;

import java.lang.reflect.Field;

import niv.heater.util.FieldExtra;

public class LongAccessor extends AbstractLongAccessor {
    public LongAccessor(Field field) {
        super(field);
    }

    @Override
    public long getLong(Object target) {
        return FieldExtra.getNumber(Long.class, field, target);
    }

    @Override
    public void setLong(Object target, long value) {
        FieldExtra.setNumber(field, target, value);
    }
}
