package niv.heater.util.accessor;

import java.lang.reflect.Field;

import niv.heater.util.FieldExtra;

public class PrimitiveIntAccessor extends AbstractIntAccessor {
    public PrimitiveIntAccessor(Field field) {
        super(field);
    }

    @Override
    public int getInt(Object target) {
        return FieldExtra.getInt(field, target);
    }

    @Override
    public void setInt(Object target, int value) {
        FieldExtra.setInt(field, target, value);
    }
}
