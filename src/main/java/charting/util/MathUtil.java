package charting.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MathUtil {
    private MathUtil() {
    }

    public static double getMin(double... values) {
        return getMin(false, values);
    }

    public static double getMinIgnoreNan(double... values) {
        return getMin(true, values);
    }

    private static double getMin(boolean ignoreNan, double... values) {
        if (values.length == 0) {
            return Double.NaN;
        }

        double min = Double.POSITIVE_INFINITY;

        for (double v : values) {
            if (ignoreNan) {
                if (!Double.isNaN(v)) {
                    min = Math.min(min, v);
                }
            } else {
                min = Math.min(min, v);
            }
        }

        return min;
    }

    public static double getMax(double... values) {
        return getMax(false, values);
    }

    public static double getMaxIgnoreNan(double... values) {
        return getMax(true, values);
    }

    private static double getMax(boolean ignoreNan, double... values) {
        if (values.length == 0) {
            return Double.NaN;
        }

        double max = Double.NEGATIVE_INFINITY;

        for (double v : values) {
            if (ignoreNan) {
                if (!Double.isNaN(v)) {
                    max = Math.max(max, v);
                }
            } else {
                max = Math.max(max, v);
            }
        }

        return max;
    }
}
