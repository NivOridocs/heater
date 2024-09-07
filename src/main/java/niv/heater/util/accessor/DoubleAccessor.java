package niv.heater.util.accessor;

import java.lang.reflect.Field;

import niv.heater.util.FieldExtra;

public class DoubleAccessor extends AbstractDoubleAccessor {
    public DoubleAccessor(Field field) {
        super(field);
    }

    @Override
    public double getDouble(Object target) {
        return FieldExtra.getNumber(Double.class, field, target);
    }

    @Override
    public void setDouble(Object target, double value) {
        FieldExtra.setNumber(field, target, value);
    }
}
