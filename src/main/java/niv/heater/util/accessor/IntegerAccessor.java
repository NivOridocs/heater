package niv.heater.util.accessor;

import java.lang.reflect.Field;

import niv.heater.util.FieldExtra;

public class IntegerAccessor extends AbstractIntAccessor {
    public IntegerAccessor(Field field) {
        super(field);
    }

    @Override
    public int getInt(Object target) {
        return FieldExtra.getNumber(Integer.class, field, target);
    }

    @Override
    public void setInt(Object target, int value) {
        FieldExtra.setNumber(field, target, value);
    }
}
