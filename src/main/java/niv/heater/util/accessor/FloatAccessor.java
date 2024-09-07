package niv.heater.util.accessor;

import java.lang.reflect.Field;

import niv.heater.util.FieldExtra;

public class FloatAccessor extends AbstractFloatAccessor {
    public FloatAccessor(Field field) {
        super(field);
    }

    @Override
    public float getFloat(Object target) {
        return FieldExtra.getNumber(Float.class, field, target);
    }

    @Override
    public void setFloat(Object target, float value) {
        FieldExtra.setNumber(field, target, value);
    }
}
