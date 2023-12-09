package charting.gui.chart;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ViewportUtilTest {
    @Test
    void toTargetX() {
        Bounds source = new BoundingBox(0, 0, 100, 100);
        Bounds target = new BoundingBox(100, 0, 200, 100);

        assertEquals(200, ViewportUtil.toTargetX(source, 50, target));
    }

    @Test
    void toTargetY() {
        Bounds source = new BoundingBox(0, 0, 100, 100);
        Bounds target = new BoundingBox(0, 100, 100, 200);

        assertEquals(200, ViewportUtil.toTargetY(source, 50, target));
    }

    @Test
    void toTargetWidth() {
        Bounds source = new BoundingBox(0, 0, 100, 100);
        Bounds target = new BoundingBox(100, 0, 200, 100);

        assertEquals(100, ViewportUtil.toTargetWidth(source, 50, target));
    }

    @Test
    void toTargetHeight() {
        Bounds source = new BoundingBox(0, 0, 100, 100);
        Bounds target = new BoundingBox(0, 100, 100, 200);

        assertEquals(100, ViewportUtil.toTargetHeight(source, 50, target));
    }

    @Test
    void returnsNanOnNullBounds() {
        Bounds b = new BoundingBox(0, 0, 100, 100);

        assertEquals(Double.NaN, ViewportUtil.toTargetX(null, 0, b));
        assertEquals(Double.NaN, ViewportUtil.toTargetX(b, 0, null));
        assertEquals(Double.NaN, ViewportUtil.toTargetY(null, 0, b));
        assertEquals(Double.NaN, ViewportUtil.toTargetY(b, 0, null));
        assertEquals(Double.NaN, ViewportUtil.toTargetWidth(null, 0, b));
        assertEquals(Double.NaN, ViewportUtil.toTargetWidth(b, 0, null));
        assertEquals(Double.NaN, ViewportUtil.toTargetHeight(null, 0, b));
        assertEquals(Double.NaN, ViewportUtil.toTargetHeight(b, 0, null));
    }
}
