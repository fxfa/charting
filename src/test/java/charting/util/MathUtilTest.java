package charting.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MathUtilTest {
    @Test
    void getMin() {
        assertEquals(Double.NaN, MathUtil.getMin());
        assertEquals(-1, MathUtil.getMin(-1, 0, 1));
        assertEquals(Double.NaN, MathUtil.getMin(Double.NEGATIVE_INFINITY, Double.NaN));
    }

    @Test
    void getMinIgnoreNan() {
        assertEquals(Double.NaN, MathUtil.getMinIgnoreNan());
        assertEquals(-1, MathUtil.getMinIgnoreNan(-1, 0, 1, Double.NaN));
    }

    @Test
    void getMax() {
        assertEquals(Double.NaN, MathUtil.getMax());
        assertEquals(1, MathUtil.getMax(-1, 0, 1));
        assertEquals(Double.NaN, MathUtil.getMax(Double.POSITIVE_INFINITY, Double.NaN));
    }

    @Test
    void getMaxIgnoreNan() {
        assertEquals(Double.NaN, MathUtil.getMaxIgnoreNan());
        assertEquals(1, MathUtil.getMaxIgnoreNan(-1, 0, 1, Double.NaN));
    }
}
