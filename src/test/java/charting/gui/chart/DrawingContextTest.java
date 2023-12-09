package charting.gui.chart;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DrawingContextTest {
    private DrawingContext context;

    @BeforeEach
    void beforeEach() {
        Bounds b = new BoundingBox(0, 0, 100, 100);
        context = new DrawingContext(b, 200, 200);
    }

    @Test
    void toViewportX() {
        assertEquals(15, context.toViewportX(30));
    }

    @Test
    void toViewportY() {
        assertEquals(85, context.toViewportY(30));
    }

    @Test
    void toViewportWidth() {
        assertEquals(15, context.toViewportWidth(30));
    }

    @Test
    void toViewportHeight() {
        assertEquals(-15, context.toViewportHeight(30));
    }

    @Test
    void toCanvasX() {
        assertEquals(60, context.toCanvasX(30));
    }

    @Test
    void toCanvasY() {
        assertEquals(140, context.toCanvasY(30));
    }

    @Test
    void toCanvasWidth() {
        assertEquals(60, context.toCanvasWidth(30));
    }

    @Test
    void toCanvasHeight() {
        assertEquals(-60, context.toCanvasHeight(30));
    }
}
