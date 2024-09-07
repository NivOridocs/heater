package niv.heater.util;

import static niv.heater.util.NumberType.maxType;
import static niv.heater.util.NumberType.numberType;

import niv.heater.api.Furnace;

public final class FurnaceExtra {
    private FurnaceExtra() {
    }

    public static final int compare(Furnace a, Furnace b) {
        return compare(b.getComparable(), a.getComparable());
    }

    private static final int compare(Number a, Number b) {
        switch (maxType(numberType(a), numberType(b))) {
            case INT:
                return Integer.compare(a.intValue(), b.intValue());
            case LONG:
                return Long.compare(a.longValue(), b.longValue());
            case FLOAT:
                return Float.compare(a.floatValue(), b.floatValue());
            case DOUBLE:
                return Double.compare(a.doubleValue(), b.doubleValue());
            default:
                return 0;
        }
    }
}
