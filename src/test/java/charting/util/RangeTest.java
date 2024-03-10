package charting.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RangeTest {
    @Test
    void getLength() {
        assertEquals(10, new Range(0, 10).getLength());
        assertEquals(-10, new Range(10, 0).getLength());
    }

    @Test
    void getCenter() {
        assertEquals(5, new Range(0, 10).getCenter());
        assertEquals(5, new Range(10, 0).getCenter());
    }

    @Test
    void scaled() {
        assertEquals(new Range(-5, 15), new Range(0, 10).scaled(2, 5));
        assertEquals(new Range(0, 5), new Range(0, 10).scaled(0.5, 0));
    }

    @Test
    void translated() {
        assertEquals(new Range(5, 15), new Range(0, 10).translated(5));
        assertEquals(new Range(-5, 5), new Range(0, 10).translated(-5));
    }

    @Test
    void toTargetPosition() {
        Range source = new Range(0,100);

        Range target = new Range(100, 300);
        assertEquals(200, source.toTargetPosition(50, target));

        assertEquals(Double.NaN, source.toTargetPosition(0, null));
    }

    @Test
    void toTargetLength() {
        Range source = new Range(0, 100);

        Range target = new Range(100, 300);
        assertEquals(100, source.toTargetLength(50, target));

        assertEquals(Double.NaN, source.toTargetLength(0, null));
    }

    @Test
    void equals() {
        assertEquals(Range.NAN, new Range(Double.NaN, Double.NaN));
    }
}
