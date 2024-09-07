package niv.heater.util;

public enum NumberType {
    INT, LONG, FLOAT, DOUBLE;

    public static final NumberType maxType(NumberType a, NumberType b) {
        return a.ordinal() >= b.ordinal() ? a : b;
    }

    public static final NumberType numberType(Number number) {
        if (number instanceof Integer) {
            return INT;
        } else if (number instanceof Long) {
            return LONG;
        } else if (number instanceof Float) {
            return FLOAT;
        } else if (number instanceof Double) {
            return DOUBLE;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
