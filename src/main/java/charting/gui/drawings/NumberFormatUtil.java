package charting.gui.drawings;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class NumberFormatUtil {
    public static String format(double value, int precision, RoundingMode roundingMode) {
        return BigDecimal.valueOf(value).setScale(precision, roundingMode).stripTrailingZeros().toPlainString();
    }

    public static String format(double value, int precision) {
        return format(value, precision, RoundingMode.HALF_UP);
    }

    public static String format(double value) {
        return format(value, 2);
    }

    private NumberFormatUtil() {
    }
}
