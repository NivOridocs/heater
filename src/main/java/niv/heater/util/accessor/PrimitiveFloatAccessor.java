package niv.heater.util.accessor;

import java.lang.reflect.Field;

import niv.heater.util.FieldExtra;

public class PrimitiveFloatAccessor extends AbstractFloatAccessor {
    public PrimitiveFloatAccessor(Field field) {
        super(field);
    }

    @Override
    public float getFloat(Object target) {
        return FieldExtra.getFloat(field, target);
    }

    @Override
    public void setFloat(Object target, float value) {
        FieldExtra.setFloat(field, target, value);
    }
}
