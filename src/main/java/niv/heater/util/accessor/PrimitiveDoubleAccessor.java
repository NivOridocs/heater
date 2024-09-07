package niv.heater.util.accessor;

import java.lang.reflect.Field;

import niv.heater.util.FieldExtra;

public class PrimitiveDoubleAccessor extends AbstractDoubleAccessor {
    public PrimitiveDoubleAccessor(Field field) {
        super(field);
    }

    @Override
    public double getDouble(Object target) {
        return FieldExtra.getDouble(field, target);
    }

    @Override
    public void setDouble(Object target, double value) {
        FieldExtra.setDouble(field, target, value);
    }
}
