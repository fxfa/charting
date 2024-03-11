package charting.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Range2DTest {
    @Test
    void getWidth() {
        assertEquals(10, new Range2D(0, 0, 10, 10).getWidth());
        assertEquals(-10, new Range2D(10, 10, 0, 0).getWidth());
    }

    @Test
    void getHeight() {
        assertEquals(10, new Range2D(0, 0, 10, 10).getHeight());
        assertEquals(-10, new Range2D(10, 10, 0, 0).getHeight());
    }

    @Test
    void getCenterX() {
        assertEquals(5, new Range2D(0, 0, 10, 10).getCenterX());
        assertEquals(5, new Range2D(10, 10, 0, 0).getCenterX());
    }

    @Test
    void getCenterY() {
        assertEquals(5, new Range2D(0, 0, 10, 10).getCenterY());
        assertEquals(5, new Range2D(10, 10, 0, 0).getCenterY());
    }

    @Test
    void scaled() {
        assertEquals(new Range2D(-5, -5, 15, 15),
                new Range2D(0, 0, 10, 10).scaled(2, 2));
        assertEquals(new Range2D(0, 0, 5, 5),
                new Range2D(0, 0, 10, 10).scaled(0.5, 0.5, 0, 0));
    }

    @Test
    void translated() {
        assertEquals(new Range2D(5, 5, 15, 15),
                new Range2D(0, 0, 10, 10).translated(5, 5));
        assertEquals(new Range2D(-5, -5, 5, 5),
                new Range2D(0, 0, 10, 10).translated(-5, -5));
    }

    @Test
    void toTargetX() {
        Range2D source = new Range2D(0, 0, 100, 100);

        Range2D target = new Range2D(100, 0, 300, 100);
        assertEquals(200, source.toTargetX(50, target));

        assertEquals(Double.NaN, source.toTargetX(0, null));
    }

    @Test
    void toTargetY() {
        Range2D source = new Range2D(0, 0, 100, 100);

        Range2D target = new Range2D(0, 100, 100, 300);
        assertEquals(200, source.toTargetY(50, target));

        assertEquals(Double.NaN, source.toTargetY(0, null));
    }

    @Test
    void toTargetWidth() {
        Range2D source = new Range2D(0, 0, 100, 100);

        Range2D target = new Range2D(100, 0, 300, 100);
        assertEquals(100, source.toTargetWidth(50, target));

        assertEquals(Double.NaN, source.toTargetWidth(0, null));
    }

    @Test
    void toTargetHeight() {
        Range2D source = new Range2D(0, 0, 100, 100);

        Range2D target = new Range2D(0, 100, 100, 300);
        assertEquals(100, source.toTargetHeight(50, target));

        assertEquals(Double.NaN, source.toTargetHeight(0, null));
    }

    @Test
    void equals() {
        assertEquals(Range2D.NAN, new Range2D(Double.NaN, Double.NaN, Double.NaN, Double.NaN));
    }
}
